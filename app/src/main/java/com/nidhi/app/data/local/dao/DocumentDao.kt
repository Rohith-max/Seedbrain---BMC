package com.nidhi.app.data.local.dao

import androidx.room.*
import com.nidhi.app.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Query("SELECT * FROM documents WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    fun getDocuments(ownerId: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentById(id: String): DocumentEntity?

    @Query("""
        SELECT * FROM documents 
        WHERE ownerId = :ownerId 
        AND expiryDate IS NOT NULL 
        AND expiryDate > :now 
        ORDER BY expiryDate ASC 
        LIMIT :limit
    """)
    fun getUpcomingExpiryDocuments(
        ownerId: String,
        now: Long = System.currentTimeMillis(),
        limit: Int = 10
    ): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE ownerId = :ownerId AND type = :type ORDER BY createdAt DESC")
    fun getDocumentsByType(ownerId: String, type: String): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDocument(document: DocumentEntity)

    @Delete
    suspend fun deleteDocument(document: DocumentEntity)

    @Query("UPDATE documents SET ocrText = :ocrText, summaryJson = :summary, updatedAt = :now WHERE id = :id")
    suspend fun updateOcrAndSummary(
        id: String,
        ocrText: String?,
        summary: String?,
        now: Long = System.currentTimeMillis()
    )

    @Query("SELECT COUNT(*) FROM documents WHERE ownerId = :ownerId")
    fun getDocumentCount(ownerId: String): Flow<Int>
}
