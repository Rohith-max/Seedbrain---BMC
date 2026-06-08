package com.nidhi.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nidhi.app.data.local.dao.*
import com.nidhi.app.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        FamilyMemberEntity::class,
        DocumentEntity::class,
        BenefitEntity::class,
        AlertEntity::class,
        ConversationEntity::class,
        SettingEntity::class,
        VoiceHistoryEntity::class
    ],
    version = 2,          // bumped: removed FK constraints from conversations/documents/alerts
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun documentDao(): DocumentDao
    abstract fun benefitDao(): BenefitDao
    abstract fun alertDao(): AlertDao
    abstract fun conversationDao(): ConversationDao
}
