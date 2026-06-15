package uz.tayanch.app.ui.profile

import uz.tayanch.app.ui.theme.TayanchControl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import uz.tayanch.app.R
import uz.tayanch.app.data.dto.BadgeDto
import uz.tayanch.app.data.dto.LeaderboardEntryDto
import uz.tayanch.app.data.dto.UserProfileDto
import uz.tayanch.app.ui.components.SecurityNote
import uz.tayanch.app.ui.components.StateContent
import uz.tayanch.app.ui.preview.PreviewSamples
import uz.tayanch.app.ui.theme.TayanchTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    contentPadding: PaddingValues,
    onOpenResume: () -> Unit,
    onLogout: () -> Unit,
    vm: ProfileViewModel = koinViewModel(),
) {
    StateContent(vm.state, Modifier.padding(contentPadding).fillMaxSize(), onRetry = vm::load) { data ->
        ProfileContent(
            data = data,
            contentPadding = contentPadding,
            scope = vm.scope,
            onScope = vm::selectScope,
            onOpenResume = onOpenResume,
            onLogout = { vm.logout(onLogout) },
        )
    }
}

@Composable
private fun ProfileContent(
    data: ProfileData,
    contentPadding: PaddingValues,
    scope: String,
    onScope: (String) -> Unit,
    onOpenResume: () -> Unit,
    onLogout: () -> Unit,
) {
    val profile = data.profile
    var showLogoutDialog by remember { mutableStateOf(false) }
    Column(
        Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(profile.full_name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            stringResource(R.string.profile_identity, profile.current_level, profile.target_level, profile.expected_salary, profile.global_rank),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        AiMotivationCard(profile.ai_motivation)
        VelocityCard(profile)
        FocusCard(profile)
        BadgesCard(profile.badges)
        LeaderboardCard(scope = scope, onScope = onScope, entries = data.leaderboard.leaderboard)

        SecurityNote(stringResource(R.string.profile_security_note))

        Button(shape = TayanchControl.Shape, onClick = onOpenResume, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            Text("  " + stringResource(R.string.profile_edit_resume))
        }
        OutlinedButton(shape = TayanchControl.Shape, onClick = { showLogoutDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Text("  " + stringResource(R.string.btn_logout))
        }
        Spacer(Modifier.height(8.dp))
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text(stringResource(R.string.btn_logout))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text(stringResource(R.string.btn_cancel)) }
            },
            title = { Text(stringResource(R.string.logout_confirm_title)) },
            text = { Text(stringResource(R.string.logout_confirm_body)) },
        )
    }
}

@Composable
private fun AiMotivationCard(text: String) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.size(10.dp))
            Column {
                Text(stringResource(R.string.profile_ai_coach), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@Composable
private fun VelocityCard(profile: UserProfileDto) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.size(8.dp))
                Text(stringResource(R.string.profile_velocity), style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(8.dp))
            Text(profile.velocity.projection_text, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.profile_pace, profile.velocity.weekly_xp, profile.velocity.target_salary),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FocusCard(profile: UserProfileDto) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(stringResource(R.string.profile_avg_focus), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(profile.focus.average_focus_time, style = MaterialTheme.typography.titleMedium)
            }
            Column {
                Text(stringResource(R.string.profile_distraction), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(profile.focus.distraction_rate, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BadgesCard(badges: List<BadgeDto>) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.profile_badges), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                badges.forEach { badge ->
                    Surface(
                        color = if (badge.earned) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Row(
                            Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(badge.emoji)
                            Spacer(Modifier.size(6.dp))
                            Text(
                                badge.title,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (badge.earned) MaterialTheme.colorScheme.onSecondaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardCard(
    scope: String,
    onScope: (String) -> Unit,
    entries: List<LeaderboardEntryDto>,
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.profile_leaderboard), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = scope == "major", onClick = { onScope("major") }, label = { Text(stringResource(R.string.scope_major)) })
                FilterChip(selected = scope == "global", onClick = { onScope("global") }, label = { Text(stringResource(R.string.scope_global)) })
            }
            Spacer(Modifier.height(10.dp))
            entries.forEach { entry -> LeaderboardRow(entry) }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntryDto) {
    val highlight = entry.is_me
    Surface(
        color = if (highlight) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "#${entry.rank}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.width(40.dp),
                fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
            )
            Column(Modifier.weight(1f)) {
                Text(entry.name, style = MaterialTheme.typography.bodyLarge, fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal)
                Text(entry.level, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("${entry.xp} XP", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    TayanchTheme {
        ProfileContent(
            data = PreviewSamples.profileData,
            contentPadding = PaddingValues(0.dp),
            scope = "major",
            onScope = {},
            onOpenResume = {},
            onLogout = {},
        )
    }
}
