package uz.tayanch.app.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.tayanch.app.ui.theme.SuccessGreen
import uz.tayanch.app.ui.theme.TayanchTheme
import uz.tayanch.app.R
import uz.tayanch.app.data.dto.ContentNodeDto
import uz.tayanch.app.data.dto.ContentType
import uz.tayanch.app.data.dto.LevelDto
import uz.tayanch.app.data.dto.RoadmapResponse
import uz.tayanch.app.data.dto.TopicDto
import uz.tayanch.app.ui.components.SecurityNote
import uz.tayanch.app.ui.components.StateContent
import uz.tayanch.app.ui.preview.PreviewSamples

@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    onOpenContent: (String) -> Unit,
    onOpenFlashcard: (String) -> Unit,
    onOpenQuiz: (String) -> Unit,
    vm: HomeViewModel = koinViewModel(),
) {
    StateContent(vm.state, Modifier.padding(contentPadding).fillMaxSize(), onRetry = vm::load) { roadmap ->
        HomeContent(roadmap, contentPadding, onOpenContent, onOpenFlashcard, onOpenQuiz)
    }
}

@Composable
private fun HomeContent(
    roadmap: RoadmapResponse,
    contentPadding: PaddingValues,
    onOpenContent: (String) -> Unit,
    onOpenFlashcard: (String) -> Unit,
    onOpenQuiz: (String) -> Unit,
) {
    val openNode: (ContentNodeDto) -> Unit = { node ->
        if (!node.is_locked) {
            when (node.type) {
                ContentType.FLASHCARD -> onOpenFlashcard(node.content_id)
                ContentType.QUIZ -> onOpenQuiz(node.content_id)
                else -> onOpenContent(node.content_id)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.padding(contentPadding).fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column {
                Text(
                    stringResource(R.string.home_roadmap_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                SecurityNote(stringResource(R.string.home_security_note))
            }
        }

        roadmap.levels.forEach { level ->
            item { LevelHeader(level) }
            items(level.topics) { topic -> TopicCard(topic, onNode = openNode) }
            item { LevelAssignmentCard(level) }
        }

        item { Spacer(Modifier.height(88.dp)) } // clearance for the FAB
    }
}

@Composable
private fun LevelHeader(level: LevelDto) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            color = if (level.is_unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(6.dp),
        ) {
            Text(
                level.rank_label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = if (level.is_unlocked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(level.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        if (!level.is_unlocked) {
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Filled.Lock, contentDescription = stringResource(R.string.locked), modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TopicCard(topic: TopicDto, onNode: (ContentNodeDto) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text(topic.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                topic.contents.forEach { node -> ContentNode(node, onNode) }
            }
        }
    }
}

@Composable
private fun ContentNode(node: ContentNodeDto, onNode: (ContentNodeDto) -> Unit) {
    val icon = iconFor(node.type)
    val tint = when {
        node.is_locked -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        node.is_completed -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(56.dp)) {
        Box(contentAlignment = Alignment.TopEnd) {
            Surface(
                shape = CircleShape,
                color = if (node.is_completed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .size(48.dp)
                    .clickable(enabled = !node.is_locked) { onNode(node) },
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (node.is_locked) Icons.Filled.Lock else icon,
                        contentDescription = node.type,
                        tint = tint,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
            if (node.is_completed) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Text(
            stringResource(labelResFor(node.type)),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LevelAssignmentCard(level: LevelDto) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    var showInstructions by remember { mutableStateOf(false) }

    // Skip the launcher in @Preview — there's no ActivityResultRegistryOwner there.
    val launcher = if (LocalInspectionMode.current) {
        null
    } else {
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { showInstructions = true }
    }

    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.level_assignment_boss), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(Modifier.height(4.dp))
            Text(level.assignment.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            if (!level.is_unlocked) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.level_unlock_xp, level.required_xp), style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                Button(
                    enabled = !loading,
                    onClick = {
                        loading = true
                        scope.launch {
                            val uri = withContext(Dispatchers.IO) {
                                AssignmentShare.createPdf(context, level.assignment.title)
                            }
                            launcher?.launch(AssignmentShare.chooser(context, uri))
                            loading = false
                        }
                    },
                ) {
                    if (loading) {
                        CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("  " + stringResource(R.string.btn_get_task_pdf))
                    }
                }
            }
        }
    }

    if (showInstructions) {
        AlertDialog(
            onDismissRequest = { showInstructions = false },
            confirmButton = { TextButton(onClick = { showInstructions = false }) { Text(stringResource(R.string.got_it)) } },
            title = { Text(stringResource(R.string.dialog_next_steps_title)) },
            text = { Text(stringResource(R.string.dialog_next_steps_body)) },
        )
    }
}

private fun iconFor(type: String): ImageVector = when (type) {
    ContentType.MARKDOWN -> Icons.Filled.Article
    ContentType.WEBLINK -> Icons.Filled.Language
    ContentType.VIDEO -> Icons.Filled.PlayCircle
    ContentType.FLASHCARD -> Icons.Filled.Style
    else -> Icons.Filled.Quiz
}

@StringRes
private fun labelResFor(type: String): Int = when (type) {
    ContentType.MARKDOWN -> R.string.node_read
    ContentType.WEBLINK -> R.string.node_web
    ContentType.VIDEO -> R.string.node_video
    ContentType.FLASHCARD -> R.string.node_cards
    else -> R.string.node_quiz
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    TayanchTheme {
        HomeContent(
            roadmap = PreviewSamples.roadmap,
            contentPadding = PaddingValues(0.dp),
            onOpenContent = {},
            onOpenFlashcard = {},
            onOpenQuiz = {},
        )
    }
}
