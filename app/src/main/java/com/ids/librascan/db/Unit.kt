package com.ids.librascan.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unit_table")
data class Unit(
    var title:String = ""

){
    @PrimaryKey(autoGenerate = true)
    var id: Int =0

    override fun toString(): String {
            return let {title}
        }


}

