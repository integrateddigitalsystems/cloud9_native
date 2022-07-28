package com.ids.librascan.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.*


@Entity(tableName = "sessions_table")
data class Sessions(
    var sessionName:String = "",
    var warehouseName: String = "",
    var date: String = "",
    var description: String = "",
    var count: Int = 0
){
    @PrimaryKey(autoGenerate = true)
    var idSession: Int =0

    override fun toString(): String {
        return let {sessionName}
    }

}





