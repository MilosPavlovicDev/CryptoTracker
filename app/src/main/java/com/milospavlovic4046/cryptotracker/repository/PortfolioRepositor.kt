package com.milospavlovic4046.cryptotracker.repository

import com.milospavlovic4046.cryptotracker.data.local.dao.PortfolioDao
import com.milospavlovic4046.cryptotracker.data.local.entity.PortfolioHoldingEntity
import kotlinx.coroutines.flow.Flow

class PortfolioRepository(
    private val dao: PortfolioDao
) {
    fun observeHoldings(): Flow<List<PortfolioHoldingEntity>> = dao.observeHoldings()

    suspend fun upsert(coinId: String, amount: Double) {
        dao.upsertHolding(PortfolioHoldingEntity(coinId = coinId, amount = amount))
    }

    suspend fun delete(coinId: String) {
        dao.deleteHolding(coinId)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}