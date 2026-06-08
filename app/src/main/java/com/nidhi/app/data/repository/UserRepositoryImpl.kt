package com.nidhi.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nidhi.app.data.local.dao.UserDao
import com.nidhi.app.data.local.entity.FamilyMemberEntity
import com.nidhi.app.data.local.entity.UserEntity
import com.nidhi.app.domain.model.FamilyMember
import com.nidhi.app.domain.model.Result
import com.nidhi.app.domain.model.User
import com.nidhi.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore
) : UserRepository {

    override fun getUser(uid: String): Flow<User?> =
        userDao.getUser(uid).map { it?.toDomain() }

    override fun getFamilyMembers(userId: String): Flow<List<FamilyMember>> =
        userDao.getFamilyMembers(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun saveUser(user: User) {
        userDao.upsertUser(user.toEntity())
    }

    override suspend fun saveFamilyMember(member: FamilyMember) {
        userDao.upsertFamilyMember(member.toEntity())
    }

    override suspend fun deleteFamilyMember(member: FamilyMember) {
        userDao.deleteFamilyMember(member.toEntity())
    }

    override suspend fun syncWithFirestore(uid: String): Result<Unit> {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val data = doc.data
            if (data != null) {
                val user = UserEntity(
                    uid = uid,
                    email = data["email"] as? String,
                    phone = data["phone"] as? String,
                    name = data["name"] as? String ?: "User",
                    photoUri = data["photoUri"] as? String,
                    createdAt = (data["createdAt"] as? Long) ?: System.currentTimeMillis()
                )
                userDao.upsertUser(user)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    // Mappers
    private fun UserEntity.toDomain() = User(uid, email, phone, name, photoUri, createdAt)
    private fun User.toEntity() = UserEntity(uid, email, phone, name, photoUri, createdAt)
    private fun FamilyMemberEntity.toDomain() =
        FamilyMember(id, userId, name, relation, dob, photoUri, contactId)
    private fun FamilyMember.toEntity() =
        FamilyMemberEntity(id, userId, name, relation, dob, photoUri, contactId)
}
