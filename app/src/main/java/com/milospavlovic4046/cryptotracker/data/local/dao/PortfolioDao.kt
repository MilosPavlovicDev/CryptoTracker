package com.milospavlovic4046.cryptotracker.data.local.dao

import androidx.room.*
import com.milospavlovic4046.cryptotracker.data.local.entity.PortfolioHoldingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {

    @Query("SELECT * FROM portfolio_holdings ORDER BY updatedAt DESC")
    fun observeHoldings(): Flow<List<PortfolioHoldingEntity>>

    @Query("SELECT * FROM portfolio_holdings WHERE coinId = :coinId LIMIT 1")
    suspend fun getHolding(coinId: String): PortfolioHoldingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHolding(entity: PortfolioHoldingEntity)

    @Query("DELETE FROM portfolio_holdings WHERE coinId = :coinId")
    suspend fun deleteHolding(coinId: String)

    @Query("DELETE FROM portfolio_holdings")
    suspend fun clearAll()
}