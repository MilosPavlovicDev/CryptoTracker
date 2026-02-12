package com.milospavlovic4046.cryptotracker.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.milospavlovic4046.cryptotracker.presentation.components.SearchBar
import com.milospavlovic4046.cryptotracker.presentation.components.StatisticView
import androidx.compose.foundation.layout.statusBarsPadding

@Composable
fun HomeScreen(vm: HomeViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {

        // Header (minimalno, posle ga "ulepšamo" kao na iOS-u)
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Live Prices", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { vm.refresh() }) { Text("Refresh") }
        }

        // Stats row
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            state.stats.take(3).forEach { stat ->
                StatisticView(stat = stat, modifier = Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(8.dp))

        // Search
        SearchBar(
            value = state.searchText,
            onValueChange = vm::onSearchChange
        )

        Spacer(Modifier.height(8.dp))

        // Loading / Error / List
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Column(Modifier.padding(16.dp)) {
                    Text("Error: ${state.error}")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { vm.refresh() }) { Text("Retry") }
                }
            }
            else -> {
                LazyColumn {
                    items(state.filteredCoins) { coin ->
                        CoinRow(coin)
                        Divider()
                    }
                }
            }
        }
    }
}