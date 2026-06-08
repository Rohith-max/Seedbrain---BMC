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
// FK removed — demo_user may not exist in users table and would cause FK violation
@Entity(
    tableName = "family_members",
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
// FK removed — demo_user may not exist in users table and would cause FK violation
@Entity(
    tableName = "documents",
    indices = [Index("ownerId"), Index("expiryDate")]
)
data class DocumentEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val type: String,
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
    val category: String,
    val eligibilityCriteria: String,
    val requiredDocs: String,
    val officialUrl: String?,
    val status: String = "unknown"
)

// ── Alert ─────────────────────────────────────────────────────────────────────
// FK removed — demo_user may not exist in users table and would cause FK violation
@Entity(
    tableName = "alerts",
    indices = [Index("userId"), Index("triggerTime")]
)
data class AlertEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,
    val triggerTime: Long,
    val isRead: Boolean = false,
    val deepLink: String?,
    val createdAt: Long = System.currentTimeMillis()
)

// ── Conversation ──────────────────────────────────────────────────────────────
// FK removed — demo_user may not exist in users table, causing FK crash on first chat
@Entity(
    tableName = "conversations",
    indices = [Index("userId")]
)
data class ConversationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val messagesJson: String,
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
