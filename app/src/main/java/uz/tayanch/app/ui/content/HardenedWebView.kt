package uz.tayanch.app.ui.content

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Pillar 19 — WebView hardening / sandboxing. External content is shown in a
 * WebView that denies local file/content access, enforces a navigation allowlist,
 * turns on Safe Browsing, and (for articles) exposes no JS bridge.
 *
 * Articles `loadUrl` directly. YouTube lessons use the **IFrame Player API** so
 * the app can read the real duration / playback position / ended state and align
 * the on-screen timer to the actual clip length. A single, receive-only
 * JavascriptInterface (`YtBridge.report`, numbers only) carries player state back
 * — a deliberately minimal, trusted-content-only relaxation of the no-bridge rule.
 */
private val ALLOWED_HOSTS = listOf(
    "youtube.com", "youtu.be", "www.youtube.com", "m.youtube.com",
    "owasp.org", "cheatsheetseries.owasp.org", "portswigger.net", "medium.com",
    "python.org", "realpython.com",
    "developer.android.com", "kotlinlang.org",
    "interaction-design.org", "figma.com", "fonts.google.com",
)

private fun isAllowed(url: String?): Boolean {
    if (url == null) return false
    if (url.startsWith("about:") || url.startsWith("data:") || url.startsWith("blob:")) return true
    val host = runCatching { java.net.URI(url).host }.getOrNull() ?: return false
    return ALLOWED_HOSTS.any { host == it || host.endsWith(".$it") }
}

private fun youtubeId(url: String): String? = runCatching {
    val uri = java.net.URI(url)
    val host = uri.host.orEmpty()
    when {
        host.endsWith("youtu.be") -> uri.path.trimStart('/').substringBefore('/')
        host.endsWith("youtube.com") && uri.path.startsWith("/embed/") -> uri.path.removePrefix("/embed/").substringBefore('/')
        host.endsWith("youtube.com") && uri.path.startsWith("/watch") ->
            uri.query.orEmpty().split("&").firstOrNull { it.startsWith("v=") }?.substringAfter("v=")
        else -> null
    }?.takeIf { it.isNotBlank() }
}.getOrNull()

/** IFrame Player API page; polls the player and reports state back via YtBridge. */
private fun youtubeIframeApiHtml(id: String): String = """
    <!DOCTYPE html><html><head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <style>html,body{margin:0;background:#000;height:100%;overflow:hidden}#p{position:fixed;inset:0}</style>
    </head><body><div id="p"></div>
    <script src="https://www.youtube.com/iframe_api"></script>
    <script>
      var player;
      function onYouTubeIframeAPIReady(){
        player = new YT.Player('p', {
          videoId: '$id', width:'100%', height:'100%',
          playerVars: {playsinline:1, rel:0, modestbranding:1, origin:'https://tayanch.uz'},
          events: { 'onReady': report, 'onStateChange': report }
        });
      }
      function report(){
        try {
          var d = Math.floor(player.getDuration() || 0);
          var c = Math.floor(player.getCurrentTime() || 0);
          var ended = (player.getPlayerState() === 0);
          if (window.YtBridge) YtBridge.report(d, c, ended);
        } catch(e) {}
      }
      setInterval(report, 1000);
    </script></body></html>
""".trimIndent()

/** Player state pushed from the WebView (duration & position in seconds). */
data class VideoProgress(val durationSec: Int, val positionSec: Int, val ended: Boolean)

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun HardenedWebView(
    url: String,
    modifier: Modifier = Modifier,
    onVideoProgress: ((VideoProgress) -> Unit)? = null,
) {
    var loading by remember(url) { mutableStateOf(true) }
    val progressCb = rememberUpdatedState(onVideoProgress)

    Box(modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false
                    @Suppress("DEPRECATION")
                    run {
                        settings.allowFileAccessFromFileURLs = false
                        settings.allowUniversalAccessFromFileURLs = false
                    }
                    settings.safeBrowsingEnabled = true
                    settings.domStorageEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = false

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean =
                            !isAllowed(request?.url?.toString())

                        override fun onPageStarted(view: WebView?, u: String?, favicon: android.graphics.Bitmap?) { loading = true }
                        override fun onPageFinished(view: WebView?, u: String?) { loading = false }
                        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: android.webkit.WebResourceError?) {
                            if (request?.isForMainFrame == true) loading = false
                        }
                    }

                    val ytId = youtubeId(url)
                    if (ytId != null) {
                        // Minimal receive-only bridge for player state.
                        addJavascriptInterface(object {
                            @JavascriptInterface
                            fun report(durationSec: Int, positionSec: Int, ended: Boolean) {
                                post { progressCb.value?.invoke(VideoProgress(durationSec, positionSec, ended)) }
                            }
                        }, "YtBridge")
                        // Base URL must be a third-party origin (not youtube.com) or the embed errors.
                        loadDataWithBaseURL("https://tayanch.uz", youtubeIframeApiHtml(ytId), "text/html", "utf-8", null)
                    } else if (isAllowed(url)) {
                        loadUrl(url)
                    }
                }
            },
        )

        if (loading) CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
}
