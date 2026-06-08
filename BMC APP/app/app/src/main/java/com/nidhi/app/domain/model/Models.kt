package com.nidhi.app.domain.model

import kotlinx.serialization.Serializable

// ── User ──────────────────────────────────────────────────────────────────────
data class User(
    val uid: String,
    val email: String?,
    val phone: String?,
    val name: String,
    val photoUri: String?,
    val createdAt: Long
)

data class FamilyMember(
    val id: String,
    val userId: String,
    val name: String,
    val relation: String,
    val dob: Long?,
    val photoUri: String?,
    val contactId: String?
)

// ── Document ──────────────────────────────────────────────────────────────────
data class Document(
    val id: String,
    val ownerId: String,
    val type: String,
    val title: String,
    val filePath: String,
    val thumbnailPath: String?,
    val ocrText: String?,
    val summary: DocumentSummary?,
    val expiryDate: Long?,
    val linkedMemberId: String?,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class DocumentSummary(
    val headline: String,
    val keyPoints: List<String>,
    val expiryDateStr: String?,
    val extractedIds: Map<String, String>  // e.g. "Aadhaar" -> "XXXX-XXXX-XXXX"
)

// ── Benefit ───────────────────────────────────────────────────────────────────
data class Benefit(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val eligibilityCriteria: List<EligibilityCriterion>,
    val requiredDocTypes: List<String>,
    val officialUrl: String?,
    val status: BenefitStatus
)

data class EligibilityCriterion(
    val field: String,
    val operator: String,   // eq | lt | gt | contains
    val value: String,
    val description: String
)

enum class BenefitStatus {
    ELIGIBLE, MISSING_DOCS, INELIGIBLE, UNKNOWN
}

// ── Alert ─────────────────────────────────────────────────────────────────────
data class Alert(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: AlertType,
    val triggerTime: Long,
    val isRead: Boolean,
    val deepLink: String?
)

enum class AlertType {
    EXPIRY, DEADLINE, BENEFIT, GENERAL
}

// ── AI Chat ───────────────────────────────────────────────────────────────────
@Serializable
data class ChatMessage(
    val id: String,
    val role: ChatRole,
    val content: String,
    val timestamp: Long,
    val isStreaming: Boolean = false
)

@Serializable
enum class ChatRole { USER, ASSISTANT, SYSTEM }

data class Conversation(
    val id: String,
    val userId: String,
    val title: String,
    val messages: List<ChatMessage>,
    val createdAt: Long,
    val updatedAt: Long
)

// ── Health Score ──────────────────────────────────────────────────────────────
data class FamilyHealthScore(
    val score: Int,          // 0–100
    val documentsScore: Int,
    val benefitsScore: Int,
    val alertsScore: Int,
    val insights: List<String>
)

// ── Emergency ─────────────────────────────────────────────────────────────────
data class EmergencyContact(
    val id: String,
    val name: String,
    val phone: String,
    val relation: String
)

// ── Result wrapper ────────────────────────────────────────────────────────────
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable, val message: String? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

fun <T> Result<T>.isSuccess() = this is Result.Success
fun <T> Result<T>.getOrNull() = (this as? Result.Success)?.data
