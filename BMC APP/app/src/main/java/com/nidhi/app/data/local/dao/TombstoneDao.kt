package com.nidhi.app.data.local.dao

import androidx.room.*
import com.nidhi.app.data.local.entity.DeletedDocumentEntity

@Dao
interface TombstoneDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DeletedDocumentEntity)

    @Query("SELECT COUNT(*) > 0 FROM deleted_documents WHERE documentId = :documentId")
    suspend fun isDeleted(documentId: String): Boolean

    @Query("DELETE FROM deleted_documents WHERE documentId = :documentId")
    suspend fun remove(documentId: String)

    @Query("SELECT documentId FROM deleted_documents")
    suspend fun getAllDeletedIds(): List<String>
}
