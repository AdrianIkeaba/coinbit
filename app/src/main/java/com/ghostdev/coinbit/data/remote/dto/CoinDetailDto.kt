package com.ghostdev.coinbit.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinDetailDto(
    @SerialName("id")
    val id: String,
    @SerialName("symbol")
    val symbol: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: DescriptionDto,
    @SerialName("image")
    val image: ImageDto,
    @SerialName("market_cap_rank")
    val marketCapRank: Int?,
    @SerialName("market_data")
    val marketData: MarketDataDto,
    @SerialName("links")
    val links: LinksDto,
    @SerialName("last_updated")
    val lastUpdated: String
)

@Serializable
data class DescriptionDto(
    @SerialName("en")
    val en: String
)

@Serializable
data class ImageDto(
    @SerialName("thumb")
    val thumb: String,
    @SerialName("small")
    val small: String,
    @SerialName("large")
    val large: String
)

@Serializable
data class MarketDataDto(
    @SerialName("current_price")
    val currentPrice: Map<String, Double>,
    @SerialName("market_cap")
    val marketCap: Map<String, Long>,
    @SerialName("total_volume")
    val totalVolume: Map<String, Long>,
    @SerialName("high_24h")
    val high24h: Map<String, Double>,
    @SerialName("low_24h")
    val low24h: Map<String, Double>,
    @SerialName("price_change_24h")
    val priceChange24h: Double?,
    @SerialName("price_change_percentage_24h")
    val priceChangePercentage24h: Double?,
    @SerialName("price_change_percentage_7d")
    val priceChangePercentage7d: Double?,
    @SerialName("price_change_percentage_14d")
    val priceChangePercentage14d: Double?,
    @SerialName("price_change_percentage_30d")
    val priceChangePercentage30d: Double?,
    @SerialName("price_change_percentage_60d")
    val priceChangePercentage60d: Double?,
    @SerialName("price_change_percentage_200d")
    val priceChangePercentage200d: Double?,
    @SerialName("price_change_percentage_1y")
    val priceChangePercentage1y: Double?,
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
    val ath: Map<String, Double>,
    @SerialName("ath_change_percentage")
    val athChangePercentage: Map<String, Double>,
    @SerialName("ath_date")
    val athDate: Map<String, String>,
    @SerialName("atl")
    val atl: Map<String, Double>,
    @SerialName("atl_change_percentage")
    val atlChangePercentage: Map<String, Double>,
    @SerialName("atl_date")
    val atlDate: Map<String, String>
)

@Serializable
data class LinksDto(
    @SerialName("homepage")
    val homepage: List<String>,
    @SerialName("blockchain_site")
    val blockchainSite: List<String>,
    @SerialName("official_forum_url")
    val officialForumUrl: List<String>,
    @SerialName("subreddit_url")
    val subredditUrl: String?
)
