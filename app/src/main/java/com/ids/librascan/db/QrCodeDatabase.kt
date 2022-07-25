package com.ids.librascan.db

import android.app.Application
import androidx.room.*

@Database(entities = [QrCode::class,Unit::class,Warehouse::class,Sessions::class], version = 4)
abstract class QrCodeDatabase : RoomDatabase() {
    abstract fun getCodeDao(): QrCodeDao
    abstract fun getUnit(): UnitDao
    abstract fun getWarehouse():WarehouseDao
    abstract fun getSessions():SessionsDao
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