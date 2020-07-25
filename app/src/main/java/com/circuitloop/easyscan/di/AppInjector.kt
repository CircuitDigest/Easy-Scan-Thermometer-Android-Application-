package com.circuitloop.easyscan.di

import androidx.room.Room
import com.assignment.app.AppNavigator
import com.circuitloop.easyscan.database.DetailsDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
val DATABASE_NAME = "easyscan_database"


val appNavigatorModule = module { single { AppNavigator() } }
val dbModule = module {

    single {
        Room.databaseBuilder(
            androidContext(), DetailsDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    single { get<DetailsDatabase>().detailsTableDao }
}