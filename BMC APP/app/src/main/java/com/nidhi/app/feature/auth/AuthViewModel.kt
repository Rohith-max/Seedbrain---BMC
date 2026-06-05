package com.nidhi.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.domain.model.Result
import com.nidhi.app.domain.model.User
import com.nidhi.app.domain.repository.AuthRepository
import com.nidhi.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val isSignedIn: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.signInWithGoogle(idToken)) {
                is Result.Success -> onSignInSuccess(result.data)
                is Result.Error   -> _uiState.update { it.copy(isLoading = false, error = result.message ?: "Sign-in failed") }
                is Result.Loading -> Unit
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.signInWithEmail(email, password)) {
                is Result.Success -> onSignInSuccess(result.data)
                is Result.Error   -> _uiState.update { it.copy(isLoading = false, error = result.message ?: "Sign-in failed") }
                is Result.Loading -> Unit
            }
        }
    }

    fun signUpWithEmail(email: String, password: String, name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.signUpWithEmail(email, password, name)) {
                is Result.Success -> onSignInSuccess(result.data)
                is Result.Error   -> _uiState.update { it.copy(isLoading = false, error = result.message ?: "Sign-up failed") }
                is Result.Loading -> Unit
            }
        }
    }

    fun verifyOtp(verificationId: String, otp: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.verifyOtp(verificationId, otp)) {
                is Result.Success -> onSignInSuccess(result.data)
                is Result.Error   -> _uiState.update { it.copy(isLoading = false, error = result.message ?: "OTP failed") }
                is Result.Loading -> Unit
            }
        }
    }

    fun signInAsDemo() {
        viewModelScope.launch {
            userPreferences.setDemoMode(true)
            userPreferences.setOnboardingComplete(true)
            userPreferences.setCurrentUserId("demo_user")
            _uiState.update { it.copy(isSignedIn = true) }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }

    private suspend fun onSignInSuccess(user: User) {
        userRepository.saveUser(user)
        userPreferences.setCurrentUserId(user.uid)
        userPreferences.setOnboardingComplete(true)
        _uiState.update { it.copy(isLoading = false, user = user, isSignedIn = true) }
    }
}
