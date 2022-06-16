package com.ids.librascan.model


import androidx.room.*


@Dao
interface UrlDao {
    @Insert
    suspend fun insertUrl(url: Url)

    @Query("SELECT * FROM urlScan_table")
    fun getAllUrl(): List<Url>

    @Insert
    fun addUrl (vararg url: Url)

    @Update
    suspend fun updateUrl(url: Url)

    @Delete
    suspend fun deleteUrl(url: Url)
}