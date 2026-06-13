package uz.tayanch.app.ui.battle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.delay
import uz.tayanch.app.ui.theme.SuccessGreen
import uz.tayanch.app.ui.theme.TayanchTheme
import uz.tayanch.app.R
import uz.tayanch.app.data.dto.QuizQuestionDto
import uz.tayanch.app.data.security.SecureClock
import uz.tayanch.app.ui.components.ErrorBox
import uz.tayanch.app.ui.components.SecurityNote
import uz.tayanch.app.ui.components.UiState
import uz.tayanch.app.ui.preview.PreviewSamples
import uz.tayanch.app.ui.security.SecureScreenEffect

@Composable
fun BattleScreen(
    onFinish: () -> Unit,
    vm: BattleViewModel = koinViewModel(),
) {
    SecureScreenEffect()
    LaunchedEffect(Unit) { vm.load() }

    when (vm.loadState) {
        is UiState.Loading -> Searching()
        is UiState.Error -> ErrorBox((vm.loadState as UiState.Error).message, onRetry = vm::load)
        is UiState.Success -> if (vm.isComplete) BattleResult(vm, onFinish) else BattleActive(vm, onFinish)
    }
}

@Composable
private fun Searching() {
    Column(
        Modifier.fillMaxSize().systemBarsPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.battle_searching), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Text(
            stringResource(R.string.battle_handshake_note),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BattleActive(vm: BattleViewModel, onFinish: () -> Unit) {
    val q = vm.current ?: return
    var remaining by remember(vm.index) { mutableIntStateOf(q.duration_sec) }
    LaunchedEffect(vm.index, vm.inResult) {
        if (!vm.inResult) {
            val start = SecureClock.nowMonotonic()
            while (!vm.inResult) {
                remaining = (q.duration_sec - ((SecureClock.nowMonotonic() - start) / 1000).toInt()).coerceAtLeast(0)
                if (remaining <= 0) { vm.submit(auto = true); break }
                delay(250)
            }
        }
    }

    BattleRound(
        question = q,
        round = vm.index + 1,
        total = vm.total,
        myScore = vm.myScore,
        oppScore = vm.oppScore,
        opponentName = vm.opponent?.name.orEmpty(),
        remaining = remaining,
        selected = vm.selected,
        inResult = vm.inResult,
        grading = vm.grading,
        isLast = vm.isLast,
        lastMyCorrect = vm.lastMyCorrect,
        lastOppCorrect = vm.lastOppCorrect,
        onChoose = vm::choose,
        onSubmit = { vm.submit() },
        onNext = vm::next,
    )
}

@Composable
private fun BattleRound(
    question: QuizQuestionDto,
    round: Int,
    total: Int,
    myScore: Int,
    oppScore: Int,
    opponentName: String,
    remaining: Int,
    selected: String?,
    inResult: Boolean,
    grading: Boolean,
    isLast: Boolean,
    lastMyCorrect: Boolean?,
    lastOppCorrect: Boolean,
    onChoose: (String) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
) {
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(16.dp)) {
        ScoreHeader(myScore, oppScore, opponentName, remaining, inResult)
        Spacer(Modifier.height(10.dp))
        SecurityNote(stringResource(R.string.battle_security_note))

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(vertical = 16.dp)) {
            Text(stringResource(R.string.battle_round, round, total), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text(question.text, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            question.options.forEach { option ->
                val isSelected = selected == option.id
                val bg = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                Surface(
                    color = bg,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable(enabled = !inResult) { onChoose(option.id) },
                ) {
                    Text(option.text, Modifier.padding(14.dp), style = MaterialTheme.typography.bodyLarge)
                }
            }

            if (inResult) {
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ResultChip(stringResource(R.string.battle_you), lastMyCorrect == true)
                    ResultChip(opponentName, lastOppCorrect)
                }
            }
        }

        Surface(shadowElevation = 8.dp) {
            Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                if (!inResult) {
                    Button(onClick = onSubmit, enabled = selected != null && !grading, modifier = Modifier.fillMaxWidth()) {
                        if (grading) CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                        else Text(stringResource(R.string.btn_lock_answer))
                    }
                } else {
                    Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(if (isLast) R.string.btn_see_result else R.string.btn_next_round))
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreHeader(myScore: Int, oppScore: Int, opponentName: String, remaining: Int, inResult: Boolean) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(stringResource(R.string.battle_you), style = MaterialTheme.typography.labelMedium)
                Text("$myScore", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (inResult) "—" else "⏱ ${remaining}s", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.battle_vs), style = MaterialTheme.typography.labelSmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(opponentName, style = MaterialTheme.typography.labelMedium)
                Text("$oppScore", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ResultChip(name: String, correct: Boolean) {
    val color = if (correct) SuccessGreen else MaterialTheme.colorScheme.error
    Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
        Text(
            stringResource(if (correct) R.string.battle_correct else R.string.battle_wrong, name),
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun BattleResult(vm: BattleViewModel, onFinish: () -> Unit) {
    val title = when (vm.winner) {
        "YOU" -> stringResource(R.string.battle_victory)
        "OPPONENT" -> stringResource(R.string.battle_defeat)
        else -> stringResource(R.string.battle_draw)
    }
    Column(
        Modifier.fillMaxSize().systemBarsPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Text("${vm.myScore} – ${vm.oppScore}", style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(8.dp))
        Text(vm.resultDetail, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.btn_back_career)) }
    }
}

@Preview(showBackground = true)
@Composable
private fun BattleScreenPreview() {
    TayanchTheme {
        BattleRound(
            question = PreviewSamples.quizQuestion,
            round = 2,
            total = 5,
            myScore = 30,
            oppScore = 15,
            opponentName = PreviewSamples.opponent.name,
            remaining = 12,
            selected = "C",
            inResult = false,
            grading = false,
            isLast = false,
            lastMyCorrect = null,
            lastOppCorrect = false,
            onChoose = {},
            onSubmit = {},
            onNext = {},
        )
    }
}
