package com.milospavlovic4046.cryptotracker.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String? = null,
    val name: String,
    val age: Int
)