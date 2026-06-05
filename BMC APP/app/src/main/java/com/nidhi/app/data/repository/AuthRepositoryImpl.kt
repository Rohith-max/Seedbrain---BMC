package com.nidhi.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import com.nidhi.app.BuildConfig
import com.nidhi.app.domain.model.Result
import com.nidhi.app.domain.model.User
import com.nidhi.app.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    // ── Simulated demo user (used when Firebase is not configured) ────────────
    private val demoUser = User(
        uid = "demo_user",
        email = "demo@nidhi.app",
        phone = "+91-9876543210",
        name = "Demo User",
        photoUri = null,
        createdAt = System.currentTimeMillis()
    )

    override val currentUser: Flow<User?> = callbackFlow {
        if (BuildConfig.USE_SIMULATED_LLM) {
            // In simulation mode, emit demo user if locally marked signed-in
            trySend(null) // start as signed-out; login sets it via prefs
            awaitClose {}
        } else {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                trySend(auth.currentUser?.toUser())
            }
            firebaseAuth.addAuthStateListener(listener)
            awaitClose { firebaseAuth.removeAuthStateListener(listener) }
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        if (BuildConfig.USE_SIMULATED_LLM) return simulateSuccess("google_user")
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user ?: return Result.Error(Exception("Sign-in failed"))
            Result.Success(user.toUser())
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun signInWithPhone(phone: String): Result<String> {
        return Result.Error(
            UnsupportedOperationException("Use signInWithPhoneCallback from the Auth screen")
        )
    }

    override suspend fun verifyOtp(verificationId: String, otp: String): Result<User> {
        if (BuildConfig.USE_SIMULATED_LLM) {
            // Accept any 6-digit OTP in simulation
            delay(800)
            return if (otp.length == 6) simulateSuccess("phone_user")
            else Result.Error(Exception("Invalid OTP. In demo mode enter any 6 digits."))
        }
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user ?: return Result.Error(Exception("OTP verification failed"))
            Result.Success(user.toUser())
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        if (BuildConfig.USE_SIMULATED_LLM) {
            delay(900) // simulate network call
            return if (email.isNotBlank() && password.length >= 6) {
                simulateSuccess(email.substringBefore("@"))
            } else {
                Result.Error(Exception("Email and password (min 6 chars) are required."))
            }
        }
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.Error(Exception("Sign-in failed"))
            Result.Success(user.toUser())
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        name: String
    ): Result<User> {
        if (BuildConfig.USE_SIMULATED_LLM) {
            delay(1000)
            return if (email.isNotBlank() && password.length >= 6 && name.isNotBlank()) {
                simulateSuccess(name, email)
            } else {
                Result.Error(Exception("All fields are required (password min 6 chars)."))
            }
        }
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.Error(Exception("Sign-up failed"))
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(name).build()
            user.updateProfile(profileUpdates).await()
            Result.Success(user.toUser(name))
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun signOut() {
        if (!BuildConfig.USE_SIMULATED_LLM) {
            firebaseAuth.signOut()
        }
    }

    override fun isSignedIn(): Boolean {
        return if (BuildConfig.USE_SIMULATED_LLM) false
        else firebaseAuth.currentUser != null
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun simulateSuccess(
        nameOrId: String,
        email: String? = null
    ): Result<User> {
        val uid = "demo_${nameOrId.replace(" ", "_").lowercase()}"
        return Result.Success(
            User(
                uid = uid,
                email = email ?: "$nameOrId@demo.nidhi",
                phone = null,
                name = nameOrId.replaceFirstChar { it.uppercase() },
                photoUri = null,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    private fun com.google.firebase.auth.FirebaseUser.toUser(displayName: String? = null) = User(
        uid = uid,
        email = email,
        phone = phoneNumber,
        name = displayName ?: this.displayName ?: email?.substringBefore("@") ?: "User",
        photoUri = photoUrl?.toString(),
        createdAt = metadata?.creationTimestamp ?: System.currentTimeMillis()
    )
}
