package com.milospavlovic4046.cryptotracker.presentation.components


import androidx.compose.runtime.Composable
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext

@Composable
fun CoinIcon(
    url: String,
    contentDescription: String?
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription
    )
}