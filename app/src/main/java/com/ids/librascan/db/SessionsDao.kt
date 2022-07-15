package com.ids.librascan.db


import androidx.room.*
import kotlin.Unit


@Dao
interface SessionsDao {
    @Insert
    suspend fun insertSessions (sessions: Sessions)
    @Query("SELECT * FROM sessions_table")
    suspend fun getSessions(): List<Sessions>

    @Query("DELETE  FROM sessions_table WHERE id=:id")
    suspend fun deleteSession(id:Int?)
}