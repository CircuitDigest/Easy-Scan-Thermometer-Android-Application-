package com.circuitloop.easyscan.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class DetailsTable {
    @PrimaryKey(autoGenerate = true)
    var serialNo: Long?=null
    var temperature: String? = null
    var time:String? = null
    var isSuspected:Boolean = false
}

