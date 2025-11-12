package com.ghostdev.coinbit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ghostdev.coinbit.data.local.entities.CoinDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDetailDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinDetail(coinDetail: CoinDetailEntity)
    
    @Query("SELECT * FROM coin_details WHERE id = :coinId")
    suspend fun getCoinDetailById(coinId: String): CoinDetailEntity?
    
    @Query("SELECT * FROM coin_details WHERE id = :coinId")
    fun getCoinDetailByIdFlow(coinId: String): Flow<CoinDetailEntity?>
    
    @Query("DELETE FROM coin_details WHERE timestamp < :timestamp")
    suspend fun deleteOldCoinDetails(timestamp: Long)
    
    @Query("DELETE FROM coin_details")
    suspend fun deleteAllCoinDetails()
}
