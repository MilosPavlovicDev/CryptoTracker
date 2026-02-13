package com.milospavlovic4046.cryptotracker.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milospavlovic4046.cryptotracker.repository.PortfolioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PortfolioViewModel(
    private val repo: PortfolioRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PortfolioUiState())
    val state: StateFlow<PortfolioUiState> = _state

    init {
        repo.observeHoldings()
            .onEach { list -> _state.value = PortfolioUiState(holdings = list) }
            .launchIn(viewModelScope)
    }

    fun addOrUpdate(coinId: String, amount: Double) {
        viewModelScope.launch { repo.upsert(coinId, amount) }
    }

    fun delete(coinId: String) {
        viewModelScope.launch { repo.delete(coinId) }
    }

    fun clearAll() {
        viewModelScope.launch { repo.clearAll() }
    }
}