package com.ghostdev.coinbit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "market_charts")
data class MarketChartEntity(
    @PrimaryKey
    val cacheKey: String, // Format: "coinId_days" (e.g., "bitcoin_7")
    val coinId: String,
    val days: Int,
    val prices: String, // JSON encoded list of [timestamp, price]
    val marketCaps: String, // JSON encoded list of [timestamp, marketCap]
    val totalVolumes: String, // JSON encoded list of [timestamp, volume]
    val timestamp: Long = System.currentTimeMillis()
)
