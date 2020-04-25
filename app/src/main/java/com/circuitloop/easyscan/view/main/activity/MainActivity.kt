package com.circuitloop.easyscan.view.main.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.circuitloop.easyscan.R
import com.circuitloop.easyscan.view.main.fragment.MainFragment
import com.circuitloop.easyscan.view.main.fragment.TerminalFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment, MainFragment.newInstance())
                    .commitNow()
        }
    }
    override fun onNewIntent(intent: Intent) {
        if (intent.action == "android.hardware.usb.action.USB_DEVICE_ATTACHED") {
            (supportFragmentManager.findFragmentByTag("terminal") as TerminalFragment?)?.status("USB device detected")
        }
        super.onNewIntent(intent)
    }


}
