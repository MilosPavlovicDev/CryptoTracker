package com.milospavlovic4046.cryptotracker.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.milospavlovic4046.cryptotracker.data.local.dao.PortfolioDao
import com.milospavlovic4046.cryptotracker.data.local.entity.PortfolioHoldingEntity
import com.milospavlovic4046.cryptotracker.data.local.dao.UserDao
import com.milospavlovic4046.cryptotracker.data.local.entity.UserEntity

@Database(
    entities = [PortfolioHoldingEntity::class, UserEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun userDao(): UserDao
}