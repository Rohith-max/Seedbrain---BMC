package com.nidhi.app.data.repository

import com.nidhi.app.data.local.dao.AlertDao
import com.nidhi.app.data.local.dao.DocumentDao
import com.nidhi.app.data.local.entity.AlertEntity
import com.nidhi.app.domain.model.Alert
import com.nidhi.app.domain.model.AlertType
import com.nidhi.app.domain.model.Result
import com.nidhi.app.domain.repository.AlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import java.util.concurrent.TimeUnit

class AlertRepositoryImpl(
    private val alertDao: AlertDao,
    private val documentDao: DocumentDao
) : AlertRepository {

    override fun getAlerts(userId: String): Flow<List<Alert>> =
        alertDao.getAlerts(userId).map { list -> list.map { it.toDomain() } }

    override fun getUnreadAlerts(userId: String): Flow<List<Alert>> =
        alertDao.getUnreadAlerts(userId).map { list -> list.map { it.toDomain() } }

    override fun getUnreadCount(userId: String): Flow<Int> =
        alertDao.getUnreadCount(userId)

    override suspend fun upsertAlert(alert: Alert) {
        alertDao.upsertAlert(alert.toEntity())
    }

    override suspend fun markAsRead(id: String) = alertDao.markAsRead(id)

    override suspend fun markAllAsRead(userId: String) = alertDao.markAllAsRead(userId)

    override suspend fun deleteAlert(alert: Alert) = alertDao.deleteAlert(alert.toEntity())

    override suspend fun scheduleExpiryAlerts(userId: String) {
        val now = System.currentTimeMillis()
        val thirtyDays = TimeUnit.DAYS.toMillis(30)
        val sevenDays = TimeUnit.DAYS.toMillis(7)

        val docs = documentDao.getUpcomingExpiryDocuments(userId, now, 50).first()

        docs.forEach { doc ->
            val expiry = doc.expiryDate ?: return@forEach
            val daysLeft = TimeUnit.MILLISECONDS.toDays(expiry - now)

            if (daysLeft <= 30) {
                val existingAlertId = "expiry_${doc.id}"
                val alert = AlertEntity(
                    id = existingAlertId,
                    userId = userId,
                    title = "Document expiring soon",
                    message = "${doc.title} expires in $daysLeft days",
                    type = "expiry",
                    triggerTime = expiry - sevenDays,
                    deepLink = "app://nidhi/document/${doc.id}"
                )
                alertDao.upsertAlert(alert)
            }
        }
    }

    // Mappers
    private fun AlertEntity.toDomain(): Alert {
        val type = try { AlertType.valueOf(this.type.uppercase()) }
        catch (_: Exception) { AlertType.GENERAL }
        return Alert(id, userId, title, message, type, triggerTime, isRead, deepLink)
    }

    private fun Alert.toEntity() = AlertEntity(
        id, userId, title, message,
        type.name.lowercase(), triggerTime, isRead, deepLink
    )
}
