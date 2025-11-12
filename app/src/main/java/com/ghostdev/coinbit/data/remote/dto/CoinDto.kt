package com.ghostdev.coinbit.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinDto(
    @SerialName("id")
    val id: String,
    @SerialName("symbol")
    val symbol: String,
    @SerialName("name")
    val name: String,
    @SerialName("image")
    val image: String,
    @SerialName("current_price")
    val currentPrice: Double,
    @SerialName("market_cap")
    val marketCap: Long,
    @SerialName("market_cap_rank")
    val marketCapRank: Int?,
    @SerialName("fully_diluted_valuation")
    val fullyDilutedValuation: Long?,
    @SerialName("total_volume")
    val totalVolume: Double,
    @SerialName("high_24h")
    val high24h: Double?,
    @SerialName("low_24h")
    val low24h: Double?,
    @SerialName("price_change_24h")
    val priceChange24h: Double?,
    @SerialName("price_change_percentage_24h")
    val priceChangePercentage24h: Double?,
    @SerialName("market_cap_change_24h")
    val marketCapChange24h: Double?,
    @SerialName("market_cap_change_percentage_24h")
    val marketCapChangePercentage24h: Double?,
    @SerialName("circulating_supply")
    val circulatingSupply: Double?,
    @SerialName("total_supply")
    val totalSupply: Double?,
    @SerialName("max_supply")
    val maxSupply: Double?,
    @SerialName("ath")
    val ath: Double?,
    @SerialName("ath_change_percentage")
    val athChangePercentage: Double?,
    @SerialName("ath_date")
    val athDate: String?,
    @SerialName("atl")
    val atl: Double?,
    @SerialName("atl_change_percentage")
    val atlChangePercentage: Double?,
    @SerialName("atl_date")
    val atlDate: String?,
    @SerialName("last_updated")
    val lastUpdated: String
)
