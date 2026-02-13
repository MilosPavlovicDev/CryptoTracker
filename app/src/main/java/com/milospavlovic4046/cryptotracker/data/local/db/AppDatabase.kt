package com.milospavlovic4046.cryptotracker.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.milospavlovic4046.cryptotracker.data.local.dao.PortfolioDao
import com.milospavlovic4046.cryptotracker.data.local.entity.PortfolioHoldingEntity

@Database(
    entities = [PortfolioHoldingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
}