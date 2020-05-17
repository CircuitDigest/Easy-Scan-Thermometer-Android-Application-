package com.circuitloop.easyscan.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class DetailsTable {
    @PrimaryKey(autoGenerate = true)
    var autoGenerateId: Long?=null
    var serialNo: String = "0"
    var temperature: String? = null
    var date: Long? = null
    var time: String? = null
    var lastDate: String? = null
    var isSuspected:Boolean = false
    var imgPath:String? = null
    var isFilterList:Boolean = false
}

