package com.ghostdev.coinbit.data.repository

import android.util.Log
import com.ghostdev.coinbit.data.local.CoinDatabase
import com.ghostdev.coinbit.data.local.datastore.CacheManager
import com.ghostdev.coinbit.data.remote.api.CoinGeckoApi
import com.ghostdev.coinbit.domain.model.ChartData
import com.ghostdev.coinbit.domain.model.Coin
import com.ghostdev.coinbit.domain.model.CoinDetail
import com.ghostdev.coinbit.domain.repository.CoinRepository
import com.ghostdev.coinbit.util.NetworkConnectivityObserver
import com.ghostdev.coinbit.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CoinRepositoryImpl(
    private val api: CoinGeckoApi,
    private val database: CoinDatabase,
    private val cacheManager: CacheManager,
    private val networkObserver: NetworkConnectivityObserver
) : CoinRepository {
    
    private val coinDao = database.coinDao()
    
    override fun getCoins(forceRefresh: Boolean): Flow<Resource<List<Coin>>> = flow {
        Log.d("CoinRepository", "getCoins called with forceRefresh=$forceRefresh")
        emit(Resource.Loading())
        
        // Check if we have cached data
        val cachedCoins = coinDao.getAllCoins().firstOrNull()
        Log.d("CoinRepository", "Cached coins count: ${cachedCoins?.size ?: 0}")
        
        // Emit cached data first if available
        if (!cachedCoins.isNullOrEmpty() && !forceRefresh) {
            Log.d("CoinRepository", "Emitting cached data")
            emit(Resource.Success(cachedCoins.map { it.toDomain() }))
        }
        
        // Check if we should refresh
        val shouldRefresh = forceRefresh || cacheManager.shouldRefreshCoins()
        Log.d("CoinRepository", "Should refresh: $shouldRefresh")
        
        if (shouldRefresh) {
            val isConnected = networkObserver.isConnected()
            Log.d("CoinRepository", "Network connected: $isConnected")
            
            if (!isConnected) {
                if (cachedCoins.isNullOrEmpty()) {
                    Log.e("CoinRepository", "No connection and no cache")
                    emit(Resource.Error("No internet connection and no cached data available"))
                } else {
                    Log.d("CoinRepository", "No connection, using cache")
                    emit(Resource.Success(cachedCoins.map { it.toDomain() }))
                }
                return@flow
            }
            
            try {
                Log.d("CoinRepository", "Fetching coins from API...")
                val remoteCoins = api.getCoins()
                Log.d("CoinRepository", "API returned ${remoteCoins.size} coins")
                
                // Get favorite status from cache
                val favoriteIds = cachedCoins?.filter { it.isFavorite }?.map { it.id } ?: emptyList()
                
                // Convert to entities and preserve favorite status
                val entities = remoteCoins.map { dto ->
                    dto.toEntity(isFavorite = favoriteIds.contains(dto.id))
                }
                
                // Save to database
                Log.d("CoinRepository", "Saving ${entities.size} coins to database")
                coinDao.insertCoins(entities)
                
                // Update last fetch time
                cacheManager.setLastCoinFetchTime(System.currentTimeMillis())
                
                // Emit fresh data
                Log.d("CoinRepository", "Emitting fresh data")
                emit(Resource.Success(entities.map { it.toDomain() }))
            } catch (e: Exception) {
                Log.e("CoinRepository", "Error fetching coins: ${e.message}", e)
                if (cachedCoins.isNullOrEmpty()) {
                    emit(Resource.Error(e.message ?: "An unexpected error occurred"))
                } else {
                    Log.d("CoinRepository", "Error occurred, falling back to cache")
                    emit(Resource.Success(cachedCoins.map { it.toDomain() }))
                }
            }
        }
    }
    
    override fun getCoinById(coinId: String): Flow<Resource<CoinDetail>> = flow {
        emit(Resource.Loading())
        
        try {
            val coinDetailDao = database.coinDetailDao()
            
            // Check if coin is in favorites
            val cachedCoin = coinDao.getCoinById(coinId)
            val isFavorite = cachedCoin?.isFavorite ?: false
            
            // Check cache first (10-minute validity for coin details)
            val cachedDetail = coinDetailDao.getCoinDetailById(coinId)
            if (cachedDetail != null) {
                val cacheAge = System.currentTimeMillis() - cachedDetail.timestamp
                val cacheValidityDuration = 10 * 60 * 1000L // 10 minutes
                
                if (cacheAge < cacheValidityDuration) {
                    // Cache is still valid, return cached data
                    val domainFromCache = cachedDetail.toDomain().copy(isFavorite = isFavorite)
                    emit(Resource.Success(domainFromCache))
                    return@flow
                }
            }
            
            if (!networkObserver.isConnected()) {
                // No connection, return cached data if available or error
                if (cachedDetail != null) {
                    val domainFromCache = cachedDetail.toDomain().copy(isFavorite = isFavorite)
                    emit(Resource.Success(domainFromCache))
                } else {
                    emit(Resource.Error("No internet connection"))
                }
                return@flow
            }
            
            // Fetch from API
            val coinDetail = api.getCoinById(coinId)
            
            // Save to cache
            val entityToCache = coinDetail.toEntity(isFavorite)
            coinDetailDao.insertCoinDetail(entityToCache)
            
            // Return fresh data
            val domainModel = coinDetail.toDomain(isFavorite)
            emit(Resource.Success(domainModel))
        } catch (e: Exception) {
            // On error, try to return cached data
            val coinDetailDao = database.coinDetailDao()
            val cachedDetail = coinDetailDao.getCoinDetailById(coinId)
            if (cachedDetail != null) {
                val currentFavorite = coinDao.getCoinById(coinId)?.isFavorite ?: false
                emit(Resource.Success(cachedDetail.toDomain().copy(isFavorite = currentFavorite)))
            } else {
                emit(Resource.Error(e.message ?: "An unexpected error occurred"))
            }
        }
    }
    
    override fun getMarketChart(coinId: String, days: Int): Flow<Resource<ChartData>> = flow {
        emit(Resource.Loading())
        
        try {
            val marketChartDao = database.marketChartDao()
            val cacheKey = "${coinId}_$days"
            
            // Check cache first (30-minute validity for chart data)
            val cachedChart = marketChartDao.getMarketChartByKey(cacheKey)
            if (cachedChart != null) {
                val cacheAge = System.currentTimeMillis() - cachedChart.timestamp
                val cacheValidityDuration = 30 * 60 * 1000L // 30 minutes
                
                if (cacheAge < cacheValidityDuration) {
                    // Cache is still valid, return cached data
                    emit(Resource.Success(cachedChart.toChartData()))
                    return@flow
                }
            }
            
            if (!networkObserver.isConnected()) {
                // No connection, return cached data if available or error
                if (cachedChart != null) {
                    emit(Resource.Success(cachedChart.toChartData()))
                } else {
                    emit(Resource.Error("No internet connection"))
                }
                return@flow
            }
            
            // Fetch from API
            val chartData = api.getMarketChart(coinId, days = days)
            
            // Save to cache
            val entityToCache = chartData.toEntity(coinId, days)
            marketChartDao.insertMarketChart(entityToCache)
            
            // Return fresh data
            emit(Resource.Success(chartData.toChartData()))
        } catch (e: Exception) {
            // On error, try to return cached data
            val marketChartDao = database.marketChartDao()
            val cacheKey = "${coinId}_$days"
            val cachedChart = marketChartDao.getMarketChartByKey(cacheKey)
            if (cachedChart != null) {
                emit(Resource.Success(cachedChart.toChartData()))
            } else {
                emit(Resource.Error(e.message ?: "An unexpected error occurred"))
            }
        }
    }
    
    override fun searchCoins(query: String): Flow<List<Coin>> {
        return coinDao.searchCoins(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getFavoriteCoins(): Flow<List<Coin>> {
        return coinDao.getFavoriteCoins().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun toggleFavorite(coinId: String) {
        val coin = coinDao.getCoinById(coinId)
        coin?.let {
            coinDao.updateFavoriteStatus(coinId, !it.isFavorite)
        }
    }
}
