package com.ids.librascan.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [QrCode::class,Unit::class], version = 2)
abstract class QrCodeDatabase : RoomDatabase() {
    abstract fun getUrlDao(): QrCodeDao
    abstract fun getUnit(): UnitDao
    companion object{
       @Volatile private var instance  : QrCodeDatabase? = null
        private  var LOCK = Any()

        operator  fun invoke(application: Application) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(application).also {
                instance = it
            }
        }
        private fun buildDatabase(application: Application) = Room.databaseBuilder(
            application,
            QrCodeDatabase::class.java,
            "urlDatabase"
        ).build()
    }
}