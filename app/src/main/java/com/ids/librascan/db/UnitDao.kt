package com.ids.librascan.db


import androidx.room.*
@Dao
interface UnitDao {
    @Insert
    suspend fun insertUnit (unit: Unit)
    @Query("SELECT title FROM unit_table")
    suspend fun getUnitData(): List<String>

}