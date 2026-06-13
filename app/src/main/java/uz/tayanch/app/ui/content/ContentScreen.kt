package uz.tayanch.app.ui.content

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.tayanch.app.R
import uz.tayanch.app.data.dto.ContentDetailDto
import uz.tayanch.app.data.dto.ContentType
import uz.tayanch.app.data.security.SecureClock
import uz.tayanch.app.ui.components.MarkdownText
import uz.tayanch.app.ui.components.SecurityNote
import uz.tayanch.app.ui.components.StateContent
import uz.tayanch.app.ui.preview.PreviewSamples
import uz.tayanch.app.ui.security.SecureScreenEffect
import uz.tayanch.app.ui.theme.TayanchTheme

private const val DEMO_MS_PER_MINUTE = 5_000L // scaled down so the demo isn't tedious
private const val IDLE_MS = 7_000L
private const val EXIT_DISMISS_MS = 200L // let the dialog fade out before the screen slides away

@Composable
fun ContentScreen(
    contentId: String,
    onFinish: () -> Unit,
    vm: ContentViewModel = koinViewModel(),
) {
    LaunchedEffect(contentId) { vm.load(contentId) }
    StateContent(vm.state, onRetry = { vm.load(contentId) }) { content ->
        ContentBody(content, onFinish)
    }
}

@Composable
private fun ContentBody(content: ContentDetailDto, onFinish: () -> Unit) {
    val isVideo = content.type == ContentType.VIDEO
    if (isVideo) SecureScreenEffect(antiTapjacking = false) // Pillar 8: block recording of video

    val targetMs = remember(content) {
        maxOf(15_000L, content.estimated_minutes * DEMO_MS_PER_MINUTE)
    }

    var accumulatedMs by remember { mutableLongStateOf(0L) }
    var lastInteraction by remember { mutableLongStateOf(SecureClock.nowMonotonic()) }
    var isResumed by remember { mutableStateOf(true) }
    var isIdle by remember { mutableStateOf(false) }
    val completed = accumulatedMs >= targetMs

    // Pillar 22 — pause the timer the instant the app is backgrounded / split-screened.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> { isResumed = true; lastInteraction = SecureClock.nowMonotonic() }
                Lifecycle.Event.ON_PAUSE -> isResumed = false
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // The focus tick: only accrues time while resumed AND actively interacting.
    LaunchedEffect(targetMs) {
        while (true) {
            delay(200)
            val now = SecureClock.nowMonotonic()
            isIdle = now - lastInteraction > IDLE_MS
            if (isResumed && !isIdle && accumulatedMs < targetMs) {
                accumulatedMs += 200
            }
        }
    }

    var showLeave by remember { mutableStateOf(false) }
    val leaveScope = rememberCoroutineScope()
    // Dismiss the confirm dialog, let it fade, THEN pop so the screen slides away
    // smoothly (instead of vanishing the instant the button is tapped).
    val confirmLeave: () -> Unit = {
        showLeave = false
        leaveScope.launch {
            delay(EXIT_DISMISS_MS)
            onFinish()
        }
    }
    BackHandler { if (completed) onFinish() else showLeave = true }

    val trackTouches = Modifier.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                awaitPointerEvent(PointerEventPass.Initial)
                lastInteraction = SecureClock.nowMonotonic()
            }
        }
    }

    val remainingSec = (targetMs - accumulatedMs).coerceAtLeast(0) / 1000
    val progress = (accumulatedMs.toFloat() / targetMs).coerceIn(0f, 1f)
    ArticleReader(
        content = content,
        remainingSec = remainingSec,
        progress = progress,
        completed = completed,
        interaction = trackTouches,
        onBack = { if (completed) onFinish() else showLeave = true },
        onFinish = onFinish,
        onLeave = { showLeave = true },
    )

    // Never stack both dialogs: the leave/forfeit dialog takes priority over the
    // idle "Are you still there?" prompt.
    if (isIdle && !completed && !showLeave) {
        AlertDialog(
            onDismissRequest = { lastInteraction = SecureClock.nowMonotonic() },
            confirmButton = {
                TextButton(onClick = { lastInteraction = SecureClock.nowMonotonic() }) { Text(stringResource(R.string.btn_im_here)) }
            },
            title = { Text(stringResource(R.string.dialog_still_there_title)) },
            text = { Text(stringResource(R.string.dialog_still_there_body)) },
        )
    }

    if (showLeave) {
        AlertDialog(
            onDismissRequest = { showLeave = false },
            confirmButton = { TextButton(onClick = confirmLeave) { Text(stringResource(R.string.btn_leave)) } },
            dismissButton = { TextButton(onClick = { showLeave = false }) { Text(stringResource(R.string.btn_keep_reading)) } },
            title = { Text(stringResource(R.string.dialog_forfeit_title)) },
            text = { Text(stringResource(R.string.dialog_forfeit_body)) },
        )
    }
}

/** Stateless reader UI — the focus-engine state lives in [ContentBody]. */
@Composable
private fun ArticleReader(
    content: ContentDetailDto,
    remainingSec: Long,
    progress: Float,
    completed: Boolean,
    interaction: Modifier,
    onBack: () -> Unit,
    onFinish: () -> Unit,
    onLeave: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
            Column(Modifier.statusBarsPadding().padding(horizontal = 8.dp, vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                    Text(content.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Text(
                        if (completed) "✓ done" else "⏱ ${remainingSec}s",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    )
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                )
            }
        }

        Box(Modifier.weight(1f).then(interaction)) {
            when (content.type) {
                ContentType.WEBLINK, ContentType.VIDEO ->
                    HardenedWebView(url = content.url.orEmpty(), modifier = Modifier.fillMaxSize())
                else -> Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                ) {
                    SecurityNote(stringResource(R.string.content_presence_note))
                    Spacer(Modifier.height(12.dp))
                    MarkdownText(content.markdown.orEmpty())
                    Spacer(Modifier.height(24.dp))
                }
            }
        }

        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
            Column(
                Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (completed) {
                    Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.btn_finish_earn_xp, content.reward_xp))
                    }
                } else {
                    Text(
                        stringResource(R.string.content_stay_focused, content.reward_xp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(onClick = onLeave) { Text(stringResource(R.string.btn_leave_forfeit)) }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContentScreenPreview() {
    TayanchTheme {
        ArticleReader(
            content = PreviewSamples.markdownContent,
            remainingSec = 12,
            progress = 0.4f,
            completed = false,
            interaction = Modifier,
            onBack = {},
            onFinish = {},
            onLeave = {},
        )
    }
}
