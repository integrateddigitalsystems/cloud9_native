package com.ids.librascan.model


import androidx.room.*


@Dao
interface QrCodeDao {
    @Insert
    suspend fun insertUrl(url: QrCode)

    @Query("SELECT * FROM urlScan_table")
    suspend fun getAllUrl(): List<QrCode>

    @Insert
    suspend fun addUrl (vararg url: QrCode)

    @Update
    suspend fun updateUrl(url: QrCode)

    @Delete
    suspend fun deleteUrl(url: QrCode)
}