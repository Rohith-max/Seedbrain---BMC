package com.nidhi.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ── User ──────────────────────────────────────────────────────────────────────
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val email: String?,
    val phone: String?,
    val name: String,
    val photoUri: String?,
    val createdAt: Long = System.currentTimeMillis()
)

// ── FamilyMember ──────────────────────────────────────────────────────────────
@Entity(
    tableName = "family_members",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class FamilyMemberEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val relation: String,
    val dob: Long?,
    val photoUri: String?,
    val contactId: String?
)

// ── Document ──────────────────────────────────────────────────────────────────
@Entity(
    tableName = "documents",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["uid"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ownerId"), Index("expiryDate")]
)
data class DocumentEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val type: String,          // e.g. "Aadhaar", "PAN", "Passport"
    val title: String,
    val filePath: String,
    val thumbnailPath: String?,
    val ocrText: String?,
    val summaryJson: String?,
    val expiryDate: Long?,
    val linkedMemberId: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// ── Benefit ───────────────────────────────────────────────────────────────────
@Entity(tableName = "benefits")
data class BenefitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String,      // health, housing, education, etc.
    val eligibilityCriteria: String, // JSON
    val requiredDocs: String,        // JSON array
    val officialUrl: String?,
    val status: String = "unknown"   // eligible | missing_docs | ineligible | unknown
)

// ── Alert ─────────────────────────────────────────────────────────────────────
@Entity(
    tableName = "alerts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("triggerTime")]
)
data class AlertEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,          // expiry | deadline | benefit | general
    val triggerTime: Long,
    val isRead: Boolean = false,
    val deepLink: String?,
    val createdAt: Long = System.currentTimeMillis()
)

// ── Conversation ──────────────────────────────────────────────────────────────
@Entity(
    tableName = "conversations",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class ConversationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val messagesJson: String,   // JSON array of ChatMessage
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// ── Settings ──────────────────────────────────────────────────────────────────
@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

// ── Voice History ─────────────────────────────────────────────────────────────
@Entity(
    tableName = "voice_history",
    indices = [Index("userId")]
)
data class VoiceHistoryEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val transcript: String,
    val audioPath: String?,
    val timestamp: Long = System.currentTimeMillis()
)
