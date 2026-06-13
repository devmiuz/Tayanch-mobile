package uz.tayanch.app.ui.jobs

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import uz.tayanch.app.R
import uz.tayanch.app.data.dto.VacancyDto
import uz.tayanch.app.data.dto.VacancyListResponse
import uz.tayanch.app.ui.components.StateContent
import uz.tayanch.app.ui.preview.PreviewSamples
import uz.tayanch.app.ui.theme.TayanchTheme

@Composable
fun JobsScreen(
    contentPadding: PaddingValues,
    onOpenVacancy: (String) -> Unit,
    vm: JobsViewModel = koinViewModel(),
) {
    StateContent(vm.state, Modifier.padding(contentPadding).fillMaxSize(), onRetry = vm::load) { data ->
        JobsContent(
            data = data,
            query = vm.query,
            category = vm.category,
            visible = vm.filter(data.vacancies),
            onQuery = vm::onQuery,
            onCategory = vm::selectCategory,
            onOpenVacancy = onOpenVacancy,
            contentPadding = contentPadding,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JobsContent(
    data: VacancyListResponse,
    query: String,
    category: String,
    visible: List<VacancyDto>,
    onQuery: (String) -> Unit,
    onCategory: (String) -> Unit,
    onOpenVacancy: (String) -> Unit,
    contentPadding: PaddingValues,
) {
    Column(Modifier.padding(contentPadding).fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQuery,
            label = { Text(stringResource(R.string.jobs_search)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(data.categories) { cat ->
                FilterChip(selected = category == cat, onClick = { onCategory(cat) }, label = { Text(cat) })
            }
        }
        Spacer(Modifier.height(12.dp))
        if (visible.isEmpty()) {
            Text(stringResource(R.string.jobs_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(visible, key = { it.id }) { v -> VacancyCard(v, onOpenVacancy) }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VacancyCard(v: VacancyDto, onOpen: (String) -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onOpen(v.id) }) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(v.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(6.dp)) {
                    Text(v.level, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
            }
            Text("${v.company} · ${v.location} · ${v.type}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(v.salary, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            if (v.tags.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    v.tags.forEach { tag ->
                        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(6.dp)) {
                            Text(tag, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun JobsScreenPreview() {
    TayanchTheme {
        JobsContent(
            data = PreviewSamples.vacancyList,
            query = "",
            category = "Barchasi",
            visible = PreviewSamples.vacancyList.vacancies,
            onQuery = {},
            onCategory = {},
            onOpenVacancy = {},
            contentPadding = PaddingValues(0.dp),
        )
    }
}
