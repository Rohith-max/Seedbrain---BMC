package com.nidhi.app.data.local.dao

import androidx.room.*
import com.nidhi.app.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Query("SELECT * FROM alerts WHERE userId = :userId ORDER BY triggerTime ASC")
    fun getAlerts(userId: String): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE userId = :userId AND isRead = 0 ORDER BY triggerTime ASC")
    fun getUnreadAlerts(userId: String): Flow<List<AlertEntity>>

    @Query("SELECT COUNT(*) FROM alerts WHERE userId = :userId AND isRead = 0")
    fun getUnreadCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAlert(alert: AlertEntity)

    @Query("UPDATE alerts SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE alerts SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)

    @Delete
    suspend fun deleteAlert(alert: AlertEntity)

    @Query("DELETE FROM alerts WHERE userId = :userId AND triggerTime < :cutoff")
    suspend fun deleteOldAlerts(userId: String, cutoff: Long)
}
