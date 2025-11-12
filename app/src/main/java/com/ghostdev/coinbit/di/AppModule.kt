package com.ghostdev.coinbit.di

import androidx.room.Room
import com.ghostdev.coinbit.data.local.CoinDatabase
import com.ghostdev.coinbit.data.local.datastore.CacheManager
import com.ghostdev.coinbit.data.remote.api.CoinGeckoApi
import com.ghostdev.coinbit.data.remote.api.CoinGeckoApiImpl
import com.ghostdev.coinbit.data.repository.CoinRepositoryImpl
import com.ghostdev.coinbit.domain.repository.CoinRepository
import com.ghostdev.coinbit.domain.usecase.*
import com.ghostdev.coinbit.util.HttpClientFactory
import com.ghostdev.coinbit.util.NetworkConnectivityObserver
import com.ghostdev.coinbit.util.NetworkConnectivityObserverImpl
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> { HttpClientFactory.create() }
    single<CoinGeckoApi> { CoinGeckoApiImpl(get()) }
}

val dataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            CoinDatabase::class.java,
            CoinDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration() // Handle schema changes
        .build()
    }
    
    single { get<CoinDatabase>().coinDao() }
    single { get<CoinDatabase>().coinDetailDao() }
    single { get<CoinDatabase>().marketChartDao() }
    
    single { CacheManager(androidContext()) }
    
    single<CoinRepository> {
        CoinRepositoryImpl(
            api = get(),
            database = get(),
            cacheManager = get(),
            networkObserver = get()
        )
    }
}

val domainModule = module {
    factory { GetCoinsUseCase(get()) }
    factory { GetCoinDetailUseCase(get()) }
    factory { GetMarketChartUseCase(get()) }
    factory { SearchCoinsUseCase(get()) }
    factory { GetFavoriteCoinsUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
}

val viewModelModule = module {
    factory { 
        com.ghostdev.coinbit.presentation.coinlist.CoinListViewModel(
            getCoinsUseCase = get(),
            searchCoinsUseCase = get(),
            toggleFavoriteUseCase = get()
        )
    }
    factory {
        com.ghostdev.coinbit.presentation.coindetail.CoinDetailViewModel(
            getCoinDetailUseCase = get(),
            getMarketChartUseCase = get(),
            toggleFavoriteUseCase = get()
        )
    }
    factory {
        com.ghostdev.coinbit.presentation.favorites.FavoritesViewModel(
            getFavoriteCoinsUseCase = get(),
            toggleFavoriteUseCase = get()
        )
    }
}

val appModule = module {
    single<NetworkConnectivityObserver> {
        NetworkConnectivityObserverImpl(androidContext())
    }
}
