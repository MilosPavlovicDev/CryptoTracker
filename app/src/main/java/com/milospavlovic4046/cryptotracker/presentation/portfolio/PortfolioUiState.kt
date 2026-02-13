package com.milospavlovic4046.cryptotracker.presentation.portfolio

import com.milospavlovic4046.cryptotracker.data.local.entity.PortfolioHoldingEntity

data class PortfolioUiState(
    val holdings: List<PortfolioHoldingEntity> = emptyList()
)