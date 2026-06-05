package com.nidhi.app.domain.repository

import com.nidhi.app.domain.model.*
import kotlinx.coroutines.flow.Flow

// ── Auth ──────────────────────────────────────────────────────────────────────
interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signInWithPhone(phone: String): Result<String>  // returns verificationId
    suspend fun verifyOtp(verificationId: String, otp: String): Result<User>
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun signUpWithEmail(email: String, password: String, name: String): Result<User>
    suspend fun signOut()
    fun isSignedIn(): Boolean
}

// ── User ──────────────────────────────────────────────────────────────────────
interface UserRepository {
    fun getUser(uid: String): Flow<User?>
    fun getFamilyMembers(userId: String): Flow<List<FamilyMember>>
    suspend fun saveUser(user: User)
    suspend fun saveFamilyMember(member: FamilyMember)
    suspend fun deleteFamilyMember(member: FamilyMember)
    suspend fun syncWithFirestore(uid: String): Result<Unit>
}

// ── Document ──────────────────────────────────────────────────────────────────
interface DocumentRepository {
    fun getDocuments(ownerId: String): Flow<List<Document>>
    fun getUpcomingExpiryDocuments(ownerId: String): Flow<List<Document>>
    suspend fun getDocumentById(id: String): Document?
    suspend fun saveDocument(document: Document)
    suspend fun deleteDocument(document: Document)
    suspend fun performOcr(imagePath: String): Result<String>
    suspend fun generateSummary(ocrText: String, documentType: String): Result<DocumentSummary>
    suspend fun updateOcrAndSummary(id: String, ocrText: String?, summary: DocumentSummary?)
}

// ── Benefits ──────────────────────────────────────────────────────────────────
interface BenefitRepository {
    fun getAllBenefits(): Flow<List<Benefit>>
    fun getBenefitsByStatus(status: BenefitStatus): Flow<List<Benefit>>
    suspend fun loadBenefitsFromAssets(): Result<Unit>
    suspend fun evaluateEligibility(userId: String): Result<Unit>
    fun getEligibleCount(): Flow<Int>
}

// ── Alerts ────────────────────────────────────────────────────────────────────
interface AlertRepository {
    fun getAlerts(userId: String): Flow<List<Alert>>
    fun getUnreadAlerts(userId: String): Flow<List<Alert>>
    fun getUnreadCount(userId: String): Flow<Int>
    suspend fun upsertAlert(alert: Alert)
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead(userId: String)
    suspend fun deleteAlert(alert: Alert)
    suspend fun scheduleExpiryAlerts(userId: String)
}

// ── AI ────────────────────────────────────────────────────────────────────────
interface AiRepository {
    fun getConversations(userId: String): Flow<List<Conversation>>
    suspend fun getConversationById(id: String): Conversation?
    suspend fun sendMessage(conversationId: String, message: String): Flow<Result<ChatMessage>>
    suspend fun saveConversation(conversation: Conversation)
    suspend fun deleteConversation(conversation: Conversation)
    suspend fun clearAllConversations(userId: String)
}
