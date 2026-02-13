package com.milospavlovic4046.cryptotracker.presentation.portfolio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.milospavlovic4046.cryptotracker.model.CoinDto
import com.milospavlovic4046.cryptotracker.repository.MarketRepository
import com.milospavlovic4046.cryptotracker.presentation.components.CoinIcon
import com.milospavlovic4046.cryptotracker.presentation.components.SearchBar
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun PortfolioScreen(
    vm: PortfolioViewModel,
    modifier: Modifier = Modifier
) {
    val state by vm.state.collectAsStateWithLifecycle()

    var showEditor by remember { mutableStateOf(false) }
    var editingCoinId by remember { mutableStateOf<String?>(null) } // null = add mode

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Portfolio", style = MaterialTheme.typography.titleLarge)

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { vm.clearAll() }) { Text("Clear") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    editingCoinId = null
                    showEditor = true
                }) { Text("Add") }
            }
        }

        if (state.holdings.isEmpty()) {
            Text(
                text = "No coins in portfolio yet.",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn {
                items(state.holdings) { h ->
                    ListItem(
                        headlineContent = { Text(h.coinId) },
                        supportingContent = { Text("Amount: ${h.amount}") },
                        trailingContent = {
                            Row {
                                TextButton(onClick = {
                                    editingCoinId = h.coinId
                                    showEditor = true
                                }) { Text("Edit") }

                                TextButton(onClick = { vm.delete(h.coinId) }) { Text("Delete") }
                            }
                        }
                    )
                    Divider()
                }
            }
        }
    }

    if (showEditor) {
        val initialAmount = state.holdings.firstOrNull { it.coinId == editingCoinId }?.amount

        PortfolioFullScreenEditor(
            initialCoinId = editingCoinId,
            initialAmount = initialAmount,
            onDismiss = { showEditor = false },
            onSave = { coinId, amount ->
                vm.addOrUpdate(coinId, amount)
                showEditor = false
            }
        )
    }
}

@Composable
private fun PortfolioFullScreenEditor(
    initialCoinId: String?,
    initialAmount: Double?,
    onDismiss: () -> Unit,
    onSave: (coinId: String, amount: Double) -> Unit
) {
    val scope = rememberCoroutineScope()

    // U ovom koraku, bez Hilt-a, najjednostavnije je direktno repo.
    val repo = remember { MarketRepository() }

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var allCoins by remember { mutableStateOf<List<CoinDto>>(emptyList()) }

    var search by remember { mutableStateOf("") }
    var selectedCoin by remember { mutableStateOf<CoinDto?>(null) }

    val isEditMode = initialCoinId != null

    var amountText by remember { mutableStateOf(initialAmount?.toString() ?: "") }

    fun loadCoins() {
        scope.launch {
            isLoading = true
            error = null

            runCatching {
                // Smanji broj da manje udara rate-limit
                repo.fetchMarketCoins().take(150)
            }.onSuccess { coins ->
                allCoins = coins
                if (isEditMode) {
                    selectedCoin = coins.firstOrNull { it.id == initialCoinId }
                }
                isLoading = false
            }.onFailure { e ->
                error = e.message ?: "Failed to load coins"
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { loadCoins() }

    val filteredCoins = remember(search, allCoins) {
        if (search.isBlank()) allCoins
        else {
            val q = search.trim().lowercase()
            allCoins.filter {
                it.name.lowercase().contains(q) ||
                        it.symbol.lowercase().contains(q) ||
                        it.id.lowercase().contains(q)
            }
        }
    }

    val parsedAmount = amountText.replace(",", ".").toDoubleOrNull()
    val canSave = selectedCoin != null && parsedAmount != null && parsedAmount > 0.0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // TOP BAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditMode) "Edit holding" else "Add to portfolio",
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextButton(onClick = onDismiss) { Text("Close") }
                }

                // SEARCH
                SearchBar(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(8.dp))

                // CONTENT
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    error != null -> {
                        Column(Modifier.padding(16.dp)) {
                            Text("Error: $error")
                            Spacer(Modifier.height(10.dp))
                            Button(onClick = { loadCoins() }) { Text("Retry") }
                        }
                    }

                    else -> {
                        // LISTA COINOVA
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            items(filteredCoins) { coin ->
                                val isSelected = selectedCoin?.id == coin.id

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            enabled = !isEditMode // u edit mode ne menjaš coin
                                        ) {
                                            selectedCoin = coin
                                        }
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CoinIcon(
                                        url = coin.image,
                                        contentDescription = coin.name,
                                        modifier = Modifier.size(28.dp)
                                    )

                                    Spacer(Modifier.width(10.dp))

                                    Column(Modifier.weight(1f)) {
                                        Text(coin.name, style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            coin.symbol.uppercase(),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("$" + "%,.2f".format(coin.currentPrice))
                                        val p = coin.priceChangePercentage24H ?: 0.0
                                        Text(
                                            (if (p >= 0) "+" else "-") + "%.2f".format(abs(p)) + "%"
                                        )
                                    }

                                    if (isSelected) {
                                        Spacer(Modifier.width(10.dp))
                                        Text("✓")
                                    }
                                }
                                Divider()
                            }
                        }
                    }
                }

                // BOTTOM: amount + save
                Divider()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = when {
                            selectedCoin == null -> "Select a coin"
                            else -> "Selected: ${selectedCoin!!.name} (${selectedCoin!!.symbol.uppercase()})"
                        }
                    )
                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Amount") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val coinId = selectedCoin!!.id
                            val amount = parsedAmount!!
                            onSave(coinId, amount)
                        },
                        enabled = canSave,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}