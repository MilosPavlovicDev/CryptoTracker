package com.milospavlovic4046.cryptotracker.repository

import com.milospavlovic4046.cryptotracker.data.local.dao.UserDao
import com.milospavlovic4046.cryptotracker.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val dao: UserDao
) {
    fun observeUser(): Flow<UserEntity?> = dao.observeUser()

    suspend fun getUserOnce(): UserEntity? = dao.getUserOnce()

    suspend fun saveUser(name: String, age: Int) {
        dao.upsertUser(UserEntity(id = 1, name = name.trim(), age = age))
    }

    suspend fun clearUser() {
        dao.clearUser()
    }
}