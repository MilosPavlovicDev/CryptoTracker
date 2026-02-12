package com.milospavlovic4046.cryptotracker.presentation.components

import com.milospavlovic4046.cryptotracker.model.CoinDto

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchText: String = "",
    val coins: List<CoinDto> = emptyList(),
    val filteredCoins: List<CoinDto> = emptyList(),
    val stats: List<Statistic> = emptyList()
)

data class Statistic(
    val title: String,
    val value: String,
    val percentageChange: Double? = null
)