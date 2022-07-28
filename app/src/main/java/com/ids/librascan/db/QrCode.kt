package com.ids.librascan.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "codeScan_table")
data class QrCode(
    var code: String? = null,
    var unitId: Int = 0,
    var quantity:Int = 0,
    var sessionId: Int = 0
){
    @PrimaryKey(autoGenerate = true)
    var idQrcode: Int =0

}



