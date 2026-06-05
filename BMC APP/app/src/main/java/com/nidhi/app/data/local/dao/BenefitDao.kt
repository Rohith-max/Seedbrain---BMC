package com.nidhi.app.data.local.dao

import androidx.room.*
import com.nidhi.app.data.local.entity.BenefitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BenefitDao {

    @Query("SELECT * FROM benefits ORDER BY name ASC")
    fun getAllBenefits(): Flow<List<BenefitEntity>>

    @Query("SELECT * FROM benefits WHERE status = :status ORDER BY name ASC")
    fun getBenefitsByStatus(status: String): Flow<List<BenefitEntity>>

    @Query("SELECT * FROM benefits WHERE category = :category ORDER BY name ASC")
    fun getBenefitsByCategory(category: String): Flow<List<BenefitEntity>>

    @Query("SELECT * FROM benefits WHERE id = :id")
    suspend fun getBenefitById(id: String): BenefitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBenefit(benefit: BenefitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBenefits(benefits: List<BenefitEntity>)

    @Query("UPDATE benefits SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("SELECT COUNT(*) FROM benefits WHERE status = 'eligible'")
    fun getEligibleCount(): Flow<Int>
}
