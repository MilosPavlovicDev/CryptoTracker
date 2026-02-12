package com.milospavlovic4046.cryptotracker.model


import kotlinx.serialization.Serializable

@Serializable
data class GlobalResponseDto(
    val data: MarketDataDto
)

@Serializable
data class MarketDataDto(

)