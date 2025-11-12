package com.ghostdev.coinbit.data.remote.api

import android.util.Log
import com.ghostdev.coinbit.BuildConfig
import com.ghostdev.coinbit.data.remote.dto.CoinDetailDto
import com.ghostdev.coinbit.data.remote.dto.CoinDto
import com.ghostdev.coinbit.data.remote.dto.MarketChartDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

interface CoinGeckoApi {
    suspend fun getCoins(
        vsCurrency: String = "usd",
        order: String = "market_cap_desc",
        perPage: Int = 100,
        page: Int = 1,
        sparkline: Boolean = false,
        priceChangePercentage: String = "24h"
    ): List<CoinDto>
    
    suspend fun getCoinById(
        coinId: String,
        localization: Boolean = false,
        tickers: Boolean = false,
        marketData: Boolean = true,
        communityData: Boolean = false,
        developerData: Boolean = false,
        sparkline: Boolean = false
    ): CoinDetailDto
    
    suspend fun getMarketChart(
        coinId: String,
        vsCurrency: String = "usd",
        days: Int = 7,
        interval: String? = null
    ): MarketChartDto
}

class CoinGeckoApiImpl(
    private val client: HttpClient
) : CoinGeckoApi {
    
    companion object {
        private const val BASE_URL = "https://api.coingecko.com/api/v3"
        private const val API_KEY_HEADER = "x-cg-pro-api-key"
        private const val RATE_LIMIT_WARNING_THRESHOLD = 25 // Warn at 25 calls (limit is 30/min)
        
        // Rate limit tracking
        @Volatile
        private var apiCallCount = 0
        @Volatile
        private var lastResetTime = System.currentTimeMillis()
    }
    
    private fun trackApiCall() {
        val now = System.currentTimeMillis()
        
        // Reset counter every minute
        if (now - lastResetTime > 60_000) {
            if (apiCallCount > 0) {
                Log.d("API_RATE_LIMIT", "API calls in last minute: $apiCallCount")
            }
            apiCallCount = 0
            lastResetTime = now
        }
        
        apiCallCount++
        
        // Warn if approaching rate limit
        if (apiCallCount >= RATE_LIMIT_WARNING_THRESHOLD) {
            Log.w("API_RATE_LIMIT", "⚠️ WARNING: Approaching rate limit! Calls this minute: $apiCallCount/30")
        }
    }
    
    override suspend fun getCoins(
        vsCurrency: String,
        order: String,
        perPage: Int,
        page: Int,
        sparkline: Boolean,
        priceChangePercentage: String
    ): List<CoinDto> {
        trackApiCall() // Track API usage
        return try {
            Log.d("CoinGeckoApi", "Fetching coins from: $BASE_URL/coins/markets")
            Log.d("CoinGeckoApi", "API Key header: $API_KEY_HEADER")
            Log.d("CoinGeckoApi", "Parameters: vs_currency=$vsCurrency, order=$order, per_page=$perPage")
            
            val response = client.get("$BASE_URL/coins/markets") {
                header(API_KEY_HEADER, BuildConfig.COIN_GECKO_API_KEY)
                parameter("vs_currency", vsCurrency)
                parameter("order", order)
                parameter("per_page", perPage)
                parameter("page", page)
                parameter("sparkline", sparkline)
                parameter("price_change_percentage", priceChangePercentage)
            }
            
            val coins: List<CoinDto> = response.body()
            Log.d("CoinGeckoApi", "Successfully fetched ${coins.size} coins")
            coins
        } catch (e: Exception) {
            Log.e("CoinGeckoApi", "Error fetching coins: ${e.message}", e)
            throw e
        }
    }
    
    override suspend fun getCoinById(
        coinId: String,
        localization: Boolean,
        tickers: Boolean,
        marketData: Boolean,
        communityData: Boolean,
        developerData: Boolean,
        sparkline: Boolean
    ): CoinDetailDto {
        trackApiCall() // Track API usage
        return client.get("$BASE_URL/coins/$coinId") {
            header(API_KEY_HEADER, BuildConfig.COIN_GECKO_API_KEY)
            parameter("localization", localization)
            parameter("tickers", tickers)
            parameter("market_data", marketData)
            parameter("community_data", communityData)
            parameter("developer_data", developerData)
            parameter("sparkline", sparkline)
        }.body()
    }
    
    override suspend fun getMarketChart(
        coinId: String,
        vsCurrency: String,
        days: Int,
        interval: String?
    ): MarketChartDto {
        trackApiCall() // Track API usage
        return client.get("$BASE_URL/coins/$coinId/market_chart") {
            header(API_KEY_HEADER, BuildConfig.COIN_GECKO_API_KEY)
            parameter("vs_currency", vsCurrency)
            parameter("days", days)
            interval?.let { parameter("interval", it) }
        }.body()
    }
}
