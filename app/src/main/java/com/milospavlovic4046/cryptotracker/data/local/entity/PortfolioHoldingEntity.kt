package com.milospavlovic4046.cryptotracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "portfolio_holdings")
data class PortfolioHoldingEntity(
    @PrimaryKey val coinId: String,
    val amount: Double,
    val updatedAt: Long = System.currentTimeMillis()
)