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
import com.milospavlovic4046.cryptotracker.presentation.onboarding.OnboardingProfileScreen
import com.milospavlovic4046.cryptotracker.presentation.onboarding.OnboardingViewModel
import com.milospavlovic4046.cryptotracker.presentation.onboarding.OnboardingWelcomeScreen
import com.milospavlovic4046.cryptotracker.presentation.SessionViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AppRootScreen() {

    val sessionVm: SessionViewModel = hiltViewModel()
    val user by sessionVm.user.collectAsStateWithLifecycle()

    // Onboarding step: 0=welcome, 1=profile
    var onboardingStep by remember { mutableIntStateOf(0) }

    if (user == null) {
        val onboardingVm: OnboardingViewModel = hiltViewModel()

        when (onboardingStep) {
            0 -> OnboardingWelcomeScreen(
                onStart = { onboardingStep = 1 }
            )

            1 -> OnboardingProfileScreen(
                onSave = { name, age ->
                    onboardingVm.saveUser(name, age)
                    // ne moramo ručno da prebacujemo dalje;
                    // SessionViewModel će dobiti user != null i UI će sam preći u app
                }
            )
        }

        return
    }

    val userName = user!!.name

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
                    userName = userName,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            1 -> {
                val portfolioVm: PortfolioViewModel = hiltViewModel()
                PortfolioScreen(
                    vm = portfolioVm,
                    userName = userName,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}