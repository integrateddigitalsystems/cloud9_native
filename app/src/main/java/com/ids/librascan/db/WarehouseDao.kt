package com.ids.librascan.db


import androidx.room.*

@Dao
interface WarehouseDao {
    @Insert
    suspend fun insertWarehouse (warehouse: Warehouse)
    @Query("SELECT * FROM warehouse_table")
    suspend fun getWarehouse(): List<Warehouse>


}