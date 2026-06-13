package uz.tayanch.app.ui.flashcard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import uz.tayanch.app.R
import uz.tayanch.app.data.dto.FlashcardDto
import uz.tayanch.app.ui.components.CodeBlock
import uz.tayanch.app.ui.components.SecurityNote
import uz.tayanch.app.ui.components.StateContent
import uz.tayanch.app.ui.preview.PreviewSamples
import uz.tayanch.app.ui.theme.TayanchTheme

@Composable
fun FlashcardScreen(
    contentId: String,
    onFinish: () -> Unit,
    vm: FlashcardViewModel = koinViewModel(),
) {
    LaunchedEffect(contentId) { vm.load(contentId) }
    StateContent(vm.loadState, onRetry = { vm.load(contentId) }) {
        if (vm.isComplete) {
            DeckCleared(vm.rewardXp, onFinish)
        } else {
            DeckView(vm, onFinish)
        }
    }
}

@Composable
private fun DeckView(vm: FlashcardViewModel, onFinish: () -> Unit) {
    val card = vm.current ?: return
    LaunchedEffect(card.id) { vm.resetShownClock() }
    FlashcardDeck(
        card = card,
        remaining = vm.queue.size,
        total = vm.total,
        frozen = vm.frozen,
        onResult = vm::onResult,
        onReset = vm::acknowledgeFreezeAndReset,
        onBack = onFinish,
    )
}

@Composable
private fun FlashcardDeck(
    card: FlashcardDto,
    remaining: Int,
    total: Int,
    frozen: Boolean,
    onResult: (Boolean) -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
) {
    var flipped by remember(card.id) { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }
            Text(stringResource(R.string.flashcards_title), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text(stringResource(R.string.cards_remaining, remaining, total), style = MaterialTheme.typography.labelLarge)
        }
        Spacer(Modifier.height(8.dp))
        SecurityNote(stringResource(R.string.flashcards_security_note))

        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            FlipCard(card = card, flipped = flipped, onTap = { flipped = !flipped })
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { onResult(false) }, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.btn_review))
            }
            Button(onClick = { onResult(true) }, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.btn_know_it))
            }
        }
    }

    if (frozen) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = { TextButton(onClick = onReset) { Text(stringResource(R.string.btn_start_over)) } },
            title = { Text(stringResource(R.string.dialog_slow_down_title)) },
            text = { Text(stringResource(R.string.dialog_slow_down_body)) },
        )
    }
}

@Composable
private fun FlipCard(card: FlashcardDto, flipped: Boolean, onTap: () -> Unit) {
    val rotation by animateFloatAsState(if (flipped) 180f else 0f, label = "flip")
    val density = LocalDensity.current.density
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(onClick = onTap),
        colors = CardDefaults.cardColors(
            containerColor = if (rotation <= 90f) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
            if (rotation <= 90f) {
                Column(
                    Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(R.string.card_question), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(10.dp))
                    Text(card.front, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                    if (card.code != null) {
                        Spacer(Modifier.height(12.dp))
                        CodeBlock(code = card.code, language = card.language)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.card_tap_flip), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                // Un-mirror the back face.
                Column(
                    Modifier.graphicsLayer { rotationY = 180f }.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(R.string.card_answer), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.height(10.dp))
                    Text(card.back, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable
private fun DeckCleared(xp: Int, onFinish: () -> Unit) {
    Column(
        Modifier.fillMaxSize().systemBarsPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("🎉", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(12.dp))
        Text(stringResource(R.string.deck_cleared), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(stringResource(R.string.xp_plus, xp), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.btn_back_roadmap)) }
    }
}

@Preview(showBackground = true)
@Composable
private fun FlashcardScreenPreview() {
    TayanchTheme {
        FlashcardDeck(
            card = PreviewSamples.flashcard,
            remaining = 4,
            total = 5,
            frozen = false,
            onResult = {},
            onReset = {},
            onBack = {},
        )
    }
}
