package com.milospavlovic4046.cryptotracker.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milospavlovic4046.cryptotracker.data.local.entity.PortfolioHoldingEntity
import com.milospavlovic4046.cryptotracker.model.CoinDto
import com.milospavlovic4046.cryptotracker.repository.MarketRepository
import com.milospavlovic4046.cryptotracker.repository.PortfolioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val portfolioRepo: PortfolioRepository,
    private val marketRepo: MarketRepository            // ✅ Hilt inject (nema MarketRepository())
) : ViewModel() {

    private val marketCoins = MutableStateFlow<List<CoinDto>>(emptyList())

    private val _state = MutableStateFlow(PortfolioUiState(isLoadingPrices = true))
    val state: StateFlow<PortfolioUiState> = _state

    init {
        refreshMarketCoins()

        combine(
            portfolioRepo.observeHoldings(),
            marketCoins
        ) { holdings, coins ->
            buildUiState(holdings, coins)
        }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    fun refreshMarketCoins() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingPrices = true, error = null) }
            runCatching {
                marketRepo.fetchMarketCoins().take(250)
            }.onSuccess { coins ->
                marketCoins.value = coins
                _state.update { it.copy(isLoadingPrices = false) }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        isLoadingPrices = false,
                        error = e.message ?: "Failed to load live prices"
                    )
                }
            }
        }
    }

    private fun buildUiState(
        holdings: List<PortfolioHoldingEntity>,
        coins: List<CoinDto>
    ): PortfolioUiState {
        val coinMap = coins.associateBy { it.id }

        val items = holdings.map { h ->
            val coin = coinMap[h.coinId]
            val price = coin?.currentPrice ?: 0.0
            val value = h.amount * price

            PortfolioItemUi(
                coinId = h.coinId,
                name = coin?.name ?: h.coinId,
                symbol = (coin?.symbol ?: "").uppercase(),
                imageUrl = coin?.image ?: "",
                amount = h.amount,
                currentPrice = price,
                change24h = coin?.priceChangePercentage24H,
                valueUsd = value
            )
        }

        val total = items.sumOf { it.valueUsd }

        return _state.value.copy(
            items = items,
            totalValueUsd = total
        )
    }

    fun addOrUpdate(coinId: String, amount: Double) {
        viewModelScope.launch { portfolioRepo.upsert(coinId, amount) }
    }

    fun delete(coinId: String) {
        viewModelScope.launch { portfolioRepo.delete(coinId) }
    }

    fun clearAll() {
        viewModelScope.launch { portfolioRepo.clearAll() }
    }
    suspend fun loadCoinsForPicker(limit: Int = 150): List<CoinDto> {
        return marketRepo.fetchMarketCoins().take(limit)
    }
}