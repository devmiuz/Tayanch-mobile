package uz.tayanch.app

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import org.koin.android.ext.android.inject
import uz.tayanch.app.core.LocaleUtil
import uz.tayanch.app.data.security.SecureSessionStore
import uz.tayanch.app.ui.navigation.Routes
import uz.tayanch.app.ui.navigation.TayanchNavGraph
import uz.tayanch.app.ui.theme.TayanchTheme

/**
 * Single-activity host. It extends [FragmentActivity] so the real Android
 * BiometricPrompt can attach to it. The launch destination is decided from the
 * encrypted session store, so a returning, onboarded user lands straight on the
 * hub while a fresh install starts at the auth gateway.
 */
class MainActivity : FragmentActivity() {

    private val store: SecureSessionStore by inject()

    override fun attachBaseContext(newBase: Context) {
        // Uzbek-first UI: every stringResource resolves against res/values (uz).
        super.attachBaseContext(LocaleUtil.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

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
