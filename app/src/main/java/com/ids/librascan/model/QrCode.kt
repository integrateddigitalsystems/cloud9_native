package com.ids.librascan.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "urlScan_table")
data class QrCode(
    var title: String

){
    @PrimaryKey(autoGenerate = true)
    var id: Int =0
}


