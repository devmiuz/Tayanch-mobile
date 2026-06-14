package uz.tayanch.app.ui.content

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.mutableIntStateOf
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
import uz.tayanch.app.ui.theme.TayanchTheme

private const val DEMO_MS_PER_MINUTE = 5_000L // scaled down so the demo isn't tedious
private const val EXIT_DISMISS_MS = 200L // let the dialog fade out before the screen slides away
private const val VIDEO_DONE_FRACTION = 0.9f // watched ≥90% counts as completed

private fun mmss(totalSec: Int): String {
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

@Composable
fun ContentScreen(
    contentId: String,
    onFinish: () -> Unit,
    vm: ContentViewModel = koinViewModel(),
) {
    LaunchedEffect(contentId) { vm.load(contentId) }
    StateContent(vm.state, onRetry = { vm.load(contentId) }) { content ->
        if (content.type == ContentType.VIDEO) {
            VideoContentBody(content, onFinish, onCompleted = vm::markCompleted)
        } else {
            ReadingContentBody(content, onFinish, onCompleted = vm::markCompleted)
        }
    }
}

// ---------------------------------------------------------------------------
// VIDEO: progress, timer and completion track the REAL clip — duration and
// position come from the YouTube player (HardenedWebView), so the on-screen timer
// matches the actual video length instead of an arbitrary "learn time".
// ---------------------------------------------------------------------------
@Composable
private fun VideoContentBody(content: ContentDetailDto, onFinish: () -> Unit, onCompleted: () -> Unit) {
    var durationSec by remember { mutableIntStateOf(0) }
    var positionSec by remember { mutableIntStateOf(0) }
    var watchedSec by remember { mutableIntStateOf(0) }
    var lastPos by remember { mutableIntStateOf(0) }

    // Completion is gated on time ACTUALLY watched, not the current position — so
    // dragging the scrubber to minute N does NOT mark the lesson done. You earn it
    // by watching the intended minutes (estimated_minutes) or 90% of a shorter clip;
    // the header still shows the REAL position / duration.
    val requiredSec = if (durationSec > 0)
        minOf((durationSec * VIDEO_DONE_FRACTION).toInt(), maxOf(content.estimated_minutes, 1) * 60)
    else Int.MAX_VALUE
    val completed = durationSec > 0 && watchedSec >= requiredSec
    val progress = if (durationSec > 0) (watchedSec.toFloat() / requiredSec).coerceIn(0f, 1f) else 0f
    val timerLabel = if (durationSec <= 0) "…" else
        "${mmss(positionSec.coerceAtMost(durationSec))} / ${mmss(durationSec)}"

    var showLeave by remember { mutableStateOf(false) }
    val leaveScope = rememberCoroutineScope()
    val finishCompleted: () -> Unit = { onCompleted(); onFinish() }
    val confirmLeave: () -> Unit = {
        showLeave = false
        leaveScope.launch { delay(EXIT_DISMISS_MS); onFinish() }
    }
    BackHandler { if (completed) finishCompleted() else showLeave = true }

    ContentScaffold(
        title = content.title,
        timerLabel = timerLabel,
        progress = progress,
        completed = completed,
        rewardXp = content.reward_xp,
        hint = stringResource(R.string.content_watch_to_end),
        onBack = { if (completed) finishCompleted() else showLeave = true },
        onFinish = finishCompleted,
        onLeave = { showLeave = true },
    ) {
        HardenedWebView(
            url = content.url.orEmpty(),
            modifier = Modifier.fillMaxSize(),
            onVideoProgress = { p ->
                if (p.durationSec > 0) durationSec = p.durationSec
                // Count only normal ~1s advances; seek jumps (delta>2) and
                // pauses/rewinds (delta<=0) add nothing to watched time.
                val delta = p.positionSec - lastPos
                if (delta in 1..2) watchedSec += delta
                lastPos = p.positionSec
                positionSec = p.positionSec
            },
        )
    }

    if (showLeave) ForfeitDialog(onConfirm = confirmLeave, onDismiss = { showLeave = false })
}

// ---------------------------------------------------------------------------
// READING (markdown article / external weblink): the Proof-of-Presence focus
// engine — time accrues only while resumed and actively interacting (Pillar 22).
// ---------------------------------------------------------------------------
@Composable
private fun ReadingContentBody(content: ContentDetailDto, onFinish: () -> Unit, onCompleted: () -> Unit) {
    val targetMs = remember(content) { maxOf(15_000L, content.estimated_minutes * DEMO_MS_PER_MINUTE) }
    // The idle prompt is paced to the learn time: at most 3 times, no sooner than
    // every (learn time / 3) of inactivity.
    val idleThresholdMs = remember(targetMs) { targetMs / 3 }

    var accumulatedMs by remember { mutableLongStateOf(0L) }
    var lastInteraction by remember { mutableLongStateOf(SecureClock.nowMonotonic()) }
    var isResumed by remember { mutableStateOf(true) }
    var isIdle by remember { mutableStateOf(false) }
    var idlePromptCount by remember { mutableIntStateOf(0) }
    val completed = accumulatedMs >= targetMs

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

    LaunchedEffect(targetMs) {
        while (true) {
            delay(200)
            val now = SecureClock.nowMonotonic()
            isIdle = now - lastInteraction > idleThresholdMs
            if (isResumed && !isIdle && accumulatedMs < targetMs) accumulatedMs += 200
        }
    }

    val showIdleDialog = isIdle && !completed && idlePromptCount < 3
    val dismissIdle: () -> Unit = { lastInteraction = SecureClock.nowMonotonic(); idlePromptCount++ }

    var showLeave by remember { mutableStateOf(false) }
    val leaveScope = rememberCoroutineScope()
    val finishCompleted: () -> Unit = { onCompleted(); onFinish() }
    val confirmLeave: () -> Unit = {
        showLeave = false
        leaveScope.launch { delay(EXIT_DISMISS_MS); onFinish() }
    }
    BackHandler { if (completed) finishCompleted() else showLeave = true }

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

    ContentScaffold(
        title = content.title,
        timerLabel = "⏱ ${remainingSec}s",
        progress = progress,
        completed = completed,
        rewardXp = content.reward_xp,
        hint = stringResource(R.string.content_stay_focused, content.reward_xp),
        onBack = { if (completed) finishCompleted() else showLeave = true },
        onFinish = finishCompleted,
        onLeave = { showLeave = true },
    ) {
        Box(Modifier.fillMaxSize().then(trackTouches)) {
            if (content.type == ContentType.WEBLINK) {
                HardenedWebView(url = content.url.orEmpty(), modifier = Modifier.fillMaxSize())
            } else {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                    SecurityNote(stringResource(R.string.content_presence_note))
                    Spacer(Modifier.height(12.dp))
                    MarkdownText(content.markdown.orEmpty())
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    if (showIdleDialog && !showLeave) {
        AlertDialog(
            onDismissRequest = dismissIdle,
            confirmButton = { TextButton(onClick = dismissIdle) { Text(stringResource(R.string.btn_im_here)) } },
            title = { Text(stringResource(R.string.dialog_still_there_title)) },
            text = { Text(stringResource(R.string.dialog_still_there_body)) },
        )
    }

    if (showLeave) ForfeitDialog(onConfirm = confirmLeave, onDismiss = { showLeave = false })
}

@Composable
private fun ForfeitDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onConfirm) { Text(stringResource(R.string.btn_leave)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_keep_reading)) } },
        title = { Text(stringResource(R.string.dialog_forfeit_title)) },
        text = { Text(stringResource(R.string.dialog_forfeit_body)) },
    )
}

/** Shared chrome: header (title + timer + progress), a content slot, and the footer. */
@Composable
private fun ContentScaffold(
    title: String,
    timerLabel: String,
    progress: Float,
    completed: Boolean,
    rewardXp: Int,
    hint: String,
    onBack: () -> Unit,
    onFinish: () -> Unit,
    onLeave: () -> Unit,
    body: @Composable BoxScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
            Column(Modifier.statusBarsPadding().padding(horizontal = 8.dp, vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }
                    Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Text(
                        if (completed) "✓ done" else timerLabel,
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

        Box(Modifier.weight(1f), content = body)

        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
            Column(
                Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (completed) {
                    Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.btn_finish_earn_xp, rewardXp))
                    }
                } else {
                    Text(hint, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        ContentScaffold(
            title = PreviewSamples.markdownContent.title,
            timerLabel = "⏱ 12s",
            progress = 0.4f,
            completed = false,
            rewardXp = 20,
            hint = "Stay focused to earn XP",
            onBack = {}, onFinish = {}, onLeave = {},
        ) {
            MarkdownText(PreviewSamples.markdownContent.markdown.orEmpty())
        }
    }
}
