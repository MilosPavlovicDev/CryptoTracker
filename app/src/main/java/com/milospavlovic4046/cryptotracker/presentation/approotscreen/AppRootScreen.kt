package com.milospavlovic4046.cryptotracker.presentation.approotscreen

import android.content.Context
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.milospavlovic4046.cryptotracker.data.local.db.DatabaseProvider
import com.milospavlovic4046.cryptotracker.repository.PortfolioRepository
import com.milospavlovic4046.cryptotracker.presentation.components.HomeScreen
import com.milospavlovic4046.cryptotracker.presentation.components.HomeViewModel
import com.milospavlovic4046.cryptotracker.presentation.portfolio.PortfolioScreen
import com.milospavlovic4046.cryptotracker.presentation.portfolio.PortfolioViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier

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
                val homeVm: HomeViewModel = viewModel()
                HomeScreen(
                    vm = homeVm,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            1 -> {
                val ctx = LocalContext.current
                val portfolioVm: PortfolioViewModel = viewModel(
                    factory = portfolioVmFactory(ctx)
                )
                PortfolioScreen(
                    vm = portfolioVm,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

private fun portfolioVmFactory(context: Context): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = DatabaseProvider.get(context)
            val repo = PortfolioRepository(db.portfolioDao())
            @Suppress("UNCHECKED_CAST")
            return PortfolioViewModel(repo) as T
        }
    }
}