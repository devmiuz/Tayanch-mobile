package uz.tayanch.app.ui.resume

import uz.tayanch.app.ui.theme.TayanchControl

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.tayanch.app.R
import uz.tayanch.app.ui.theme.SuccessGreen
import uz.tayanch.app.ui.theme.TayanchTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeScreen(onBack: () -> Unit) {
    var fullName by remember { mutableStateOf("Jasur Abdullaev") }
    var headline by remember { mutableStateOf("Junior Android dasturchi · Kiberxavfsizlik") }
    var skills by remember { mutableStateOf("Kotlin, Jetpack Compose, OWASP, Git") }
    var experience by remember { mutableStateOf("Tayanch — Mobil dasturchi (amaliyot), 2025") }
    var education by remember { mutableStateOf("Xalqaro Islom Akademiyasi — Kiberxavfsizlik, 2020–2024") }
    var links by remember { mutableStateOf("github.com/jasur · linkedin.com/in/jasur") }
    var saved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.resume_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) } },
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                    Button(shape = TayanchControl.Shape, onClick = { saved = true }, modifier = Modifier.fillMaxWidth()) {
                        if (saved) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("  " + stringResource(R.string.resume_saved))
                        } else {
                            Text(stringResource(R.string.resume_save))
                        }
                    }
                }
            }
        },
    ) { inner ->
        Column(
            Modifier.padding(inner).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Field(stringResource(R.string.resume_full_name), fullName) { fullName = it; saved = false }
            Field(stringResource(R.string.resume_headline), headline) { headline = it; saved = false }
            Field(stringResource(R.string.resume_skills), skills) { skills = it; saved = false }
            Field(stringResource(R.string.resume_experience), experience, single = false) { experience = it; saved = false }
            Field(stringResource(R.string.resume_education), education, single = false) { education = it; saved = false }
            Field(stringResource(R.string.resume_links), links) { links = it; saved = false }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun Field(label: String, value: String, single: Boolean = true, onChange: (String) -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(value = value, onValueChange = onChange, singleLine = single, modifier = Modifier.fillMaxWidth())
    }
}

@Preview(showBackground = true)
@Composable
private fun ResumeScreenPreview() {
    TayanchTheme { ResumeScreen(onBack = {}) }
}
