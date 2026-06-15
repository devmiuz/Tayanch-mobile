package uz.tayanch.app.ui.career

import uz.tayanch.app.ui.theme.TayanchControl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import uz.tayanch.app.ui.theme.SuccessGreen
import uz.tayanch.app.ui.theme.TayanchTheme
import uz.tayanch.app.R
import uz.tayanch.app.data.dto.ArenaStatusDto
import uz.tayanch.app.data.dto.AssignmentState
import uz.tayanch.app.data.dto.AssignmentStatusDto
import uz.tayanch.app.data.dto.CareerHubDto
import uz.tayanch.app.data.dto.MockInterviewStatusDto
import uz.tayanch.app.ui.components.SecurityNote
import uz.tayanch.app.ui.components.StateContent
import uz.tayanch.app.ui.preview.PreviewSamples

@Composable
fun CareerScreen(
    contentPadding: PaddingValues,
    onOpenBattle: () -> Unit,
    onOpenInterview: () -> Unit,
    vm: CareerViewModel = koinViewModel(),
) {
    StateContent(vm.state, Modifier.padding(contentPadding).fillMaxSize(), onRetry = vm::load) { hub ->
        CareerContent(
            hub = hub,
            contentPadding = contentPadding,
            busy = vm.busy,
            onOpenBattle = onOpenBattle,
            onRequestMock = onOpenInterview,
            onSubmit = vm::submitAssignment,
        )
    }

    if (vm.message != null) {
        AlertDialog(
            onDismissRequest = vm::clearMessage,
            confirmButton = { TextButton(onClick = vm::clearMessage) { Text(stringResource(R.string.ok)) } },
            text = { Text(vm.message!!) },
        )
    }
}

@Composable
private fun CareerContent(
    hub: CareerHubDto,
    contentPadding: PaddingValues,
    busy: Boolean,
    onOpenBattle: () -> Unit,
    onRequestMock: () -> Unit,
    onSubmit: (levelId: String, url: String) -> Unit,
) {
    Column(
        Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            stringResource(R.string.career_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ArenaCard(hub.arena, onOpenBattle)
        MockInterviewCard(hub.mock_interview, busy = busy, onRequest = onRequestMock)
        AssignmentsCard(assignments = hub.assignments, busy = busy, onSubmit = onSubmit)
        SecurityNote(stringResource(R.string.career_security_note))
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ArenaCard(arena: ArenaStatusDto, onOpenBattle: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.SportsEsports, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.size(8.dp))
                Text(stringResource(R.string.career_arena), style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(10.dp))
            if (arena.unlocked) {
                Text(stringResource(R.string.arena_qualified), style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(10.dp))
                Button(shape = TayanchControl.Shape, onClick = onOpenBattle, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.btn_find_battle))
                }
            } else {
                Text(stringResource(R.string.arena_reach_xp, arena.min_xp, arena.current_xp), style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (arena.current_xp.toFloat() / arena.min_xp).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
                Button(shape = TayanchControl.Shape, onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("  " + stringResource(R.string.locked))
                }
            }
        }
    }
}

@Composable
private fun MockInterviewCard(status: MockInterviewStatusDto, busy: Boolean, onRequest: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.VideoCall, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.size(8.dp))
                Text(stringResource(R.string.career_mock), style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                stringResource(R.string.mock_level_reached, status.reached_level, status.pending_requests),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(10.dp))
            Button(shape = TayanchControl.Shape, onClick = onRequest, enabled = status.available && !busy, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.btn_request_mock))
            }
        }
    }
}

@Composable
private fun AssignmentsCard(
    assignments: List<AssignmentStatusDto>,
    busy: Boolean,
    onSubmit: (levelId: String, url: String) -> Unit,
) {
    val urlInputs = remember { mutableStateMapOf<String, String>() }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.career_assignments), style = MaterialTheme.typography.titleMedium)
            assignments.forEach { a ->
                Column {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(a.level_label, style = MaterialTheme.typography.bodyLarge)
                        StatusChip(a.state)
                    }
                    if (a.github_url != null) {
                        Text(defang(a.github_url), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (a.state == AssignmentState.AVAILABLE || a.state == AssignmentState.REJECTED) {
                        Spacer(Modifier.height(6.dp))
                        val value = urlInputs[a.level_id].orEmpty()
                        OutlinedTextField(
                            value = value,
                            onValueChange = { urlInputs[a.level_id] = it },
                            label = { Text(stringResource(R.string.field_github)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(6.dp))
                        OutlinedButton(shape = TayanchControl.Shape, 
                            onClick = { onSubmit(a.level_id, value) },
                            enabled = value.isNotBlank() && !busy,
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text(stringResource(R.string.btn_submit_assignment)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(state: String) {
    val (label, color) = when (state) {
        AssignmentState.ACCEPTED -> stringResource(R.string.status_accepted) to SuccessGreen
        AssignmentState.PENDING -> stringResource(R.string.status_pending) to Color(0xFFB7791F)
        AssignmentState.REJECTED -> stringResource(R.string.status_rejected) to MaterialTheme.colorScheme.error
        AssignmentState.AVAILABLE -> stringResource(R.string.status_available) to MaterialTheme.colorScheme.primary
        else -> stringResource(R.string.status_locked) to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
        Text(label, color = color, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
    }
}

/** Visual link defanging (Pillar 12/14) so a tapped link can't be auto-followed. */
private fun defang(url: String): String =
    url.replace("http://", "hxxp://").replace("https://", "hxxps://").replace(".", "[.]")

@Preview(showBackground = true)
@Composable
private fun CareerScreenPreview() {
    TayanchTheme {
        CareerContent(
            hub = PreviewSamples.careerHub,
            contentPadding = PaddingValues(0.dp),
            busy = false,
            onOpenBattle = {},
            onRequestMock = {},
            onSubmit = { _, _ -> },
        )
    }
}
