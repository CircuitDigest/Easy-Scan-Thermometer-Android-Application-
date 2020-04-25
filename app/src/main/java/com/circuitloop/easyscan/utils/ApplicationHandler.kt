package com.circuitloop.easyscan.utils

import android.view.View

interface ApplicationHandler {
    fun onClick(view: View?,data:Any?=null,position:Any?=null)
}