package com.ghostdev.coinbit.domain.model

data class ChartData(
    val prices: List<ChartEntry>,
    val marketCaps: List<ChartEntry>,
    val totalVolumes: List<ChartEntry>
)

data class ChartEntry(
    val timestamp: Long,
    val value: Double
)
