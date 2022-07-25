package com.ids.librascan.db



import androidx.room.*



@Dao
interface QrCodeDao {
    @Insert()
    suspend fun insertCode(qrCode: QrCode)

    @Query("SELECT * FROM codeScan_table ORDER BY id DESC")
    suspend fun getAllCode(): List<QrCode>

    @Query("SELECT * FROM codeScan_table WHERE sessionId=:sessionId ORDER BY id DESC")
    suspend fun getCode(sessionId:Int?): List<QrCode>

    @Query("SELECT * FROM codeScan_table WHERE code=:code AND sessionId=:sessionId ORDER BY id DESC")
    suspend fun  getCode(code : String?,sessionId : Int?): QrCode

    @Delete
    suspend fun deleteCode(qrCode: QrCode)

    @Query("UPDATE codeScan_table SET quantity=:quantity WHERE id = :id")
    suspend fun updateCode(quantity : Int?,id : Int?)

    @Query("DELETE FROM codeScan_table")
    suspend fun deleteAllCode()

    @Query("DELETE  FROM codeScan_table WHERE id=:sessionId ")
    suspend fun deleteCode(sessionId:Int?)




}