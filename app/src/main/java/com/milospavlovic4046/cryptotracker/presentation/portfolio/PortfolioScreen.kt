package com.milospavlovic4046.cryptotracker.presentation.portfolio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.milospavlovic4046.cryptotracker.model.CoinDto
import com.milospavlovic4046.cryptotracker.presentation.components.CoinIcon
import com.milospavlovic4046.cryptotracker.presentation.components.SearchBar
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    vm: PortfolioViewModel,
    userName: String,
    onEditName: (String) -> Unit,
    onDeleteUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by vm.state.collectAsStateWithLifecycle()

    var showEditor by remember { mutableStateOf(false) }
    var editingCoinId by remember { mutableStateOf<String?>(null) }

    // dialogs
    var showEditName by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var nameText by remember(userName) { mutableStateOf(userName) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // ✅ CLEAN HEADER (TopAppBar) + icon actions (staje na svakom ekranu)
        TopAppBar(
            title = {
                Text(
                    text = "${userName}'s portfolio",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            actions = {
                // Edit name
                IconButton(onClick = { showEditName = true }) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit name")
                }

                // Delete user
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete user")
                }

                // Clear portfolio
                IconButton(onClick = { vm.clearAll() }) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Clear portfolio")
                }

                // Add (krug +)
                IconButton(onClick = {
                    editingCoinId = null
                    showEditor = true
                }) {
                    Icon(Icons.Outlined.AddCircle, contentDescription = "Add coin")
                }
            }
        )

        Spacer(Modifier.height(8.dp))

        if (state.items.isEmpty()) {
            Text(
                text = "No coins in portfolio yet.",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Text(
                text = "Total: $" + "%,.2f".format(state.totalValueUsd),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn {
                items(state.items) { item ->
                    ListItem(
                        leadingContent = {
                            if (item.imageUrl.isNotBlank()) {
                                CoinIcon(
                                    url = item.imageUrl,
                                    contentDescription = item.name,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        },
                        headlineContent = { Text("${item.name} (${item.symbol})") },
                        supportingContent = {
                            val pct = item.change24h ?: 0.0
                            Text(
                                "Amount: ${item.amount}  •  Price: $" + "%,.2f".format(item.currentPrice) +
                                        "  •  24h: " + (if (pct >= 0) "+" else "") + "%.2f".format(pct) + "%"
                            )
                        },
                        trailingContent = {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("$" + "%,.2f".format(item.valueUsd))
                                Row {
                                    TextButton(onClick = {
                                        editingCoinId = item.coinId
                                        showEditor = true
                                    }) { Text("Edit") }

                                    TextButton(onClick = { vm.delete(item.coinId) }) { Text("Delete") }
                                }
                            }
                        }
                    )
                    Divider()
                }
            }
        }
    }

    // ---- Edit name dialog ----
    if (showEditName) {
        AlertDialog(
            onDismissRequest = { showEditName = false },
            title = { Text("Edit name") },
            text = {
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    singleLine = true,
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trimmed = nameText.trim()
                        if (trimmed.isNotBlank()) onEditName(trimmed)
                        showEditName = false
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditName = false }) { Text("Cancel") }
            }
        )
    }

    // ---- Delete user confirm ----
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete user?") },
            text = { Text("This will remove your profile and show onboarding again.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteUser()
                    }
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    // ---- Coin editor ----
    if (showEditor) {
        val initialAmount = editingCoinId?.let { id ->
            state.items.firstOrNull { it.coinId == id }?.amount
        }

        PortfolioFullScreenEditor(
            vm = vm,
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

// --- existing editor (ostaje isti kao kod tebe) ---
@Composable
private fun PortfolioFullScreenEditor(
    vm: PortfolioViewModel,
    initialCoinId: String?,
    initialAmount: Double?,
    onDismiss: () -> Unit,
    onSave: (coinId: String, amount: Double) -> Unit
) {
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var allCoins by remember { mutableStateOf<List<CoinDto>>(emptyList()) }

    var search by remember { mutableStateOf("") }
    var selectedCoin by remember { mutableStateOf<CoinDto?>(null) }

    val isEditMode = initialCoinId != null
    var lockedCoin by remember { mutableStateOf<CoinDto?>(null) }
    var amountText by remember { mutableStateOf(initialAmount?.toString() ?: "") }

    fun loadCoins() {
        scope.launch {
            isLoading = true
            error = null

            runCatching {
                vm.loadCoinsForPicker(limit = 150)
            }.onSuccess { coins ->
                allCoins = coins
                if (isEditMode) {
                    val found = coins.firstOrNull { it.id == initialCoinId }
                    selectedCoin = found
                    lockedCoin = found
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

                if (!isEditMode) {
                    SearchBar(
                        value = search,
                        onValueChange = { search = it },
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }

                        error != null -> {
                            Column(Modifier.padding(16.dp)) {
                                Text("Error: $error")
                                Spacer(Modifier.height(10.dp))
                                Button(onClick = { loadCoins() }) { Text("Retry") }
                            }
                        }

                        else -> {
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
                                            .clickable { selectedCoin = coin }
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
                                            Text(coin.symbol.uppercase(), style = MaterialTheme.typography.labelMedium)
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("$" + "%,.2f".format(coin.currentPrice))
                                            val p = coin.priceChangePercentage24H ?: 0.0
                                            Text((if (p >= 0) "+" else "-") + "%.2f".format(abs(p)) + "%")
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
                } else {
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }

                        error != null -> {
                            Column(Modifier.padding(16.dp)) {
                                Text("Error: $error")
                                Spacer(Modifier.height(10.dp))
                                Button(onClick = { loadCoins() }) { Text("Retry") }
                            }
                        }

                        else -> {
                            val coin = lockedCoin
                            if (coin == null) {
                                Column(Modifier.padding(16.dp)) {
                                    Text("Coin not found.")
                                    Spacer(Modifier.height(10.dp))
                                    TextButton(onClick = onDismiss) { Text("Close") }
                                }
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CoinIcon(
                                        url = coin.image,
                                        contentDescription = coin.name,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(coin.name, style = MaterialTheme.typography.titleLarge)
                                        Text(coin.symbol.uppercase(), style = MaterialTheme.typography.labelMedium)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("$" + "%,.2f".format(coin.currentPrice))
                                        val p = coin.priceChangePercentage24H ?: 0.0
                                        Text((if (p >= 0) "+" else "-") + "%.2f".format(abs(p)) + "%")
                                    }
                                }

                                Divider()
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }

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
                    ) { Text("Save") }
                }
            }
        }
    }
}