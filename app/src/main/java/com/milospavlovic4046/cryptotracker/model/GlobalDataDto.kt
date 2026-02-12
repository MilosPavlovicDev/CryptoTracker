package com.milospavlovic4046.cryptotracker.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GlobalResponseDto(
    val data: MarketDataDto
)

@Serializable
data class MarketDataDto(
    @SerialName("active_cryptocurrencies")
    val activeCryptocurrencies: Int? = null,

    @SerialName("total_market_cap")
    val totalMarketCap: Map<String, Double>? = null,

    @SerialName("total_volume")
    val totalVolume: Map<String, Double>? = null,

    @SerialName("market_cap_percentage")
    val marketCapPercentage: Map<String, Double>? = null,

    @SerialName("market_cap_change_percentage_24h_usd")
    val marketCapChangePercentage24hUsd: Double? = null
)