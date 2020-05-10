package com.circuitloop.easyscan.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class DetailsTable {
    @PrimaryKey(autoGenerate = true)
    var serialNo: Long?=null
    var temperature: String? = null
    var date: Long? = null
    var time: String? = null
    var lastDate: String? = null
    var isSuspected:Boolean = false
    var imgPath:String? = null
}

