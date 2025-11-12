package com.ghostdev.coinbit.data.repository

import com.ghostdev.coinbit.data.local.entities.CoinDetailEntity
import com.ghostdev.coinbit.data.local.entities.CoinEntity
import com.ghostdev.coinbit.data.local.entities.MarketChartEntity
import com.ghostdev.coinbit.data.remote.dto.CoinDetailDto
import com.ghostdev.coinbit.data.remote.dto.CoinDto
import com.ghostdev.coinbit.data.remote.dto.MarketChartDto
import com.ghostdev.coinbit.domain.model.ChartData
import com.ghostdev.coinbit.domain.model.ChartEntry
import com.ghostdev.coinbit.domain.model.Coin
import com.ghostdev.coinbit.domain.model.CoinDetail
import com.ghostdev.coinbit.domain.model.CoinLinks
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// DTO to Entity
fun CoinDto.toEntity(isFavorite: Boolean = false): CoinEntity {
    return CoinEntity(
        id = id,
        symbol = symbol,
        name = name,
        image = image,
        currentPrice = currentPrice,
        marketCap = marketCap,
        marketCapRank = marketCapRank,
        fullyDilutedValuation = fullyDilutedValuation,
        totalVolume = totalVolume,
        high24h = high24h,
        low24h = low24h,
        priceChange24h = priceChange24h,
        priceChangePercentage24h = priceChangePercentage24h,
        marketCapChange24h = marketCapChange24h,
        marketCapChangePercentage24h = marketCapChangePercentage24h,
        circulatingSupply = circulatingSupply,
        totalSupply = totalSupply,
        maxSupply = maxSupply,
        ath = ath,
        athChangePercentage = athChangePercentage,
        athDate = athDate,
        atl = atl,
        atlChangePercentage = atlChangePercentage,
        atlDate = atlDate,
        lastUpdated = lastUpdated,
        isFavorite = isFavorite,
        timestamp = System.currentTimeMillis()
    )
}

// Entity to Domain Model
fun CoinEntity.toDomain(): Coin {
    return Coin(
        id = id,
        symbol = symbol,
        name = name,
        image = image,
        currentPrice = currentPrice,
        marketCap = marketCap,
        marketCapRank = marketCapRank,
        totalVolume = totalVolume,
        high24h = high24h,
        low24h = low24h,
        priceChange24h = priceChange24h,
        priceChangePercentage24h = priceChangePercentage24h,
        circulatingSupply = circulatingSupply,
        totalSupply = totalSupply,
        maxSupply = maxSupply,
        ath = ath,
        athChangePercentage = athChangePercentage,
        atl = atl,
        atlChangePercentage = atlChangePercentage,
        lastUpdated = lastUpdated,
        isFavorite = isFavorite
    )
}

// DTO to Domain Model (direct mapping without caching)
fun CoinDto.toDomain(isFavorite: Boolean = false): Coin {
    return Coin(
        id = id,
        symbol = symbol,
        name = name,
        image = image,
        currentPrice = currentPrice,
        marketCap = marketCap,
        marketCapRank = marketCapRank,
        totalVolume = totalVolume,
        high24h = high24h,
        low24h = low24h,
        priceChange24h = priceChange24h,
        priceChangePercentage24h = priceChangePercentage24h,
        circulatingSupply = circulatingSupply,
        totalSupply = totalSupply,
        maxSupply = maxSupply,
        ath = ath,
        athChangePercentage = athChangePercentage,
        atl = atl,
        atlChangePercentage = atlChangePercentage,
        lastUpdated = lastUpdated,
        isFavorite = isFavorite
    )
}

