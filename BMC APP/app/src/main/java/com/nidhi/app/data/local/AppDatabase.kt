package com.nidhi.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nidhi.app.data.local.dao.*
import com.nidhi.app.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        FamilyMemberEntity::class,
        DocumentEntity::class,
        DeletedDocumentEntity::class,
        BenefitEntity::class,
        AlertEntity::class,
        ConversationEntity::class,
        SettingEntity::class,
        VoiceHistoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun documentDao(): DocumentDao
    abstract fun benefitDao(): BenefitDao
    abstract fun alertDao(): AlertDao
    abstract fun conversationDao(): ConversationDao
    abstract fun tombstoneDao(): TombstoneDao

    companion object {
        /**
         * V1 → V2:
         *  - Add `updatedAt` column to family_members (defaults to 0 for existing rows)
         *  - Create `deleted_documents` tombstone table
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE family_members ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS deleted_documents (
                        documentId TEXT NOT NULL PRIMARY KEY,
                        deletedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
