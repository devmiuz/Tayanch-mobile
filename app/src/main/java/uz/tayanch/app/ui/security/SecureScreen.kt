package uz.tayanch.app.ui.security

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView

/**
 * Applies two client-side anti-cheat defenses for as long as the calling screen
 * is on the composition, then cleanly removes them on exit:
 *
 *  - Pillar 2  FLAG_SECURE — blocks screenshots & screen recording (a black frame
 *              is captured instead), protecting question IP and stopping leaks.
 *  - Pillar 20 filterTouchesWhenObscured — the OS drops touch events when another
 *              app draws an overlay on top of us, defeating tapjacking / auto-clicker
 *              overlays during quizzes and battles.
 */
@Composable
fun SecureScreenEffect(antiTapjacking: Boolean = true) {
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = view.context.findActivity()?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        val previousFilter = view.filterTouchesWhenObscured
        if (antiTapjacking) view.filterTouchesWhenObscured = true
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            view.filterTouchesWhenObscured = previousFilter
        }
    }
}

fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