// CoinDetailDto to Domain Model
fun CoinDetailDto.toDomain(isFavorite: Boolean = false): CoinDetail {
    val currency = "usd"
    return CoinDetail(
        id = id,
        symbol = symbol,
        name = name,
        description = description.en.replace(Regex("<[^>]*>"), ""), // Remove HTML tags
        image = image.large,
        marketCapRank = marketCapRank,
        currentPrice = marketData.currentPrice[currency] ?: 0.0,
        marketCap = marketData.marketCap[currency] ?: 0L,
        totalVolume = marketData.totalVolume[currency] ?: 0L,
        high24h = marketData.high24h[currency] ?: 0.0,
        low24h = marketData.low24h[currency] ?: 0.0,
        priceChange24h = marketData.priceChange24h,
        priceChangePercentage24h = marketData.priceChangePercentage24h,
        priceChangePercentage7d = marketData.priceChangePercentage7d,
        priceChangePercentage14d = marketData.priceChangePercentage14d,
        priceChangePercentage30d = marketData.priceChangePercentage30d,
        priceChangePercentage60d = marketData.priceChangePercentage60d,
        priceChangePercentage200d = marketData.priceChangePercentage200d,
        priceChangePercentage1y = marketData.priceChangePercentage1y,
        marketCapChange24h = marketData.marketCapChange24h,
        marketCapChangePercentage24h = marketData.marketCapChangePercentage24h,
        circulatingSupply = marketData.circulatingSupply,
        totalSupply = marketData.totalSupply,
        maxSupply = marketData.maxSupply,
        ath = marketData.ath[currency] ?: 0.0,
        athChangePercentage = marketData.athChangePercentage[currency] ?: 0.0,
        athDate = marketData.athDate[currency] ?: "",
        atl = marketData.atl[currency] ?: 0.0,
        atlChangePercentage = marketData.atlChangePercentage[currency] ?: 0.0,
        atlDate = marketData.atlDate[currency] ?: "",
        links = CoinLinks(
            homepage = links.homepage.filter { it.isNotBlank() },
            blockchainSite = links.blockchainSite.filter { it.isNotBlank() },
            officialForumUrl = links.officialForumUrl.filter { it.isNotBlank() },
            subredditUrl = links.subredditUrl
        ),
        lastUpdated = lastUpdated,
        isFavorite = isFavorite
    )
}

// MarketChartDto to ChartData
fun MarketChartDto.toChartData(): ChartData {
    return ChartData(
        prices = prices.map { ChartEntry(it[0].toLong(), it[1]) },
        marketCaps = marketCaps.map { ChartEntry(it[0].toLong(), it[1]) },
        totalVolumes = totalVolumes.map { ChartEntry(it[0].toLong(), it[1]) }
    )
}

// CoinDetailDto to CoinDetailEntity
fun CoinDetailDto.toEntity(isFavorite: Boolean = false): CoinDetailEntity {
    val currency = "usd"
    val json = Json { ignoreUnknownKeys = true }
    
    return CoinDetailEntity(
        id = id,
        symbol = symbol,
        name = name,
        description = description.en.replace(Regex("<[^>]*>"), ""),
        image = image.large,
        marketCapRank = marketCapRank,
        currentPrice = marketData.currentPrice[currency] ?: 0.0,
        marketCap = marketData.marketCap[currency] ?: 0L,
        totalVolume = marketData.totalVolume[currency] ?: 0L,
        high24h = marketData.high24h[currency] ?: 0.0,
        low24h = marketData.low24h[currency] ?: 0.0,
        priceChange24h = marketData.priceChange24h,
        priceChangePercentage24h = marketData.priceChangePercentage24h,
        priceChangePercentage7d = marketData.priceChangePercentage7d,
        priceChangePercentage14d = marketData.priceChangePercentage14d,
        priceChangePercentage30d = marketData.priceChangePercentage30d,
        priceChangePercentage60d = marketData.priceChangePercentage60d,
        priceChangePercentage200d = marketData.priceChangePercentage200d,
        priceChangePercentage1y = marketData.priceChangePercentage1y,
        marketCapChange24h = marketData.marketCapChange24h,
        marketCapChangePercentage24h = marketData.marketCapChangePercentage24h,
        circulatingSupply = marketData.circulatingSupply,
        totalSupply = marketData.totalSupply,
        maxSupply = marketData.maxSupply,
        ath = marketData.ath[currency] ?: 0.0,
        athChangePercentage = marketData.athChangePercentage[currency] ?: 0.0,
        athDate = marketData.athDate[currency] ?: "",
        atl = marketData.atl[currency] ?: 0.0,
        atlChangePercentage = marketData.atlChangePercentage[currency] ?: 0.0,
        atlDate = marketData.atlDate[currency] ?: "",
        homepage = json.encodeToString(links.homepage.filter { it.isNotBlank() }),
        blockchainSite = json.encodeToString(links.blockchainSite.filter { it.isNotBlank() }),
        officialForumUrl = json.encodeToString(links.officialForumUrl.filter { it.isNotBlank() }),
        subredditUrl = links.subredditUrl,
        lastUpdated = lastUpdated,
        isFavorite = isFavorite,
        timestamp = System.currentTimeMillis()
    )
}

