package com.ids.librascan.controller

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.fragment.app.FragmentManager
import com.ids.librascan.model.FirebaseBaseUrlsArray
import com.ids.librascan.model.FirebaseLocalizeArray
import utils.AppConstants
import java.util.*

class MyApplication : Application() {

    companion object {

        var selectedFragmentConstant:String=""
        var fragmentManager: FragmentManager?=null

        lateinit var sharedPreferences : SharedPreferences
        lateinit var sharedPreferencesEditor : SharedPreferences.Editor
        var BASE_URL = ""
        var BASE_URLS: FirebaseBaseUrlsArray?= null
        var force_update=false
        var last_version=""
        var UNIQUE_REQUEST_CODE = 0
        var firstLaunch: Boolean = true
        var localizeArray: FirebaseLocalizeArray?= null

        internal lateinit var instance: MyApplication
        var showLogs: Boolean = true

        var isFirst : Boolean
            get() = sharedPreferences.getBoolean(AppConstants.IS_FIRST, AppConstants.FIRST)
            set(value) { sharedPreferencesEditor.putBoolean(AppConstants.IS_FIRST, value).apply() }
        var languageCode : String
            get() = sharedPreferences.getString(AppConstants.SELECTED_LANGUAGE, AppConstants.LANG_ENGLISH)!!
            set(value) { sharedPreferencesEditor.putString(AppConstants.SELECTED_LANGUAGE, value).apply() }

        var isLogin : Boolean
            get() = sharedPreferences.getBoolean(AppConstants.IS_LOGIN, AppConstants.CHECK_LOGIN)
            set(value) { sharedPreferencesEditor.putBoolean(AppConstants.IS_LOGIN, value).apply() }

    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sharedPreferencesEditor = sharedPreferences.edit()
        instance =this

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
