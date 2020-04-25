package com.circuitloop.easyscan.view.splash.activity

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.assignment.app.AppNavigator
import com.circuitloop.easyscan.R
import com.circuitloop.easyscan.databinding.ActivitySplashBinding
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {

    private val appNavigator: AppNavigator by inject()
    private val SPLASH_TIME_DELAY: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivitySplashBinding>(this, R.layout.activity_splash)
        startNextActivityWithDelay()
    }

    private fun startNextActivityWithDelay() {
        Handler().postDelayed({
            appNavigator.launchHomeScreen(this)
            finish()
        }, SPLASH_TIME_DELAY)

    }
}
