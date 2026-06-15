package uz.tayanch.app

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import uz.tayanch.app.core.LocaleUtil
import uz.tayanch.app.data.security.RootDetector
import uz.tayanch.app.data.security.SecureSessionStore
import uz.tayanch.app.data.security.SessionManager
import uz.tayanch.app.ui.navigation.Routes
import uz.tayanch.app.ui.navigation.TayanchNavGraph
import uz.tayanch.app.ui.security.RootBlockedScreen
import uz.tayanch.app.ui.theme.TayanchTheme

/**
 * Single-activity host. It extends [FragmentActivity] so the real Android
 * BiometricPrompt can attach to it. The launch destination is decided from the
 * encrypted session store, so a returning, onboarded user lands straight on the
 * hub while a fresh install starts at the auth gateway.
 */
class MainActivity : FragmentActivity() {

    private val store: SecureSessionStore by inject()
    private val sessions: SessionManager by inject()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleUtil.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Fully transparent system bars (no contrast scrim) so the OS status &
        // navigation bars show the app's own colors — otherwise edge-to-edge adds a
        // white scrim to the nav bar (in 3-button mode) that mismatches the app's
        // bottom NavigationBar.
        val transparent = android.graphics.Color.TRANSPARENT
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(transparent, transparent),
            navigationBarStyle = SystemBarStyle.auto(transparent, transparent),
        )
        // Stop the system from layering its own translucent contrast scrim behind
        // the (3-button) nav bar — that white scrim is what made the OS nav bar a
        // different shade than the app's bottom NavigationBar.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        super.onCreate(savedInstanceState)

        // Pillar 29 — runtime integrity: a rooted device weakens the Keystore/TEE
        // and FLAG_SECURE guarantees, so block the app entirely (release builds only;
        // RootDetector returns false in debug). Terminal — no further UI is wired.
        if (RootDetector.isCompromised(this)) {
            setContent { TayanchTheme { RootBlockedScreen() } }
            return
        }

        // Pillar 15 — if the backend evicts this session (login elsewhere) the
        // network layer clears the store and emits here; bounce to the gateway.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sessions.evictions.collect {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.error_session_replaced),
                        Toast.LENGTH_LONG,
                    ).show()
                    recreate()
                }
            }
        }

        val start = when {
            !store.isLoggedIn -> Routes.AUTH
            !store.isOnboarded -> Routes.ONBOARDING
            else -> Routes.MAIN
        }

        setContent {
            TayanchTheme {
                TayanchNavGraph(startDestination = start)
            }
        }
    }
}
