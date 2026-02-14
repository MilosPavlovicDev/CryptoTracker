package com.milospavlovic4046.cryptotracker.presentation.approotscreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.milospavlovic4046.cryptotracker.presentation.SessionViewModel
import com.milospavlovic4046.cryptotracker.presentation.components.HomeScreen
import com.milospavlovic4046.cryptotracker.presentation.components.HomeViewModel
import com.milospavlovic4046.cryptotracker.presentation.onboarding.OnboardingFlowScreen
import com.milospavlovic4046.cryptotracker.presentation.onboarding.OnboardingViewModel
import com.milospavlovic4046.cryptotracker.presentation.portfolio.PortfolioScreen
import com.milospavlovic4046.cryptotracker.presentation.portfolio.PortfolioViewModel

@Composable
fun AppRootScreen() {
    val sessionVm: SessionViewModel = hiltViewModel()
    val user by sessionVm.user.collectAsStateWithLifecycle()

    // 1) Ako nema user-a -> onboarding
    if (user == null) {
        val onboardingVm: OnboardingViewModel = hiltViewModel()

        OnboardingFlowScreen(
            onFinished = {
                // ništa: SessionViewModel prati Room i čim se upiše user, user != null i UI prelazi u app
            },
            onSaveProfile = { name, age ->
                onboardingVm.saveUser(name, age)
            }
        )
        return
    }

    // 2) User postoji -> normalna app
    val currentUser = user!!

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
                    userName = currentUser.name,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            1 -> {
                val portfolioVm: PortfolioViewModel = hiltViewModel()
                PortfolioScreen(
                    vm = portfolioVm,
                    userName = currentUser.name,
                    onEditName = { newName -> sessionVm.updateName(newName) },
                    onDeleteUser = { sessionVm.deleteUser() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}