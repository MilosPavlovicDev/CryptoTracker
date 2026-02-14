package com.milospavlovic4046.cryptotracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: Int = 1, // single user row
    val name: String,
    val age: Int,
    val remoteId: String? = null
)