package com.milospavlovic4046.cryptotracker.presentation.onboarding

import androidx.compose.runtime.*

@Composable
fun OnboardingFlowScreen(
    onFinished: () -> Unit,
    onSaveProfile: (name: String, age: Int) -> Unit
) {
    var step by remember { mutableIntStateOf(0) }

    when (step) {
        0 -> OnboardingStartScreen(
            onStart = { step = 1 }
        )

        1 -> OnboardingProfileScreen(
            onSave = { name, age ->
                onSaveProfile(name, age)
                onFinished()
            }
        )
    }
}