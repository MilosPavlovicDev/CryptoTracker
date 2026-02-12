package com.milospavlovic4046.cryptotracker.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinDto(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,

    @SerialName("current_price")
    val currentPrice: Double,

    @SerialName("market_cap")
    val marketCap: Double? = null,

    @SerialName("market_cap_rank")
    val marketCapRank: Int? = null,

    @SerialName("fully_diluted_valuation")
    val fullyDilutedValuation: Double? = null,

    @SerialName("total_volume")
    val totalVolume: Double? = null,

    @SerialName("high_24h")
    val high24H: Double? = null,

    @SerialName("low_24h")
    val low24H: Double? = null,

    @SerialName("price_change_24h")
    val priceChange24H: Double? = null,

    @SerialName("price_change_percentage_24h")
    val priceChangePercentage24H: Double? = null,

    @SerialName("market_cap_change_24h")
    val marketCapChange24H: Double? = null,

    @SerialName("market_cap_change_percentage_24h")
    val marketCapChangePercentage24H: Double? = null,

    @SerialName("circulating_supply")
    val circulatingSupply: Double? = null,

    @SerialName("total_supply")
    val totalSupply: Double? = null,

    @SerialName("max_supply")
    val maxSupply: Double? = null,

    val ath: Double? = null,

    @SerialName("ath_change_percentage")
    val athChangePercentage: Double? = null,

    @SerialName("ath_date")
    val athDate: String? = null,

    val atl: Double? = null,

    @SerialName("atl_change_percentage")
    val atlChangePercentage: Double? = null,

    @SerialName("atl_date")
    val atlDate: String? = null,

    @SerialName("last_updated")
    val lastUpdated: String? = null,

    @SerialName("sparkline_in_7d")
    val sparklineIn7D: SparklineIn7DDto? = null,

    @SerialName("price_change_percentage_24h_in_currency")
    val priceChangePercentage24HInCurrency: Double? = null
)

@Serializable
data class SparklineIn7DDto(
    val price: List<Double>? = null
)