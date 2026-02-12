package com.milospavlovic4046.cryptotracker.data.remote

import com.milospavlovic4046.cryptotracker.model.CoinDto
import com.milospavlovic4046.cryptotracker.model.GlobalResponseDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class CoinGeckoApi(
    private val client: io.ktor.client.HttpClient = KtorClient.httpClient
) {
    suspend fun getCoinsMarket(
        vsCurrency: String = "usd",
        perPage: Int = 50,
        page: Int = 1
    ): List<CoinDto> {
        return client.get("https://api.coingecko.com/api/v3/coins/markets") {
            parameter("vs_currency", vsCurrency)
            parameter("order", "market_cap_desc")
            parameter("per_page", perPage)
            parameter("page", page)
            parameter("sparkline", "true")
            parameter("price_change_percentage", "24h")
            parameter("locale", "en")
        }.body()
    }

    suspend fun getGlobal(): GlobalResponseDto {
        return client.get("https://api.coingecko.com/api/v3/global").body()
    }
}