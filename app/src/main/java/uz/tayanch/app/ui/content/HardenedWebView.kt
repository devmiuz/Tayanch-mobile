package uz.tayanch.app.ui.content

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Pillar 19 — WebView hardening / sandboxing. External content is shown in a
 * WebView that:
 *  - denies local file & content access (a malicious page can't read app data),
 *  - enforces a navigation allowlist (shouldOverrideUrlLoading), blocking
 *    redirects to phishing/arbitrary domains,
 *  - turns on Safe Browsing,
 *  - never calls addJavascriptInterface (no JS→native bridge).
 */
private val ALLOWED_HOSTS = listOf(
    "youtube.com", "youtu.be", "www.youtube.com", "m.youtube.com",
    "owasp.org", "cheatsheetseries.owasp.org",
    "portswigger.net", "medium.com",
)

private fun isAllowed(url: String?): Boolean {
    if (url == null) return false
    val host = runCatching { java.net.URI(url).host }.getOrNull() ?: return false
    return ALLOWED_HOSTS.any { host == it || host.endsWith(".$it") }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HardenedWebView(url: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true // needed for embedded players
                settings.allowFileAccess = false
                settings.allowContentAccess = false
                @Suppress("DEPRECATION")
                run {
                    settings.allowFileAccessFromFileURLs = false
                    settings.allowUniversalAccessFromFileURLs = false
                }
                settings.safeBrowsingEnabled = true
                settings.domStorageEnabled = true

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?,
                    ): Boolean {
                        val target = request?.url?.toString()
                        // Returning true = we block the navigation ourselves.
                        return !isAllowed(target)
                    }
                }
                if (isAllowed(url)) loadUrl(url)
            }
        },
    )
}
