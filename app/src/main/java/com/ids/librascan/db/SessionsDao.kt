package com.ids.librascan.db


import androidx.room.*
import java.util.*
import kotlin.Unit


@Dao
interface SessionsDao {
    @Insert
    suspend fun insertSessions (sessions: Sessions)
    @Query("SELECT * FROM sessions_table")
    suspend fun getSessions(): List<Sessions>

    @Query("DELETE  FROM sessions_table WHERE id=:id")
    suspend fun deleteSession(id:Int?)

    @Query("SELECT * FROM sessions_table WHERE warehouseName=:warehouseName AND date=:date")
    suspend fun  getSession(warehouseName : String?,date: String?): Sessions

    @Query("DELETE FROM sessions_table ")
    suspend fun deleteAllSession()

}