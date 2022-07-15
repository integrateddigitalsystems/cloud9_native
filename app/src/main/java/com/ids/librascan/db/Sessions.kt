package com.ids.librascan.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions_table")
data class Sessions(
    var sessionName:String = "",
    var warehouseName: String = "",
    var date: Int =0,
    var description: String = ""

){
    @PrimaryKey(autoGenerate = true)
    var id: Int =0

    override fun toString(): String {
        return let {sessionName}
    }


}