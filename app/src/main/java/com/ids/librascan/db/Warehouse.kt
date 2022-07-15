package com.ids.librascan.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "warehouse_table")
data class Warehouse(
    @PrimaryKey(autoGenerate = false)
    var id: Int =0,
    var name:String = ""

){
    override fun toString(): String {
        return let {name}
    }


}