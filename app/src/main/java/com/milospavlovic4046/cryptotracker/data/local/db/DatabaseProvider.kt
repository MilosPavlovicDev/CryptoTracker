package com.milospavlovic4046.cryptotracker.data.local.db


import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile private var db: AppDatabase? = null

    fun get(context: Context): AppDatabase {
        return db ?: synchronized(this) {
            db ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "crypto_tracker.db"
            ).build().also { db = it }
        }
    }
}