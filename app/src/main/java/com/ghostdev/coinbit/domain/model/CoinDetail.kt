package com.ghostdev.coinbit.domain.model

data class CoinDetail(
    val id: String,
    val symbol: String,
    val name: String,
    val description: String,
    val image: String,
    val marketCapRank: Int?,
    val currentPrice: Double,
    val marketCap: Long,
    val totalVolume: Long,
    val high24h: Double,
    val low24h: Double,
    val priceChange24h: Double?,
    val priceChangePercentage24h: Double?,
    val priceChangePercentage7d: Double?,
    val priceChangePercentage14d: Double?,
    val priceChangePercentage30d: Double?,
    val priceChangePercentage60d: Double?,
    val priceChangePercentage200d: Double?,
    val priceChangePercentage1y: Double?,
    val marketCapChange24h: Double?,
    val marketCapChangePercentage24h: Double?,
    val circulatingSupply: Double?,
    val totalSupply: Double?,
    val maxSupply: Double?,
    val ath: Double,
    val athChangePercentage: Double,
    val athDate: String,
    val atl: Double,
    val atlChangePercentage: Double,
    val atlDate: String,
    val links: CoinLinks,
    val lastUpdated: String,
    val isFavorite: Boolean = false
)

data class CoinLinks(
    val homepage: List<String>,
    val blockchainSite: List<String>,
    val officialForumUrl: List<String>,
    val subredditUrl: String?
)
