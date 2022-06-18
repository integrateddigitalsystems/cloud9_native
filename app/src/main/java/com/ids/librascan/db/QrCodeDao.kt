package com.ids.librascan.db


import androidx.room.*


@Dao
interface QrCodeDao {
    @Insert
    suspend fun insertUrl(qrCode: QrCode)

    @Query("SELECT * FROM urlScan_table")
    suspend fun getAllUrl(): List<QrCode>
    
    @Update
    suspend fun updateUrl(qrCode:QrCode)

   // @Query("DELETE FROM urlScan_table WHERE id = :id ")
    @Delete
    suspend fun deleteUrl(qrCode: QrCode)
}