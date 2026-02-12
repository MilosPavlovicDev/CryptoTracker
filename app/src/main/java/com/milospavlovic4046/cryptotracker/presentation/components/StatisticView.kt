package com.milospavlovic4046.cryptotracker.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.milospavlovic4046.cryptotracker.presentation.components.Statistic

@Composable
fun StatisticView(stat: Statistic, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(stat.title, style = MaterialTheme.typography.labelSmall)
        Text(stat.value, style = MaterialTheme.typography.titleMedium)

        if (stat.percentageChange != null) {
            Text(
                text = "${if (stat.percentageChange >= 0) "▲" else "▼"} ${"%.2f".format(kotlin.math.abs(stat.percentageChange))}%",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}