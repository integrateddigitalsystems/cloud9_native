package com.ids.librascan.controller

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.ids.librascan.apis.WifiService
import com.ids.librascan.model.AppLocaleLocaleProvider
import com.ids.librascan.model.FirebaseBaseUrlsArray
import com.ids.librascan.model.FirebaseLocalizeArray
import dev.b3nedikt.app_locale.AppLocale
import dev.b3nedikt.restring.Restring
import dev.b3nedikt.reword.RewordInterceptor
import dev.b3nedikt.viewpump.ViewPump
import utils.AppConstants
import utils.AppConstants.LOCALE_ARABIC

import java.util.*

class MyApplication : Application() {

    companion object {
        internal lateinit var instance: MyApplication
        lateinit var sharedPreferences : SharedPreferences
        lateinit var sharedPreferencesEditor : SharedPreferences.Editor
        var BASE_URL = "https://invo.ids.com.lb/"
        var BAS = "https://fakerapi.it"
        var BASE_URLS: FirebaseBaseUrlsArray?= null
        var localizeArray: FirebaseLocalizeArray?= null
        @SuppressLint("StaticFieldLeak")
        var showLogs: Boolean = true
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
            get() = sharedPreferences.getBoolean(AppConstants.IS_Enable, AppConstants.Default)
            set(value) { sharedPreferencesEditor.putBoolean(AppConstants.IS_Enable, value).apply() }

        var enableNewLine : Boolean
            get() = sharedPreferences.getBoolean(AppConstants.IS_NEW_LINE, AppConstants.Default)
            set(value) { sharedPreferencesEditor.putBoolean(AppConstants.IS_NEW_LINE, value).apply() }

        var isScan : Boolean
            get() = sharedPreferences.getBoolean(AppConstants.IS_SCAN, AppConstants.Default)
            set(value) { sharedPreferencesEditor.putBoolean(AppConstants.IS_SCAN, value).apply() }

        var sessionId :Int
            get() = sharedPreferences.getInt(AppConstants.SESSION_ID, 0)
            set(value) { sharedPreferencesEditor.putInt(AppConstants.SESSION_ID, value).apply() }

        var sessionName :String
            get() = sharedPreferences.getString(AppConstants.SESSION_NAME, "")!!
            set(value) { sharedPreferencesEditor.putString(AppConstants.SESSION_NAME, value).apply() }




    }
    override fun onCreate() {
        super.onCreate()
        AppLocale.supportedLocales = listOf(Locale.ENGLISH, LOCALE_ARABIC)
        Restring.init(this)
        Restring.localeProvider = AppLocaleLocaleProvider
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        ViewPump.init(RewordInterceptor)

        sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sharedPreferencesEditor = sharedPreferences.edit()
        instance =this
        setupServices()
    }

    override fun getResources(): Resources {
        return Restring.wrapResources(applicationContext, super.getResources())
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
    private fun setupServices() {
        WifiService.instance.initializeWithApplicationContext(this)
    }

}
