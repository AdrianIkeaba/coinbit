package com.ghostdev.coinbit.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cache_preferences")

class CacheManager(private val context: Context) {
    
    companion object {
        private val LAST_COIN_FETCH_TIME = longPreferencesKey("last_coin_fetch_time")
        // Increased from 5 to 15 minutes to reduce API calls and avoid rate limits
        private const val CACHE_VALIDITY_DURATION = 15 * 60 * 1000L // 15 minutes
    }
    
    suspend fun setLastCoinFetchTime(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_COIN_FETCH_TIME] = timestamp
        }
    }
    
    fun getLastCoinFetchTime(): Flow<Long?> {
        return context.dataStore.data.map { preferences ->
            preferences[LAST_COIN_FETCH_TIME]
        }
    }
    
    suspend fun shouldRefreshCoins(): Boolean {
        val lastFetchTime = getLastCoinFetchTime().firstOrNull() ?: 0L
        return System.currentTimeMillis() - lastFetchTime > CACHE_VALIDITY_DURATION
    }
    
    suspend fun clearCache() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
