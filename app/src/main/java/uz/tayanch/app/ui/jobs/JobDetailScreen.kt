package uz.tayanch.app.ui.jobs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import uz.tayanch.app.R
import uz.tayanch.app.data.dto.VacancyDto
import uz.tayanch.app.ui.components.StateContent
import uz.tayanch.app.ui.preview.PreviewSamples
import uz.tayanch.app.ui.theme.SuccessGreen
import uz.tayanch.app.ui.theme.TayanchTheme

@Composable
fun JobDetailScreen(
    vacancyId: String,
    onBack: () -> Unit,
    vm: JobDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(vacancyId) { vm.load(vacancyId) }
    StateContent(vm.state, onRetry = { vm.load(vacancyId) }) { vacancy ->
        JobDetailContent(
            vacancy = vacancy,
            applied = vm.applied,
            applying = vm.applying,
            onApply = { vm.apply(vacancyId) },
            onBack = onBack,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun JobDetailContent(
    vacancy: VacancyDto,
    applied: Boolean,
    applying: Boolean,
    onApply: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(vacancy.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }
                },
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                    Button(
                        onClick = onApply,
                        enabled = !applied && !applying,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        when {
                            applying -> CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                            applied -> { Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp)); Text("  " + stringResource(R.string.vacancy_applied)) }
                            else -> Text(stringResource(R.string.vacancy_apply))
                        }
                    }
                }
            }
        },
    ) { inner ->
        Column(
            Modifier.padding(inner).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(vacancy.company, style = MaterialTheme.typography.titleMedium)
            Text("${vacancy.location} · ${vacancy.type} · ${vacancy.level}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(vacancy.salary, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)

            if (vacancy.tags.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    vacancy.tags.forEach { tag ->
                        Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(6.dp)) {
                            Text(tag, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.vacancy_description), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(vacancy.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.vacancy_requirements), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            vacancy.requirements.forEach { req ->
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(8.dp))
                    Text(req, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun JobDetailScreenPreview() {
    TayanchTheme {
        JobDetailContent(
            vacancy = PreviewSamples.vacancy,
            applied = false,
            applying = false,
            onApply = {},
            onBack = {},
        )
    }
}
