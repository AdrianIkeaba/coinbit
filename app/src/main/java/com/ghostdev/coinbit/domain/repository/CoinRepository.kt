package com.ghostdev.coinbit.domain.repository

import com.ghostdev.coinbit.domain.model.ChartData
import com.ghostdev.coinbit.domain.model.Coin
import com.ghostdev.coinbit.domain.model.CoinDetail
import com.ghostdev.coinbit.util.Resource
import kotlinx.coroutines.flow.Flow

interface CoinRepository {
    fun getCoins(forceRefresh: Boolean = false): Flow<Resource<List<Coin>>>
    fun getCoinById(coinId: String): Flow<Resource<CoinDetail>>
    fun getMarketChart(coinId: String, days: Int = 7): Flow<Resource<ChartData>>
    fun searchCoins(query: String): Flow<List<Coin>>
    fun getFavoriteCoins(): Flow<List<Coin>>
    suspend fun toggleFavorite(coinId: String)
}
