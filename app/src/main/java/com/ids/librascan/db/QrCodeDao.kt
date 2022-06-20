package com.ids.librascan.db


import androidx.room.*
import java.util.concurrent.Flow


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

   /* @Query("SELECT * FROM urlScan_table WHERE title LIKE :title")
    suspend fun searchDatabase(title: String)*/
}