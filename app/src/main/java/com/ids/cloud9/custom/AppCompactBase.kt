package com.ids.cloud9.custom

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ids.cloud9.utils.AppHelper
import com.ids.cloud9.utils.LocaleUtils
import java.util.*

open class AppCompactBase : AppCompatActivity() /*, LifecycleObserver*/ {
    private var decorView: View? = null

    init {
        LocaleUtils.updateConfig(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppHelper.handleCrashes(this)
        super.onCreate(savedInstanceState)
        AppHelper.setLocal()

    }


    override fun onPause() {
        super.onPause()

    }

    override fun onResume() {
        super.onResume()

    }


    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onDestroy() {
        super.onDestroy()


        val restartServiceIntent = Intent(
            applicationContext, this.javaClass
        )
        restartServiceIntent.setPackage(packageName)

        val restartServicePendingIntent: PendingIntent = PendingIntent.getService(
            applicationContext,
            1,
            restartServiceIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val alarmService: AlarmManager =
            applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmService.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 1,
            restartServicePendingIntent
        )
        /* if(MyApplication.saveLocationTracking!!) {
             val broadcastIntent = Intent()
             broadcastIntent.setAction("restartservice")
             broadcastIntent.setClass(this, Restarter::class.java)
             this.sendBroadcast(broadcastIntent)
         }*/

    }


    override fun attachBaseContext(newBase: Context) {
        var newBase = newBase
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val config = newBase.resources.configuration
            config.setLocale(Locale.getDefault())
            newBase = newBase.createConfigurationContext(config)
        }
        super.attachBaseContext(newBase)
    }
}
