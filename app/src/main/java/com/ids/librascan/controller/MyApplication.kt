package com.ids.librascan.controller

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import android.preference.PreferenceManager
import androidx.fragment.app.FragmentManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.ids.librascan.model.FirebaseBaseUrlsArray
import com.ids.librascan.model.FirebaseLocalizeArray
import utils.AppConstants
import java.util.*

class MyApplication : Application() {

    companion object {
        internal lateinit var instance: MyApplication
        lateinit var sharedPreferences : SharedPreferences
        lateinit var sharedPreferencesEditor : SharedPreferences.Editor
        var BASE_URL = "https://invo.ids.com.lb/"
        var BASE_URLS: FirebaseBaseUrlsArray?= null
        var localizeArray: FirebaseLocalizeArray?= null
        var displayName = ""
        var showLogs: Boolean = true
        var bearer: String
            get() = sharedPreferences.getString("bearer", "")!!
            set(value) {
                sharedPreferencesEditor.putString("bearer", value).apply()
            }
        var clientKey: String
            get() = sharedPreferences.getString(AppConstants.CLIENT_KEY, "")!!
            set(value) {
                sharedPreferencesEditor.putString(AppConstants.CLIENT_KEY, value).apply()
            }
        var isFirst : Boolean
            get() = sharedPreferences.getBoolean(AppConstants.IS_FIRST, AppConstants.FIRST)
            set(value) { sharedPreferencesEditor.putBoolean(AppConstants.IS_FIRST, value).apply() }
        var languageCode : String
            get() = sharedPreferences.getString(AppConstants.SELECTED_LANGUAGE, AppConstants.LANG_ENGLISH)!!
            set(value) { sharedPreferencesEditor.putString(AppConstants.SELECTED_LANGUAGE, value).apply() }

        var isLogin : Boolean
            get() = sharedPreferences.getBoolean(AppConstants.IS_LOGIN, AppConstants.CHECK_LOGIN)
            set(value) { sharedPreferencesEditor.putBoolean(AppConstants.IS_LOGIN, value).apply() }

        var enableInsert : Boolean
            get() = sharedPreferences.getBoolean(AppConstants.IS_Enable, AppConstants.Enable)
            set(value) { sharedPreferencesEditor.putBoolean(AppConstants.IS_Enable, value).apply() }

        var enableNewLine : Boolean
            get() = sharedPreferences.getBoolean(AppConstants.IS_NEW_LINE, AppConstants.Enable)
            set(value) { sharedPreferencesEditor.putBoolean(AppConstants.IS_NEW_LINE, value).apply() }

        var isScan : Boolean
            get() = sharedPreferences.getBoolean(AppConstants.IS_SCAN, AppConstants.Enable)
            set(value) { sharedPreferencesEditor.putBoolean(AppConstants.IS_SCAN, value).apply() }
        var showSync : Boolean
            get() = sharedPreferences.getBoolean(AppConstants.SHOW_SYNC, AppConstants.Enable)
            set(value) { sharedPreferencesEditor.putBoolean(AppConstants.SHOW_SYNC, value).apply() }

        var sessionId :Int
            get() = sharedPreferences.getInt(AppConstants.SESSION_ID, 0)
            set(value) { sharedPreferencesEditor.putInt(AppConstants.SESSION_ID, value).apply() }

        var sessionName :String
            get() = sharedPreferences.getString(AppConstants.SESSION_NAME, "")!!
            set(value) { sharedPreferencesEditor.putString(AppConstants.SESSION_NAME, value).apply() }

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
