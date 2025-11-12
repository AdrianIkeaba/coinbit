package com.ghostdev.coinbit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ghostdev.coinbit.data.local.entities.CoinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoins(coins: List<CoinEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoin(coin: CoinEntity)
    
    @Query("SELECT * FROM coins ORDER BY marketCapRank ASC")
    fun getAllCoins(): Flow<List<CoinEntity>>
    
    @Query("SELECT * FROM coins WHERE id = :coinId")
    suspend fun getCoinById(coinId: String): CoinEntity?
    
    @Query("SELECT * FROM coins WHERE id = :coinId")
    fun getCoinByIdFlow(coinId: String): Flow<CoinEntity?>
    
    @Query("SELECT * FROM coins WHERE isFavorite = 1 ORDER BY marketCapRank ASC")
    fun getFavoriteCoins(): Flow<List<CoinEntity>>
    
    @Query("""
        SELECT * FROM coins 
        WHERE name LIKE '%' || :query || '%' 
        OR symbol LIKE '%' || :query || '%'
        ORDER BY marketCapRank ASC
    """)
    fun searchCoins(query: String): Flow<List<CoinEntity>>
    
    @Query("UPDATE coins SET isFavorite = :isFavorite WHERE id = :coinId")
    suspend fun updateFavoriteStatus(coinId: String, isFavorite: Boolean)
    
    @Query("DELETE FROM coins WHERE isFavorite = 0 AND timestamp < :timestamp")
    suspend fun deleteOldCoins(timestamp: Long)
    
    @Query("DELETE FROM coins")
    suspend fun deleteAllCoins()
    
    @Query("SELECT COUNT(*) FROM coins")
    suspend fun getCoinCount(): Int
}
