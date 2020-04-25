package com.assignment.app

import android.content.Context
import android.content.Intent
import com.circuitloop.easyscan.view.main.activity.MainActivity


class AppNavigator {

    fun launchHomeScreen(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }

}