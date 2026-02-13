package com.milospavlovic4046.cryptotracker.presentation.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milospavlovic4046.cryptotracker.data.local.entity.PortfolioHoldingEntity
import com.milospavlovic4046.cryptotracker.model.CoinDto
import com.milospavlovic4046.cryptotracker.repository.MarketRepository
import com.milospavlovic4046.cryptotracker.repository.PortfolioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class HomeViewModel(
    private val repo: MarketRepository = MarketRepository(), // kasnije Hilt ubacimo
    private val portfolioRepo: PortfolioRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState(isLoading = true))
    val state: StateFlow<HomeUiState> = _state


    private var lastHoldings: List<PortfolioHoldingEntity> = emptyList()

    init {
        observeHoldingsAndCache()
        refresh()
    }

    fun onSearchChange(text: String) {
        _state.update { it.copy(searchText = text) }
        val coins = _state.value.coins
        _state.update { it.copy(filteredCoins = filterCoins(text, coins)) }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            runCatching {
                val coins = repo.fetchMarketCoins()
                val global = repo.fetchGlobalData()
                coins to global
            }.onSuccess { (coins, _) ->

                val filtered = filterCoins(_state.value.searchText, coins)

                val stats = listOf(
                    Statistic("Market", "CoinGecko"),
                    Statistic("Coins", coins.size.toString()),
                    // placeholder, preračunaćemo odmah ispod
                    Statistic(
                        "Portfolio",
                        _state.value.stats.firstOrNull { it.title == "Portfolio" }?.value ?: "$0.00",
                        0.0
                    )
                )

                _state.update {
                    it.copy(
                        isLoading = false,
                        coins = coins,
                        filteredCoins = filtered,
                        stats = stats
                    )
                }

                // ✅ 2) ČIM coins stignu, odmah preračunaj iz lastHoldings
                recomputePortfolioStat()
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    // ✅ 3) Samo cache-uje holdings, pa preračuna (ako coins već postoje)
    private fun observeHoldingsAndCache() {
        portfolioRepo.observeHoldings()
            .onEach { holdings ->
                lastHoldings = holdings
                recomputePortfolioStat()
            }
            .launchIn(viewModelScope)
    }

    // ✅ 4) Centralizovan obračun (radi i kad holdings stignu i kad coins stignu)
    private fun recomputePortfolioStat() {
        val coins = _state.value.coins
        if (coins.isEmpty()) return // još nema cena

        val priceMap = coins.associateBy({ it.id }, { it.currentPrice })
        val total = lastHoldings.sumOf { h -> (priceMap[h.coinId] ?: 0.0) * h.amount }

        _state.update { current ->
            val updatedStats = current.stats.toMutableList()
            val idx = updatedStats.indexOfFirst { it.title == "Portfolio" }

            val newStat = Statistic(
                "Portfolio",
                "$" + "%,.2f".format(total),
                0.0
            )

            if (idx >= 0) updatedStats[idx] = newStat else updatedStats.add(newStat)
            current.copy(stats = updatedStats)
        }
    }

    private fun filterCoins(text: String, coins: List<CoinDto>): List<CoinDto> {
        if (text.isBlank()) return coins
        val q = text.trim().lowercase()
        return coins.filter { coin ->
            coin.name.lowercase().contains(q) ||
                    coin.symbol.lowercase().contains(q) ||
                    coin.id.lowercase().contains(q)
        }
    }
}