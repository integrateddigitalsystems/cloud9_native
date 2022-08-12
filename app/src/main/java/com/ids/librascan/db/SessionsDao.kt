package com.ids.librascan.db


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction


@Dao
interface SessionsDao {
    @Insert
    suspend fun insertSessions (sessions: Sessions)

    @Query("SELECT * ,COUNT(idQrcode) as count,Max(sessionId) FROM sessions_table LEFT JOIN  codeScan_table ON sessions_table.idSession = codeScan_table.sessionId group by idSession ")
    suspend fun getSessions(): List<SessionQrcode>

    @Query("SELECT * FROM sessions_table")
    suspend fun getAllSessions(): List<Sessions>

    @Query("DELETE  FROM sessions_table WHERE idSession=:id")
    suspend fun deleteSession(id:Int?)

    @Query("SELECT * FROM sessions_table WHERE warehouseName=:warehouseName AND date=:date")
    suspend fun  getSession(warehouseName : String?,date: String?): Sessions

    @Query("DELETE FROM sessions_table ")
    suspend fun deleteAllSession()

    @Query("DELETE  FROM codeScan_table WHERE sessionId=:sessionId ")
    suspend fun deleteCodes(sessionId:Int?)

    @Transaction
    suspend fun deleteAll(id:Int?) {
        deleteCodes(id)
        deleteSession(id)
    }

}