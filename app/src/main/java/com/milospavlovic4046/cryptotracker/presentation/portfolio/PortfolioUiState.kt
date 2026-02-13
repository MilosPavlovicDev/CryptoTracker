package com.milospavlovic4046.cryptotracker.presentation.portfolio

data class PortfolioUiState(
    val isLoadingPrices: Boolean = false,
    val error: String? = null,
    val items: List<PortfolioItemUi> = emptyList(),
    val totalValueUsd: Double = 0.0
)

data class PortfolioItemUi(
    val coinId: String,
    val name: String,
    val symbol: String,
    val imageUrl: String,
    val amount: Double,
    val currentPrice: Double,
    val change24h: Double?,
    val valueUsd: Double
)