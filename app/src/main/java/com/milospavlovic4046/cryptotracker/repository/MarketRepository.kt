package com.milospavlovic4046.cryptotracker.repository

import com.milospavlovic4046.cryptotracker.data.remote.CoinGeckoApi
import com.milospavlovic4046.cryptotracker.model.CoinDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MarketRepository @Inject constructor(
    private val api: CoinGeckoApi
) {
    suspend fun fetchMarketCoins(): List<CoinDto> = withContext(Dispatchers.IO) {
        api.getCoinsMarket()
    }

    suspend fun fetchGlobalData() = withContext(Dispatchers.IO) {
        api.getGlobal()
    }
}