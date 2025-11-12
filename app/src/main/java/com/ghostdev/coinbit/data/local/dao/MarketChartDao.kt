package com.ghostdev.coinbit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ghostdev.coinbit.data.local.entities.MarketChartEntity

@Dao
interface MarketChartDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketChart(marketChart: MarketChartEntity)
    
    @Query("SELECT * FROM market_charts WHERE cacheKey = :cacheKey")
    suspend fun getMarketChartByKey(cacheKey: String): MarketChartEntity?
    
    @Query("DELETE FROM market_charts WHERE timestamp < :timestamp")
    suspend fun deleteOldMarketCharts(timestamp: Long)
    
    @Query("DELETE FROM market_charts")
    suspend fun deleteAllMarketCharts()
}
