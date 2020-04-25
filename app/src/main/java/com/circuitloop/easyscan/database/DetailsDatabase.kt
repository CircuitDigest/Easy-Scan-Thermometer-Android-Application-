package com.circuitloop.easyscan.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DetailsTable::class], version = 1,
    exportSchema = false
)
abstract class DetailsDatabase : RoomDatabase() {
    abstract val detailsTableDao: DetailsTableDao
}