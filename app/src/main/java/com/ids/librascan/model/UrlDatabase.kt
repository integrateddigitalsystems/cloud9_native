package com.ids.librascan.model

import android.app.Activity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.locks.Lock

@Database(
    entities = [Url::class], version = 1
)
abstract class UrlDatabase : RoomDatabase() {
    abstract fun getUrlDao(): UrlDao
    companion object{
       @Volatile private var instance  : UrlDatabase? = null
        private  var LOCK = Any()

        operator  fun invoke(activity: Activity) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(activity).also {
                instance= it
            }
        }
        private fun buildDatabase(activity: Activity) = Room.databaseBuilder(
            activity.application,
            UrlDatabase::class.java,
            "urlDatabase"
        ).build()
    }
}