package com.nidhi.app.data.local.dao

import androidx.room.*
import com.nidhi.app.data.local.entity.FamilyMemberEntity
import com.nidhi.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUser(uid: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserOnce(uid: String): UserEntity?

    /** Suspend alias used by FirestoreSyncWorker. */
    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserById(uid: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM family_members WHERE userId = :userId ORDER BY name ASC")
    fun getFamilyMembers(userId: String): Flow<List<FamilyMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFamilyMember(member: FamilyMemberEntity)

    @Delete
    suspend fun deleteFamilyMember(member: FamilyMemberEntity)

    @Query("SELECT * FROM family_members WHERE id = :id")
    suspend fun getFamilyMemberById(id: String): FamilyMemberEntity?

    /** Returns all family members for a user as a plain list (used by sync push). */
    @Query("SELECT * FROM family_members WHERE userId = :userId")
    suspend fun getFamilyMembersDirect(userId: String): List<FamilyMemberEntity>
}
