package com.milospavlovic4046.cryptotracker.di

import android.content.Context
import androidx.room.Room
import com.milospavlovic4046.cryptotracker.data.local.dao.PortfolioDao
import com.milospavlovic4046.cryptotracker.data.local.db.AppDatabase
import com.milospavlovic4046.cryptotracker.data.remote.CoinGeckoApi
import com.milospavlovic4046.cryptotracker.repository.MarketRepository
import com.milospavlovic4046.cryptotracker.repository.PortfolioRepository
import com.milospavlovic4046.cryptotracker.data.local.dao.UserDao
import com.milospavlovic4046.cryptotracker.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ---- ROOM ----
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "crypto_tracker_hilt.db"
        ).build()
    }

    @Provides
    fun providePortfolioDao(db: AppDatabase): PortfolioDao = db.portfolioDao()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun providePortfolioRepository(dao: PortfolioDao): PortfolioRepository {
        return PortfolioRepository(dao)
    }

    @Provides
    @Singleton
    fun provideUserRepository(dao: UserDao): UserRepository {
        return UserRepository(dao)
    }

    // ---- API + REPO ----
    @Provides
    @Singleton
    fun provideCoinGeckoApi(): CoinGeckoApi = CoinGeckoApi()

    @Provides
    @Singleton
    fun provideMarketRepository(api: CoinGeckoApi): MarketRepository {
        return MarketRepository(api)
    }
}