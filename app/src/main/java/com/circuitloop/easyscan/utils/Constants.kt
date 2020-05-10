package com.circuitloop.easyscan.utils

import com.circuitloop.easyscan.BuildConfig

internal object Constants {
    // values have to be globally unique
    val INTENT_ACTION_GRANT_USB: String =
        BuildConfig.APPLICATION_ID.toString() + ".GRANT_USB"
    val INTENT_ACTION_DISCONNECT: String =
        BuildConfig.APPLICATION_ID.toString() + ".Disconnect"
    val NOTIFICATION_CHANNEL: String = BuildConfig.APPLICATION_ID.toString() + ".Channel"
    val INTENT_CLASS_MAIN_ACTIVITY: String =
        BuildConfig.APPLICATION_ID.toString() + ".MainActivity"
    // values have to be unique within each app
    const val NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001
    val CAMERA_INTENT: Int = 100
    val RESULT_INTENT = "RESULT_INTENT"
    val INTENT_KEY_HOMEBASEMODEL = "INTENT_KEY_HOMEBASEMODEL"
    val INTENT_KEY_VIEWFINDERWIDTH = "INTENT_KEY_VIEWFINDERWIDTH"
    val INTENT_KEY_VIEWFINDERHEIGHT = "INTENT_KEY_VIEWFINDERHEIGHT"
    val SHARED_PREF_KEY_SETTING_DATA = "SHARED_PREF_KEY_SETTING_DATA"
    val SHARED_PREF_KEY_LIST_DATA = "SHARED_PREF_KEY_LIST_DATA"
    val SHARED_PREF_KEY_LIST_INT_DATA = "SHARED_PREF_KEY_LIST_INT_DATA"
}
