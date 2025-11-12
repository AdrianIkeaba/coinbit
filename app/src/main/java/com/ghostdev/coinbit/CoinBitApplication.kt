package com.ghostdev.coinbit

import android.app.Application
import com.ghostdev.coinbit.di.appModule
import com.ghostdev.coinbit.di.dataModule
import com.ghostdev.coinbit.di.domainModule
import com.ghostdev.coinbit.di.networkModule
import com.ghostdev.coinbit.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CoinBitApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@CoinBitApplication)
            modules(
                appModule,
                networkModule,
                dataModule,
                domainModule,
                viewModelModule
            )
        }
    }
}
