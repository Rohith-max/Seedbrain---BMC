package com.nidhi.app.data

import android.content.Context
import com.nidhi.app.data.local.AppDatabase
import com.nidhi.app.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Seeds the database with demo/sample data for the Demo Mode.
 */
class DemoDataSeeder(
    private val database: AppDatabase,
    private val context: Context
) {

    suspend fun seedDemoData() = withContext(Dispatchers.IO) {
        val demoUserId = "demo_user"

        // Skip if already seeded
        if (database.userDao().getUserOnce(demoUserId) != null) return@withContext

        // 1. User
        val user = UserEntity(
            uid = demoUserId,
            email = "demo@nidhi.app",
            phone = "+91-9876543210",
            name = "Demo Family",
            photoUri = null,
            createdAt = System.currentTimeMillis()
        )
        database.userDao().upsertUser(user)

        // 2. Family Members
        val members = listOf(
            FamilyMemberEntity(
                id = UUID.randomUUID().toString(),
                userId = demoUserId,
                name = "Rajesh Kumar",
                relation = "Self",
                dob = System.currentTimeMillis() - (40L * 365 * 24 * 3600 * 1000),
                photoUri = null,
                contactId = null
            ),
            FamilyMemberEntity(
                id = UUID.randomUUID().toString(),
                userId = demoUserId,
                name = "Priya Kumar",
                relation = "Spouse",
                dob = System.currentTimeMillis() - (38L * 365 * 24 * 3600 * 1000),
                photoUri = null,
                contactId = null
            ),
            FamilyMemberEntity(
                id = UUID.randomUUID().toString(),
                userId = demoUserId,
                name = "Aarav Kumar",
                relation = "Son",
                dob = System.currentTimeMillis() - (12L * 365 * 24 * 3600 * 1000),
                photoUri = null,
                contactId = null
            )
        )
        members.forEach { database.userDao().upsertFamilyMember(it) }

        // 3. Documents
        val now = System.currentTimeMillis()
        val documents = listOf(
            DocumentEntity(
                id = UUID.randomUUID().toString(),
                ownerId = demoUserId,
                type = "Aadhaar Card",
                title = "Rajesh's Aadhaar",
                filePath = "",
                thumbnailPath = null,
                ocrText = "Sample Aadhaar text",
                summaryJson = """{"headline":"Aadhaar Card for Rajesh Kumar","keyPoints":["ID: XXXX-XXXX-1234"],"expiryDateStr":null,"extractedIds":{"Aadhaar":"XXXX-XXXX-1234"}}""",
                expiryDate = null,
                linkedMemberId = members[0].id,
                createdAt = now - 30L * 24 * 3600 * 1000
            ),
            DocumentEntity(
                id = UUID.randomUUID().toString(),
                ownerId = demoUserId,
                type = "PAN Card",
                title = "Rajesh's PAN",
                filePath = "",
                thumbnailPath = null,
                ocrText = "Sample PAN text",
                summaryJson = """{"headline":"PAN Card","keyPoints":["PAN: ABCDE1234F"],"expiryDateStr":null,"extractedIds":{"PAN":"ABCDE1234F"}}""",
                expiryDate = null,
                linkedMemberId = members[0].id,
                createdAt = now - 60L * 24 * 3600 * 1000
            ),
            DocumentEntity(
                id = UUID.randomUUID().toString(),
                ownerId = demoUserId,
                type = "Passport",
                title = "Priya's Passport",
                filePath = "",
                thumbnailPath = null,
                ocrText = "Sample Passport text",
                summaryJson = """{"headline":"Indian Passport","keyPoints":["Passport No: J1234567"],"expiryDateStr":"15/06/2026","extractedIds":{"Passport":"J1234567"}}""",
                expiryDate = System.currentTimeMillis() + 180L * 24 * 3600 * 1000, // 6 months
                linkedMemberId = members[1].id,
                createdAt = now - 90L * 24 * 3600 * 1000
            ),
            DocumentEntity(
                id = UUID.randomUUID().toString(),
                ownerId = demoUserId,
                type = "Insurance Policy",
                title = "Family Health Insurance",
                filePath = "",
                thumbnailPath = null,
                ocrText = "Sample insurance policy",
                summaryJson = """{"headline":"Health Insurance","keyPoints":["Cover: ₹5 lakh","Expires soon"],"expiryDateStr":"01/08/2026","extractedIds":{}}""",
                expiryDate = now + 60L * 24 * 3600 * 1000, // 2 months
                linkedMemberId = null,
                createdAt = now - 120L * 24 * 3600 * 1000
            )
        )
        documents.forEach { database.documentDao().upsertDocument(it) }

        // 4. Alerts
        val alerts = listOf(
            AlertEntity(
                id = UUID.randomUUID().toString(),
                userId = demoUserId,
                title = "Passport expiring soon",
                message = "Priya's passport will expire in 6 months. Start renewal process.",
                type = "expiry",
                triggerTime = now + 150L * 24 * 3600 * 1000,
                isRead = false,
                deepLink = "app://nidhi/document/${documents[2].id}"
            ),
            AlertEntity(
                id = UUID.randomUUID().toString(),
                userId = demoUserId,
                title = "Health Insurance renewal",
                message = "Family health insurance expires in 2 months.",
                type = "deadline",
                triggerTime = now + 30L * 24 * 3600 * 1000,
                isRead = false,
                deepLink = "app://nidhi/document/${documents[3].id}"
            ),
            AlertEntity(
                id = UUID.randomUUID().toString(),
                userId = demoUserId,
                title = "New benefit discovered",
                message = "You may be eligible for PM-KISAN scheme. Check benefits section.",
                type = "benefit",
                triggerTime = now - 2L * 24 * 3600 * 1000,
                isRead = true,
                deepLink = "app://nidhi/benefits"
            )
        )
        alerts.forEach { database.alertDao().upsertAlert(it) }

        // 5. Sample Conversation
        val conversation = ConversationEntity(
            id = UUID.randomUUID().toString(),
            userId = demoUserId,
            title = "What benefits am I eligible for?",
            messagesJson = """[{"id":"1","role":"USER","content":"What government benefits am I eligible for?","timestamp":${now - 60000},"isStreaming":false},{"id":"2","role":"ASSISTANT","content":"Based on your profile, here are some schemes you may qualify for:\\n\\n1. **PM-KISAN** – If you own farmland, you can get ₹6,000/year.\\n2. **Ayushman Bharat (PMJAY)** – Health cover of ₹5 lakh per family.\\n3. **Sukanya Samriddhi** – If you have a daughter below 10 years.\\n\\nWould you like me to help with application steps for any of these?","timestamp":${now - 55000},"isStreaming":false}]""",
            createdAt = now - 60000,
            updatedAt = now - 55000
        )
        database.conversationDao().upsertConversation(conversation)
    }

    suspend fun clearDemoData() = withContext(Dispatchers.IO) {
        database.clearAllTables()
    }
}
