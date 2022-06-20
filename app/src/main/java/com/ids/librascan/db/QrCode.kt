package com.ids.librascan.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "urlScan_table")
data class QrCode(
    var title: String = "",
    var quantity  :String = "",
    var unitId : String =" "

){
    @PrimaryKey(autoGenerate = true)
    var id: Int =0

    override fun toString(): String {
        return title
    }
}





