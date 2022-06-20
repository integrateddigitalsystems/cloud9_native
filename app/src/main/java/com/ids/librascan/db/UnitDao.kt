package com.ids.librascan.db


import androidx.room.*
@Dao
interface UnitDao {
    @Insert
    suspend fun insertUnit (unit: Unit)
    @Query("SELECT * FROM unit_table")
    suspend fun getUnitData(): List<Unit>

}