package com.ghostdev.coinbit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ghostdev.coinbit.data.local.dao.CoinDao
import com.ghostdev.coinbit.data.local.dao.CoinDetailDao
import com.ghostdev.coinbit.data.local.dao.MarketChartDao
import com.ghostdev.coinbit.data.local.entities.CoinEntity
import com.ghostdev.coinbit.data.local.entities.CoinDetailEntity
import com.ghostdev.coinbit.data.local.entities.MarketChartEntity

@Database(
    entities = [
        CoinEntity::class,
        CoinDetailEntity::class,
        MarketChartEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class CoinDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao
    abstract fun coinDetailDao(): CoinDetailDao
    abstract fun marketChartDao(): MarketChartDao
    
    companion object {
        const val DATABASE_NAME = "coin_database"
    }
}