// CoinDetailEntity to Domain Model
fun CoinDetailEntity.toDomain(): CoinDetail {
    val json = Json { ignoreUnknownKeys = true }
    
    return CoinDetail(
        id = id,
        symbol = symbol,
        name = name,
        description = description,
        image = image,
        marketCapRank = marketCapRank,
        currentPrice = currentPrice,
        marketCap = marketCap,
        totalVolume = totalVolume,
        high24h = high24h,
        low24h = low24h,
        priceChange24h = priceChange24h,
        priceChangePercentage24h = priceChangePercentage24h,
        priceChangePercentage7d = priceChangePercentage7d,
        priceChangePercentage14d = priceChangePercentage14d,
        priceChangePercentage30d = priceChangePercentage30d,
        priceChangePercentage60d = priceChangePercentage60d,
        priceChangePercentage200d = priceChangePercentage200d,
        priceChangePercentage1y = priceChangePercentage1y,
        marketCapChange24h = marketCapChange24h,
        marketCapChangePercentage24h = marketCapChangePercentage24h,
        circulatingSupply = circulatingSupply,
        totalSupply = totalSupply,
        maxSupply = maxSupply,
        ath = ath,
        athChangePercentage = athChangePercentage,
        athDate = athDate,
        atl = atl,
        atlChangePercentage = atlChangePercentage,
        atlDate = atlDate,
        links = CoinLinks(
            homepage = json.decodeFromString(homepage),
            blockchainSite = json.decodeFromString(blockchainSite),
            officialForumUrl = json.decodeFromString(officialForumUrl),
            subredditUrl = subredditUrl
        ),
        lastUpdated = lastUpdated,
        isFavorite = isFavorite
    )
}

// MarketChartDto to MarketChartEntity
fun MarketChartDto.toEntity(coinId: String, days: Int): MarketChartEntity {
    val json = Json { ignoreUnknownKeys = true }
    
    return MarketChartEntity(
        cacheKey = "${coinId}_$days",
        coinId = coinId,
        days = days,
        prices = json.encodeToString(prices),
        marketCaps = json.encodeToString(marketCaps),
        totalVolumes = json.encodeToString(totalVolumes),
        timestamp = System.currentTimeMillis()
    )
}

// MarketChartEntity to ChartData
fun MarketChartEntity.toChartData(): ChartData {
    val json = Json { ignoreUnknownKeys = true }
    
    val pricesList: List<List<Double>> = json.decodeFromString(prices)
    val marketCapsList: List<List<Double>> = json.decodeFromString(marketCaps)
    val volumesList: List<List<Double>> = json.decodeFromString(totalVolumes)
    
    return ChartData(
        prices = pricesList.map { ChartEntry(it[0].toLong(), it[1]) },
        marketCaps = marketCapsList.map { ChartEntry(it[0].toLong(), it[1]) },
        totalVolumes = volumesList.map { ChartEntry(it[0].toLong(), it[1]) }
    )
}
