package com.ghostdev.coinbit.domain.usecase

import com.ghostdev.coinbit.domain.model.ChartData
import com.ghostdev.coinbit.domain.model.Coin
import com.ghostdev.coinbit.domain.model.CoinDetail
import com.ghostdev.coinbit.domain.repository.CoinRepository
import com.ghostdev.coinbit.util.Resource
import kotlinx.coroutines.flow.Flow

class GetCoinDetailUseCase(
    private val repository: CoinRepository
) {
    operator fun invoke(coinId: String): Flow<Resource<CoinDetail>> {
        return repository.getCoinById(coinId)
    }
}

class GetMarketChartUseCase(
    private val repository: CoinRepository
) {
    operator fun invoke(coinId: String, days: Int = 7): Flow<Resource<ChartData>> {
        return repository.getMarketChart(coinId, days)
    }
}

class SearchCoinsUseCase(
    private val repository: CoinRepository
) {
    operator fun invoke(query: String): Flow<List<Coin>> {
        return repository.searchCoins(query)
    }
}

class GetFavoriteCoinsUseCase(
    private val repository: CoinRepository
) {
    operator fun invoke(): Flow<List<Coin>> {
        return repository.getFavoriteCoins()
    }
}

class ToggleFavoriteUseCase(
    private val repository: CoinRepository
) {
    suspend operator fun invoke(coinId: String) {
        repository.toggleFavorite(coinId)
    }
}
