package uz.tayanch.app.ui.onboarding

import uz.tayanch.app.ui.theme.TayanchControl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import uz.tayanch.app.R
import uz.tayanch.app.data.dto.MajorDto
import uz.tayanch.app.ui.components.SecurityNote
import uz.tayanch.app.ui.preview.PreviewSamples
import uz.tayanch.app.ui.theme.TayanchTheme

@Composable
fun OnboardingScreen(
    onDone: () -> Unit,
    vm: OnboardingViewModel = koinViewModel(),
) {
    OnboardingContent(
        state = vm.state,
        filteredMajors = vm.filteredMajors,
        showAddMajor = vm.showAddMajor,
        canSubmit = vm.canSubmit,
        onMajorQuery = vm::onMajorQuery,
        onSelectMajor = vm::selectMajor,
        onAddCustomMajor = vm::addCustomMajor,
        onToggleInterest = vm::toggleInterest,
        onLoadMore = vm::loadMoreInterests,
        onSalary = vm::onSalary,
        onSubmit = { vm.submit(onDone) },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OnboardingContent(
    state: OnboardingUiState,
    filteredMajors: List<MajorDto>,
    showAddMajor: Boolean,
    canSubmit: Boolean,
    onMajorQuery: (String) -> Unit,
    onSelectMajor: (String) -> Unit,
    onAddCustomMajor: () -> Unit,
    onToggleInterest: (String) -> Unit,
    onLoadMore: () -> Unit,
    onSalary: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.ob_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            stringResource(R.string.ob_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(20.dp))

        Text(stringResource(R.string.ob_major), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.majorQuery,
            onValueChange = onMajorQuery,
            label = { Text(stringResource(R.string.ob_major_search)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(10.dp))
        if (state.majorsLoading) {
            CircularProgressIndicator(Modifier.height(24.dp))
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filteredMajors.forEach { major ->
                    FilterChip(
                        selected = state.selectedMajor == major.name,
                        onClick = { onSelectMajor(major.name) },
                        label = { Text(major.name) },
                        leadingIcon = if (state.selectedMajor == major.name) {
                            { Icon(Icons.Filled.Check, contentDescription = null) }
                        } else null,
                    )
                }
                if (showAddMajor) {
                    AssistChip(
                        onClick = { onAddCustomMajor() },
                        label = { Text(stringResource(R.string.ob_add_major, state.majorQuery.trim())) },
                        leadingIcon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    )
                }
            }
        }

        if (state.selectedMajor != null) {
            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.ob_interests), style = MaterialTheme.typography.titleMedium)
            if (state.aiGenerated) {
                Text(
                    stringResource(R.string.ob_ai_generated),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                state.interests.forEach { interest ->
                    FilterChip(
                        selected = interest.id in state.selectedInterestIds,
                        onClick = { onToggleInterest(interest.id) },
                        label = { Text(interest.name) },
                    )
                }
            }
            if (state.interestsLoading) {
                CircularProgressIndicator(Modifier.padding(top = 8.dp).height(24.dp))
            } else if (state.interestsHasMore) {
                TextButton(onClick = onLoadMore) { Text(stringResource(R.string.ob_load_more)) }
            }

            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.ob_salary), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.salary,
                onValueChange = onSalary,
                label = { Text(stringResource(R.string.ob_salary_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = { Text(stringResource(R.string.unit_usd)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(24.dp))
        Button(shape = TayanchControl.Shape, 
            onClick = onSubmit,
            enabled = canSubmit && !state.submitting,
            modifier = Modifier.fillMaxWidth().height(50.dp),
        ) {
            if (state.submitting) {
                CircularProgressIndicator(Modifier.height(22.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(stringResource(R.string.btn_complete_setup))
            }
        }

        Spacer(Modifier.height(16.dp))
        SecurityNote(stringResource(R.string.ob_security_note))
        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    TayanchTheme {
        OnboardingContent(
            state = PreviewSamples.onboardingState,
            filteredMajors = PreviewSamples.onboardingMajors,
            showAddMajor = false,
            canSubmit = true,
            onMajorQuery = {},
            onSelectMajor = {},
            onAddCustomMajor = {},
            onToggleInterest = {},
            onLoadMore = {},
            onSalary = {},
            onSubmit = {},
        )
    }
}
