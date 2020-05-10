package com.circuitloop.easyscan.app

import android.app.Application
import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import com.circuitloop.easyscan.di.appNavigatorModule
import com.circuitloop.easyscan.di.dbModule
import com.circuitloop.easyscan.di.viewModelModule
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class MainApplication : Application() {

    companion object {
        lateinit var appContext: Context
        var mFirebaseAnalytics: FirebaseAnalytics? = null
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
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