package com.milospavlovic4046.cryptotracker.presentation.approotscreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.milospavlovic4046.cryptotracker.presentation.components.HomeScreen
import com.milospavlovic4046.cryptotracker.presentation.components.HomeViewModel
import com.milospavlovic4046.cryptotracker.presentation.portfolio.PortfolioScreen
import com.milospavlovic4046.cryptotracker.presentation.portfolio.PortfolioViewModel

@Composable
fun AppRootScreen() {

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Market") },
                    label = { Text("Market") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Portfolio") },
                    label = { Text("Portfolio") }
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> {
                val homeVm: HomeViewModel = hiltViewModel()
                HomeScreen(
                    vm = homeVm,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            1 -> {
                val portfolioVm: PortfolioViewModel = hiltViewModel()
                PortfolioScreen(
                    vm = portfolioVm,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}