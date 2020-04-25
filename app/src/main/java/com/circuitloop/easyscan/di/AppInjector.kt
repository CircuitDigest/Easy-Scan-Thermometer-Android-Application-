package com.circuitloop.easyscan.di

import androidx.room.Room
import com.assignment.app.AppNavigator
import com.circuitloop.easyscan.database.DetailsDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val appNavigatorModule = module { single { AppNavigator() } }
val dbModule = module {

    single {
        Room.databaseBuilder(
            androidContext(), DetailsDatabase::class.java,
            "SS"
        ).build()
    }

    single { get<DetailsDatabase>().detailsTableDao }
}