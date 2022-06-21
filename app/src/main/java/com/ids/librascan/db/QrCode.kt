package com.ids.librascan.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "codeScan_table")
data class QrCode(
    var code: String ,
    var unitId : Int ,
    var quantity  :String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int =0
    override fun toString(): String {
        return code
    }
}


//, primaryKeys = ["code", "unitId"]


