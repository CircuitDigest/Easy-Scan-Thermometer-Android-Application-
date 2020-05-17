package com.circuitloop.easyscan.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

const val appSharedPrefs = "AppSharedPrefs"
const val filterSharedPrefs = "FilterSharedPrefs"


object SharedPreferencesHolder {

    lateinit var appSharedPreferences: SharedPreferences
    lateinit var filterSharedPreferences: SharedPreferences

    fun initializeSharedPrefs(context: Context) {
        appSharedPreferences = context.getSharedPreferences(appSharedPrefs, Context.MODE_PRIVATE)
    }

    fun initializeFilterSharedPrefs(context: Context) {
        filterSharedPreferences = context.getSharedPreferences(filterSharedPrefs, Context.MODE_PRIVATE)
    }

    fun deleteSharedPrefData() {
        appSharedPreferences.edit { clear() }
        appSharedPreferences.edit{ commit() }
    }

    fun deleteFilterSharedPrefData() {
        filterSharedPreferences.edit { clear() }
        filterSharedPreferences.edit{ commit() }
    }

}