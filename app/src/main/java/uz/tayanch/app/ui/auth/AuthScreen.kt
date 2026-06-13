package uz.tayanch.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import org.koin.androidx.compose.koinViewModel
import uz.tayanch.app.ui.theme.SuccessGreen
import uz.tayanch.app.ui.theme.TayanchTheme
import uz.tayanch.app.R
import uz.tayanch.app.ui.components.SecurityNote
import uz.tayanch.app.ui.preview.PreviewSamples
import uz.tayanch.app.ui.security.SecureScreenEffect
import uz.tayanch.app.ui.security.findActivity

@Composable
fun AuthScreen(
    onAuthenticated: (onboarded: Boolean) -> Unit,
    onForgotPassword: () -> Unit = {},
    vm: AuthViewModel = koinViewModel(),
) {
    // Credentials on screen → block screenshots/recording.
    SecureScreenEffect(antiTapjacking = false)

    val context = LocalContext.current
    val activity = context.findActivity() as? FragmentActivity
    val showBiometric = !vm.state.isRegister && vm.hasEnrolledSession &&
            activity != null && BiometricAuthenticator.isAvailable(context)

    AuthContent(
        state = vm.state,
        canSubmit = vm.canSubmit,
        showBiometric = showBiometric,
        onSetMode = vm::setMode,
        onPhone = vm::onPhone,
        onName = vm::onName,
        onAge = vm::onAge,
        onPassword = vm::onPassword,
        onSubmit = { vm.submit(onAuthenticated) },
        onForgotPassword = onForgotPassword,
        onBiometric = {
            activity?.let {
                BiometricAuthenticator.prompt(
                    activity = it,
                    onSuccess = { vm.completeBiometricUnlock(onAuthenticated) },
                    onError = { },
                )
            }
        },
    )
}

@Composable
private fun AuthContent(
    state: AuthUiState,
    canSubmit: Boolean,
    showBiometric: Boolean,
    onSetMode: (Boolean) -> Unit,
    onPhone: (String) -> Unit,
    onName: (String) -> Unit,
    onAge: (String) -> Unit,
    onPassword: (String) -> Unit,
    onSubmit: () -> Unit,
    onForgotPassword: () -> Unit,
    onBiometric: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(32.dp))
        Text(
            "Tayanch",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            stringResource(R.string.app_tagline),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(28.dp))

        val selectedTabIndex = if (state.isRegister) 1 else 0
        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier,
            containerColor = Color.Transparent,
            tabs = {
                Tab(
                    selected = !state.isRegister,
                    onClick = { onSetMode(false) },
                    text = { Text(stringResource(R.string.tab_login)) })
                Tab(
                    selected = state.isRegister,
                    onClick = { onSetMode(true) },
                    text = { Text(stringResource(R.string.tab_register)) })
            }
        )
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = state.phone,
            onValueChange = onPhone,
            label = { Text(stringResource(R.string.field_phone)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.isRegister) {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.fullName,
                onValueChange = onName,
                label = { Text(stringResource(R.string.field_full_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.age,
                onValueChange = onAge,
                label = { Text(stringResource(R.string.field_age)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = onPassword,
            label = { Text(stringResource(R.string.field_password)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.isRegister) {
            PasswordStrengthBar(password = state.password, modifier = Modifier.padding(top = 8.dp))
        }

        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                state.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onSubmit,
            enabled = canSubmit && !state.loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
        ) {
            if (state.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(stringResource(if (state.isRegister) R.string.btn_create_account else R.string.btn_login))
            }
        }

        if (!state.isRegister) {
            TextButton(onClick = onForgotPassword) { Text(stringResource(R.string.forgot_password)) }
        }

        if (showBiometric) {
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onBiometric, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Fingerprint, contentDescription = null)
                Text("  " + stringResource(R.string.btn_biometric_unlock))
            }
        }

        Spacer(Modifier.height(24.dp))
        SecurityNote(stringResource(R.string.auth_security_note))
    }
}

@Composable
private fun PasswordStrengthBar(password: String, modifier: Modifier = Modifier) {
    val strength = estimatePassword(password)
    val labelRes = when (strength.score) {
        0 -> R.string.pw_too_weak
        1 -> R.string.pw_weak
        2 -> R.string.pw_fair
        3 -> R.string.pw_good
        else -> R.string.pw_strong
    }
    val labelText = stringResource(labelRes)
    Column(modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(4) { index ->
                val active = index < strength.score
                Surface(
                    color = if (active) strength.color else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp),
                ) {}
            }
        }
        Text(
            text = if (strength.score < 3 && password.isNotEmpty()) {
                stringResource(R.string.pw_strength_hint, labelText)
            } else {
                stringResource(R.string.pw_strength_label, labelText)
            },
            style = MaterialTheme.typography.labelSmall,
            color = if (strength.score >= 3) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenPreview() {
    TayanchTheme {
        AuthContent(
            state = PreviewSamples.authState,
            canSubmit = true,
            showBiometric = false,
            onSetMode = {},
            onPhone = {},
            onName = {},
            onAge = {},
            onPassword = {},
            onSubmit = {},
            onForgotPassword = {},
            onBiometric = {},
        )
    }
}
