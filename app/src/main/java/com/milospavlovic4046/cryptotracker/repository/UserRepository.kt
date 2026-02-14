package com.milospavlovic4046.cryptotracker.repository

import com.milospavlovic4046.cryptotracker.data.local.dao.UserDao
import com.milospavlovic4046.cryptotracker.data.local.entity.UserEntity
import com.milospavlovic4046.cryptotracker.data.remote.UserApi
import com.milospavlovic4046.cryptotracker.data.remote.UserDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val userApi: UserApi
) {
    fun observeUser(): Flow<UserEntity?> = userDao.observeUser()

    suspend fun getUserOnce(): UserEntity? = userDao.getUserOnce()

    suspend fun refreshFromRemote() {
        runCatching { userApi.getUsers() }
            .onSuccess { users ->
                val u = users.firstOrNull() ?: return
                val remoteId = u.id ?: return

                userDao.upsertUser(
                    UserEntity(
                        id = 1,
                        name = u.name,
                        age = u.age,
                        remoteId = remoteId
                    )
                )
            }
    }
    suspend fun saveUser(name: String, age: Int) {
        val existing = userDao.getUserOnce()

        // 1) Local first (offline-safe)
        userDao.upsertUser(
            UserEntity(
                id = 1,
                name = name,
                age = age,
                remoteId = existing?.remoteId
            )
        )

        // 2) Remote sync best-effort
        val remoteId = existing?.remoteId
        if (!remoteId.isNullOrBlank()) {
            runCatching {
                userApi.updateUser(
                    remoteId = remoteId,
                    dto = UserDto(id = remoteId, name = name, age = age)
                )
            }
        } else {
            runCatching {
                userApi.createUser(UserDto(name = name, age = age))
            }.onSuccess { created ->
                val newRemoteId = created.id ?: return@onSuccess
                userDao.upsertUser(
                    UserEntity(id = 1, name = created.name, age = created.age, remoteId = newRemoteId)
                )
            }
        }
    }
    suspend fun clearUser() {
        val existing = userDao.getUserOnce()
        val remoteId = existing?.remoteId

        userDao.clearUser()

        if (!remoteId.isNullOrBlank()) {
            runCatching { userApi.deleteUser(remoteId) }
        }
    }
}