package com.ghostdev.coinbit.domain.usecase

import com.ghostdev.coinbit.domain.model.Coin
import com.ghostdev.coinbit.domain.repository.CoinRepository
import com.ghostdev.coinbit.util.Resource
import kotlinx.coroutines.flow.Flow

class GetCoinsUseCase(
    private val repository: CoinRepository
) {
    operator fun invoke(forceRefresh: Boolean = false): Flow<Resource<List<Coin>>> {
        return repository.getCoins(forceRefresh)
    }
}
