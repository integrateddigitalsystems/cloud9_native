package com.ids.librascan.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "urlScan_table")
data class QrCode(
    var title: String

){
    @PrimaryKey(autoGenerate = true)
    var id: Int =0
}

@Entity(tableName = "unit_table")
data class Unit(
    var titleUnit: String

){
    @PrimaryKey(autoGenerate = true)
    var idUnit: Int =0
}



