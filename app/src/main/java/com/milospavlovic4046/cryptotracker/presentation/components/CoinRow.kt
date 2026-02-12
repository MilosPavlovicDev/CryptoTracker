package com.milospavlovic4046.cryptotracker.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.milospavlovic4046.cryptotracker.model.CoinDto
import com.milospavlovic4046.cryptotracker.presentation.components.CoinIcon
import kotlin.math.abs

@Composable
fun CoinRow(coin: CoinDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank
        Text(
            text = (coin.marketCapRank ?: 0).toString(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(28.dp)
        )

        // Icon
        CoinIcon(
            url = coin.image,
            contentDescription = coin.name,
            modifier = Modifier.size(28.dp)
        )

        Spacer(Modifier.width(10.dp))

        // Symbol
        Text(
            text = coin.symbol.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        // Price + %
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$" + "%,.2f".format(coin.currentPrice),
                style = MaterialTheme.typography.titleSmall
            )

            val p = coin.priceChangePercentage24H ?: 0.0
            Text(
                text = (if (p >= 0) "+" else "-") + "%.2f".format(abs(p)) + "%",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}