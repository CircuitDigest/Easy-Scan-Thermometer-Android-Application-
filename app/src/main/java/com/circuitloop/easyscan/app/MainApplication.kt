package com.circuitloop.easyscan.app

import android.app.Application
import android.content.Context
import com.circuitloop.easyscan.di.appNavigatorModule
import com.circuitloop.easyscan.di.dbModule
import com.circuitloop.easyscan.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class MainApplication : Application() {

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(listOf(
                viewModelModule,
                appNavigatorModule,
                dbModule
            ))
        }
    }
}