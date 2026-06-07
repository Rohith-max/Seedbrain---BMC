package com.nidhi.app.data.repository

import android.content.Context
import com.nidhi.app.data.local.dao.BenefitDao
import com.nidhi.app.data.local.entity.BenefitEntity
import com.nidhi.app.domain.model.Benefit
import com.nidhi.app.domain.model.BenefitStatus
import com.nidhi.app.domain.model.EligibilityCriterion
import com.nidhi.app.domain.model.Result
import com.nidhi.app.domain.repository.BenefitRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BenefitRepositoryImpl(
    private val benefitDao: BenefitDao,
    private val moshi: Moshi,
    private val context: Context
) : BenefitRepository {

    override fun getAllBenefits(): Flow<List<Benefit>> =
        benefitDao.getAllBenefits().map { list -> list.map { it.toDomain(moshi) } }

    override fun getBenefitsByStatus(status: BenefitStatus): Flow<List<Benefit>> =
        benefitDao.getBenefitsByStatus(status.name.lowercase())
            .map { list -> list.map { it.toDomain(moshi) } }

    override suspend fun loadBenefitsFromAssets(): Result<Unit> {
        return try {
            val json = context.assets.open("benefits.json").bufferedReader().use { it.readText() }
            val type = Types.newParameterizedType(List::class.java, BenefitJson::class.java)
            val adapter = moshi.adapter<List<BenefitJson>>(type)
            val benefitJsonList = adapter.fromJson(json) ?: emptyList()
            val entities = benefitJsonList.map { it.toEntity() }
            benefitDao.upsertBenefits(entities)
            Result.Success(Unit)
        } catch (e: Exception) {
            // If asset not found, load hardcoded defaults
            loadDefaultBenefits()
            Result.Success(Unit)
        }
    }

    override suspend fun evaluateEligibility(userId: String): Result<Unit> {
        // Stub: real implementation would check user profile & documents
        // For now mark first few as eligible for demo
        return Result.Success(Unit)
    }

    override fun getEligibleCount(): Flow<Int> = benefitDao.getEligibleCount()

    private suspend fun loadDefaultBenefits() {
        val defaults = defaultBenefits()
        benefitDao.upsertBenefits(defaults)
    }

    // ── Mappers ───────────────────────────────────────────────────────────────
    private fun BenefitEntity.toDomain(moshi: Moshi): Benefit {
        val criteriaType = Types.newParameterizedType(List::class.java, EligibilityCriterion::class.java)
        val docsType = Types.newParameterizedType(List::class.java, String::class.java)
        val criteria = try {
            moshi.adapter<List<EligibilityCriterion>>(criteriaType)
                .fromJson(eligibilityCriteria) ?: emptyList()
        } catch (_: Exception) { emptyList() }
        val docs = try {
            moshi.adapter<List<String>>(docsType).fromJson(requiredDocs) ?: emptyList()
        } catch (_: Exception) { emptyList() }
        val statusEnum = try { BenefitStatus.valueOf(status.uppercase()) }
        catch (_: Exception) { BenefitStatus.UNKNOWN }
        return Benefit(id, name, description, category, criteria, docs, officialUrl, statusEnum)
    }

    data class BenefitJson(
        val id: String,
        val name: String,
        val description: String,
        val category: String,
        val eligibilityCriteria: String,
        val requiredDocs: String,
        val officialUrl: String?
    ) {
        fun toEntity() = BenefitEntity(id, name, description, category,
            eligibilityCriteria, requiredDocs, officialUrl)
    }

    private fun defaultBenefits(): List<BenefitEntity> = listOf(
        BenefitEntity(
            id = "pmay_gramin",
            name = "PMAY-Gramin",
            description = "Pradhan Mantri Awas Yojana – Rural housing subsidy for BPL families",
            category = "housing",
            eligibilityCriteria = """[{"field":"income","operator":"lt","value":"180000","description":"Annual income below ₹1,80,000"}]""",
            requiredDocs = """["Aadhaar","Income Certificate","Bank Passbook"]""",
            officialUrl = "https://pmayg.nic.in",
            status = "unknown"
        ),
        BenefitEntity(
            id = "ayushman_bharat",
            name = "Ayushman Bharat – PMJAY",
            description = "Health cover of ₹5 lakh per family per year for secondary and tertiary care",
            category = "health",
            eligibilityCriteria = """[{"field":"secc_listed","operator":"eq","value":"true","description":"Listed in SECC database"}]""",
            requiredDocs = """["Aadhaar","Ration Card"]""",
            officialUrl = "https://pmjay.gov.in",
            status = "unknown"
        ),
        BenefitEntity(
            id = "pm_kisan",
            name = "PM-KISAN",
            description = "₹6,000 per year income support to farmer families",
            category = "agriculture",
            eligibilityCriteria = """[{"field":"farmer","operator":"eq","value":"true","description":"Land-owning farmer family"}]""",
            requiredDocs = """["Aadhaar","Land Records","Bank Passbook"]""",
            officialUrl = "https://pmkisan.gov.in",
            status = "unknown"
        ),
        BenefitEntity(
            id = "sukanya_samriddhi",
            name = "Sukanya Samriddhi Yojana",
            description = "Savings scheme for girl child with high interest rate and tax benefits",
            category = "savings",
            eligibilityCriteria = """[{"field":"girl_child_age","operator":"lt","value":"10","description":"Girl child below 10 years"}]""",
            requiredDocs = """["Aadhaar","Birth Certificate","Parent Aadhaar"]""",
            officialUrl = "https://www.indiapost.gov.in",
            status = "unknown"
        ),
        BenefitEntity(
            id = "mudra_loan",
            name = "PM Mudra Yojana",
            description = "Loans up to ₹10 lakh for non-corporate, non-farm small businesses",
            category = "business",
            eligibilityCriteria = """[{"field":"business_type","operator":"eq","value":"micro","description":"Micro or small enterprise"}]""",
            requiredDocs = """["Aadhaar","PAN","Bank Statement","Business Proof"]""",
            officialUrl = "https://mudra.org.in",
            status = "unknown"
        ),
        BenefitEntity(
            id = "nsc",
            name = "National Savings Certificate",
            description = "Fixed-income investment scheme with tax benefit under Section 80C",
            category = "savings",
            eligibilityCriteria = """[]""",
            requiredDocs = """["Aadhaar","PAN"]""",
            officialUrl = "https://www.indiapost.gov.in",
            status = "unknown"
        ),
        BenefitEntity(
            id = "atal_pension",
            name = "Atal Pension Yojana",
            description = "Pension scheme for unorganised sector workers aged 18–40",
            category = "pension",
            eligibilityCriteria = """[{"field":"age","operator":"lt","value":"40","description":"Age below 40 years"}]""",
            requiredDocs = """["Aadhaar","Bank Account"]""",
            officialUrl = "https://www.npscra.nsdl.co.in",
            status = "unknown"
        ),
        BenefitEntity(
            id = "pmsby",
            name = "PMSBY – Accident Insurance",
            description = "₹2 lakh accident insurance at ₹20/year premium",
            category = "insurance",
            eligibilityCriteria = """[{"field":"age","operator":"lt","value":"70","description":"Age 18–70 years"}]""",
            requiredDocs = """["Aadhaar","Bank Account"]""",
            officialUrl = "https://financialservices.gov.in",
            status = "unknown"
        )
    )
}
