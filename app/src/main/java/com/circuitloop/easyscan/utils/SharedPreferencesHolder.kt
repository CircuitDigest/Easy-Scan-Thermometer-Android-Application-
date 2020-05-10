package com.circuitloop.easyscan.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

const val appSharedPrefs = "AppSharedPrefs"

object SharedPreferencesHolder {

    lateinit var appSharedPreferences: SharedPreferences

    fun initializeSharedPrefs(context: Context) {
        appSharedPreferences = context.getSharedPreferences(appSharedPrefs, Context.MODE_PRIVATE)
    }
    fun deleteSharedPrefData() {
        appSharedPreferences.edit { clear() }
        appSharedPreferences.edit{ commit() }
    }

}