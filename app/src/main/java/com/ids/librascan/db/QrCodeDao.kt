package com.ids.librascan.db


import androidx.room.*


@Dao
interface QrCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCode(qrCode: QrCode)

    @Query("SELECT * FROM codeScan_table")
    suspend fun getAllCode(): List<QrCode>

    @Query("SELECT * FROM codeScan_table WHERE code=:code AND unitId=:unitId")
    suspend fun  getCode(code : String?,unitId : Int?): QrCode

    @Update
    suspend fun updateUrl(qrCode:QrCode)

    @Delete
    suspend fun deleteCode(qrCode: QrCode)

    @Query("DELETE FROM codeScan_table ")
    suspend fun deleteAllCode()

}