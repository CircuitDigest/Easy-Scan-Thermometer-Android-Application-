package com.circuitloop.easyscan.database

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface DetailsTableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(table: DetailsTable): Completable

    @Delete
    fun delete(pageTable: DetailsTable): Completable

    @Query("Select * From DetailsTable")
    fun getAllList(): Observable<List<DetailsTable>>

    @Query("Select * From DetailsTable where isSuspected = :bool")
    fun getSuspectedList(bool: Boolean = true): Observable<List<DetailsTable>>

    @Query("Select * From DetailsTable where lastDate = :date")
    fun getFilterListByDate(date: String): Observable<List<DetailsTable>>

    @Query("Select * From DetailsTable where serialNo = :id")
    fun getSerialValue(id: String): DetailsTable

    @Query("DELETE FROM DetailsTable")
    fun clearAllTables(): Completable


}