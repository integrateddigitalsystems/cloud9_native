package com.ids.librascan.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "codeScan_table", primaryKeys = ["code"])
data class QrCode(
    var code: String = "" ,
    var unitId : Int = 0 ,
    var quantity  :Int = 0,
    var sessionId : Int = 0
){
    override fun toString(): String {
        return code
    }
}



