package uz.tayanch.app.ui.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.delay
import uz.tayanch.app.ui.theme.SuccessGreen
import uz.tayanch.app.ui.theme.TayanchTheme
import uz.tayanch.app.R
import uz.tayanch.app.data.dto.QuestionType
import uz.tayanch.app.data.dto.QuizQuestionDto
import uz.tayanch.app.data.security.InputSanitizer
import uz.tayanch.app.data.security.SecureClock
import uz.tayanch.app.ui.components.CodeBlock
import uz.tayanch.app.ui.components.SecurityNote
import uz.tayanch.app.ui.components.StateContent
import uz.tayanch.app.ui.preview.PreviewSamples
import uz.tayanch.app.ui.security.SecureScreenEffect

@Composable
fun QuizScreen(
    contentId: String,
    onFinish: () -> Unit,
    vm: QuizViewModel = koinViewModel(),
) {
    // Pillar 2 + 20: block screenshots and overlay/auto-clicker attacks here.
    SecureScreenEffect()
    LaunchedEffect(contentId) { vm.load(contentId) }

    StateContent(vm.loadState, onRetry = { vm.load(contentId) }) {
        if (vm.isComplete) QuizComplete(vm, onFinish) else QuizActive(vm, onFinish)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizActive(vm: QuizViewModel, onFinish: () -> Unit) {
    val q = vm.current ?: return
    val reviewing = vm.phase as? QuizPhase.Reviewing

    var remaining by remember(q.id) { mutableIntStateOf(q.duration_sec) }
    // Pillar 4: countdown derived from elapsedRealtime, not the wall clock.
    LaunchedEffect(q.id, vm.phase) {
        if (vm.phase is QuizPhase.Answering) {
            val start = SecureClock.nowMonotonic()
            while (vm.phase is QuizPhase.Answering) {
                val elapsedSec = ((SecureClock.nowMonotonic() - start) / 1000).toInt()
                remaining = (q.duration_sec - elapsedSec).coerceAtLeast(0)
                if (remaining <= 0) { vm.submit(auto = true); break }
                delay(250)
            }
        }
    }

    var showReport by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    QuizQuestionContent(
        question = q,
        index = vm.index,
        total = vm.total,
        title = vm.quizTitle,
        remaining = remaining,
        reviewing = reviewing,
        selectedOptions = vm.selectedOptions,
        inputAnswer = vm.inputAnswer,
        canSubmit = vm.canSubmit,
        grading = vm.grading,
        isLast = vm.isLast,
        onToggleOption = vm::toggleOption,
        onSetInput = vm::setInput,
        onSubmit = { vm.submit() },
        onNext = vm::next,
        onBack = onFinish,
        onReport = { showReport = true },
    )

    if (showReport) {
        ModalBottomSheet(onDismissRequest = { showReport = false }, sheetState = sheetState) {
            ReportSheet(onClose = { showReport = false })
        }
    }
}

@Composable
private fun QuizQuestionContent(
    question: QuizQuestionDto,
    index: Int,
    total: Int,
    title: String,
    remaining: Int,
    reviewing: QuizPhase.Reviewing?,
    selectedOptions: Set<String>,
    inputAnswer: String,
    canSubmit: Boolean,
    grading: Boolean,
    isLast: Boolean,
    onToggleOption: (String) -> Unit,
    onSetInput: (String) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onReport: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        Surface(shadowElevation = 2.dp) {
            Column(Modifier.statusBarsPadding().padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }
                    Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Text("${index + 1}/$total", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.size(10.dp))
                    Text(
                        if (reviewing != null) "—" else "⏱ ${remaining}s",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (remaining <= 5 && reviewing == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    )
                }
                LinearProgressIndicator(
                    progress = { (index + 1f) / total },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                )
            }
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
        ) {
            SecurityNote(stringResource(R.string.quiz_security_note))
            Spacer(Modifier.height(12.dp))
            Text(question.text, style = MaterialTheme.typography.titleMedium)
            if (question.code != null) {
                Spacer(Modifier.height(10.dp))
                CodeBlock(code = question.code, language = question.language)
            }
            Spacer(Modifier.height(16.dp))

            when (question.type) {
                QuestionType.INPUT -> InputAnswer(inputAnswer, reviewing != null, onSetInput)
                else -> OptionList(question, selectedOptions, reviewing, onToggleOption)
            }

            if (reviewing != null) {
                Spacer(Modifier.height(16.dp))
                ReviewBanner(reviewing)
            }
        }

        Surface(shadowElevation = 8.dp) {
            Column(Modifier.fillMaxWidth().navigationBarsPadding().imePadding().padding(16.dp)) {
                if (reviewing == null) {
                    Button(
                        onClick = onSubmit,
                        enabled = canSubmit && !grading,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (grading) CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                        else Text(stringResource(R.string.btn_submit_answer))
                    }
                } else {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = onReport) {
                            Icon(Icons.Filled.Flag, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text(" " + stringResource(R.string.btn_report))
                        }
                        Button(onClick = onNext, modifier = Modifier.weight(1f)) {
                            Text(stringResource(if (isLast) R.string.btn_finish else R.string.btn_next))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionList(q: QuizQuestionDto, selectedOptions: Set<String>, reviewing: QuizPhase.Reviewing?, onToggle: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        q.options.forEach { option ->
            val selected = option.id in selectedOptions
            val correctIds = reviewing?.grade?.correct_option_ids.orEmpty()
            val bg = when {
                reviewing == null -> if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                option.id in correctIds -> Color(0xFFC6F6D5)
                selected -> Color(0xFFFED7D7)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            Surface(
                color = bg,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = reviewing == null) { onToggle(option.id) },
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (selected || option.id in (reviewing?.grade?.correct_option_ids.orEmpty())) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(option.text, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        if (q.type == QuestionType.MULTI) {
            Text(stringResource(R.string.quiz_select_all), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun InputAnswer(inputAnswer: String, reviewing: Boolean, onSetInput: (String) -> Unit) {
    OutlinedTextField(
        value = inputAnswer,
        onValueChange = onSetInput,
        label = { Text(stringResource(R.string.quiz_input_label)) },
        enabled = !reviewing,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    if (InputSanitizer.looksMalicious(inputAnswer)) {
        Spacer(Modifier.height(6.dp))
        Text(
            stringResource(R.string.quiz_injection_warning),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun ReviewBanner(reviewing: QuizPhase.Reviewing) {
    val grade = reviewing.grade
    val (label, color) = when {
        reviewing.savedOffline -> stringResource(R.string.quiz_saved_offline) to MaterialTheme.colorScheme.tertiary
        grade?.is_correct == true -> stringResource(R.string.quiz_correct, grade.xp_earned) to SuccessGreen
        else -> stringResource(R.string.quiz_incorrect) to MaterialTheme.colorScheme.error
    }
    Surface(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp)) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Text(label, color = color, fontWeight = FontWeight.Bold)
            if (grade != null) {
                Spacer(Modifier.height(4.dp))
                Text(grade.explanation, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ReportSheet(onClose: () -> Unit) {
    var status by remember { mutableStateOf<String?>(null) }
    var uploading by remember { mutableStateOf(false) }
    val sentMsg = stringResource(R.string.report_sent)
    val flags = listOf(
        stringResource(R.string.report_flag_wrong_key),
        stringResource(R.string.report_flag_poorly_worded),
        stringResource(R.string.report_flag_typo),
    )

    // Simulated background "screenshot" upload + content sanitization (Pillar 6 /
    // malicious-image defense): magic-number check + re-encode on the server.
    LaunchedEffect(uploading) {
        if (uploading) {
            delay(1300)
            status = sentMsg
            uploading = false
        }
    }

    Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(20.dp)) {
        Text(stringResource(R.string.report_title), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(R.string.report_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            flags.forEach { flag ->
                AssistChip(onClick = { if (!uploading && status == null) uploading = true }, label = { Text(flag) })
            }
        }
        Spacer(Modifier.height(16.dp))
        when {
            uploading -> Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(Modifier.size(18.dp))
                Spacer(Modifier.size(10.dp))
                Text(stringResource(R.string.report_uploading))
            }
            status != null -> Text(status!!, color = SuccessGreen)
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.close)) }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun QuizComplete(vm: QuizViewModel, onFinish: () -> Unit) {
    Column(
        Modifier.fillMaxSize().systemBarsPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("🏁", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(12.dp))
        Text(stringResource(R.string.quiz_complete), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.quiz_score, vm.correctCount, vm.total), style = MaterialTheme.typography.titleLarge)
        Text(stringResource(R.string.xp_plus, vm.totalXpEarned), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        if (vm.pendingSyncCount > 0) {
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.quiz_pending_sync, vm.pendingSyncCount), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.btn_done)) }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizScreenPreview() {
    TayanchTheme {
        QuizQuestionContent(
            question = PreviewSamples.quizQuestion,
            index = 0,
            total = 4,
            title = "Autentifikatsiya testi",
            remaining = 18,
            reviewing = null,
            selectedOptions = setOf("C"),
            inputAnswer = "",
            canSubmit = true,
            grading = false,
            isLast = false,
            onToggleOption = {},
            onSetInput = {},
            onSubmit = {},
            onNext = {},
            onBack = {},
            onReport = {},
        )
    }
}
