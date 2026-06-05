package com.nidhi.app.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.nidhi.app.ui.components.ErrorBanner
import com.nidhi.app.ui.components.NidhiButton
import com.nidhi.app.ui.theme.Gold600

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToOtp: (verificationId: String, phone: String) -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSignedIn) {
        if (uiState.isSignedIn) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(72.dp))

        // Brand header
        Text(
            text = "NIDHI",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = if (isSignUp) "Create your account" else "Welcome back",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(48.dp))

        // Error
        ErrorBanner(
            message = uiState.error ?: "",
            visible = uiState.error != null,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Name field (sign-up only)
        AnimatedVisibility(visible = isSignUp) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                singleLine = true
            )
        }

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(12.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide" else "Show"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(24.dp))

        // Primary action
        NidhiButton(
            text = if (isSignUp) "Create Account" else "Sign In",
            onClick = {
                viewModel.clearError()
                if (isSignUp) {
                    viewModel.signUpWithEmail(email, password, name)
                } else {
                    viewModel.signInWithEmail(email, password)
                }
            },
            loading = uiState.isLoading,
            enabled = email.isNotBlank() && password.length >= 6
        )

        Spacer(Modifier.height(16.dp))

        // Toggle sign in / sign up
        TextButton(onClick = {
            isSignUp = !isSignUp
            viewModel.clearError()
        }) {
            Text(
                text = if (isSignUp) "Already have an account? Sign In"
                else "New to NIDHI? Create Account",
                textAlign = TextAlign.Center
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Demo mode
        OutlinedButton(
            onClick = { viewModel.signInAsDemo() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue in Demo Mode")
        }

        Spacer(Modifier.height(24.dp))
    }
}
