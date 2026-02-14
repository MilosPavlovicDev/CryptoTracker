package com.milospavlovic4046.cryptotracker.di

import com.milospavlovic4046.cryptotracker.data.remote.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
        }
    }

    // Emulator -> host machine
    @Provides
    @Singleton
    @Named("jsonServerBaseUrl")
    fun provideJsonServerBaseUrl(): String = "http://10.0.2.2:3000"

    @Provides
    @Singleton
    fun provideUserApi(
        client: HttpClient,
        @Named("jsonServerBaseUrl") baseUrl: String
    ): UserApi = UserApi(client, baseUrl)
}