package com.milospavlovic4046.cryptotracker.presentation.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milospavlovic4046.cryptotracker.model.CoinDto
import com.milospavlovic4046.cryptotracker.repository.MarketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: MarketRepository = MarketRepository() // kasnije Hilt ubacimo
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState(isLoading = true))
    val state: StateFlow<HomeUiState> = _state

    init {
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
                val global = repo.fetchGlobalData() // trenutno ti je minimalan DTO, stats možemo kasnije proširiti
                coins to global
            }.onSuccess { (coins, _) ->
                val filtered = filterCoins(_state.value.searchText, coins)

                // Za sad stavi placeholder stats (dok ne popunimo MarketDataDto polja)
                val stats = listOf(
                    Statistic("Market", "CoinGecko"),
                    Statistic("Coins", coins.size.toString()),
                    Statistic("Portfolio", "$0.00", 0.0)
                )

                _state.update {
                    it.copy(
                        isLoading = false,
                        coins = coins,
                        filteredCoins = filtered,
                        stats = stats
                    )
                }
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