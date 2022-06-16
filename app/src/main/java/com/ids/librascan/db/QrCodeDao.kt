package com.ids.librascan.db


import androidx.room.*


@Dao
interface QrCodeDao {
    @Insert
    suspend fun insertUrl(url: QrCode)

    @Query("SELECT * FROM urlScan_table")
    suspend fun getAllUrl(): List<QrCode>

   /* @Insert
    suspend fun insertUnit (unit: Unit)*/

   // @Query("SELECT * FROM unit_table")
   // suspend fun getUnit(): List<Unit>

    @Update
    suspend fun updateUrl(url: QrCode)

    @Delete
    suspend fun deleteUrl(url: QrCode)
}