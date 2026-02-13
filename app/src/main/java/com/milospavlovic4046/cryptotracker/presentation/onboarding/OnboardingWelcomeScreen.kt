package com.milospavlovic4046.cryptotracker.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingWelcomeScreen(
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Umesto slike: tekst "Crypto Tracker"
        Text(
            text = "Crypto Tracker",
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Welcome to Crypto Tracker, we are happy you are joining our community.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start")
        }
    }
}