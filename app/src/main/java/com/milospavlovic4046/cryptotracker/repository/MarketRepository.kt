package com.milospavlovic4046.cryptotracker.repository

import com.milospavlovic4046.cryptotracker.model.CoinDto
import com.milospavlovic4046.cryptotracker.data.remote.CoinGeckoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MarketRepository(
    private val api: CoinGeckoApi = CoinGeckoApi()
) {
    suspend fun fetchMarketCoins(): List<CoinDto> = withContext(Dispatchers.IO) {
        api.getCoinsMarket()
    }

    suspend fun fetchGlobalData() = withContext(Dispatchers.IO) {
        api.getGlobal()
    }
}