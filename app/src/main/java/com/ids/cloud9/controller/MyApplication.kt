@file:Suppress("NAME_SHADOWING")
package com.ids.mercury.controller

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import java.util.*

class MyApplication : Application() {
    companion object {
        internal lateinit var instance: MyApplication
        var selectedFragmentTag : String ?=""
        var selectedFragment  : Fragment?=null
        var showLogs: Boolean = true

    }


    override fun onCreate() {
        super.onCreate()

       /* sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sharedPreferencesEditor = sharedPreferences.edit()
        instance=this*/
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

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
