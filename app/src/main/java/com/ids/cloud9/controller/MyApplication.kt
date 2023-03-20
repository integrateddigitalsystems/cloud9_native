package com.ids.cloud9.controller

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.ids.cloud9.apis.WifiService
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.AppConstants
import com.ids.cloud9.utils.AppConstants.LOCALE_ARABIC
import dev.b3nedikt.app_locale.AppLocale
import dev.b3nedikt.restring.Restring
import dev.b3nedikt.reword.RewordInterceptor
import dev.b3nedikt.viewpump.ViewPump
import java.util.*


class MyApplication : Application() {

    companion object {
        internal lateinit var instance: MyApplication
        var BASE_URL ="https://crmtest.ids.com.lb/api/"
        var BASE_URL_IMAGE = "https://crmtest.ids.com.lb"
        var arrayVid : ArrayList<ItemSpinner> = arrayListOf()
        var selectedVisit : Visit ?=null
        var allVisits : ArrayList<Visit> = arrayListOf()
        var serviceContext : Context ?=null
        var userItem : JWTResponse ?=null
        var permission =-1
        var onTheWayVisit : Visit ?=null
        var saveLocTracking = false
        var showNotfs = false
        var gettingTracked = false
        var activityVisible = false
        var UNIQUE_REQUEST_CODE = 908
        var address : ResponseAddress ?=null
        var selectedProduct:ProductListItem  ?= null
        var selectedReccomend : FilteredActivityListItem ?=null
        var mobileConfig : MobileConfig?=null
        var languageCode : String ?="en"
        var units : ArrayList<UnitListItem> = arrayListOf()
        var isSignatureEmptyEmp = false
        var isSignatureEmptyClient = false
        var permissionAllow11  : Int?
            get() = sharedPreferences.getInt(AppConstants.PERMISSION,0)
            set(value) { sharedPreferencesEditor.putInt(AppConstants.PERMISSION, value!!).apply() }
        var locMessages : ArrayList<LocalisedMessage> = arrayListOf()
        @SuppressLint("StaticFieldLeak")
        lateinit var sharedPreferences : SharedPreferences
        lateinit var sharedPreferencesEditor : SharedPreferences.Editor
        var firebaseToken : String
            get() = sharedPreferences.getString(AppConstants.FIREBASE_TOKEN, "")!!
            set(value) { sharedPreferencesEditor.putString(AppConstants.FIREBASE_TOKEN, value).apply() }
        var loggedIn : Boolean
            get() = sharedPreferences.getBoolean(AppConstants.LOGGED_IN, false)
            set(value) { sharedPreferencesEditor.putBoolean(AppConstants.LOGGED_IN, value).apply() }
        var token : String
            get() = sharedPreferences.getString(AppConstants.TOKEN, "")!!
            set(value) { sharedPreferencesEditor.putString(AppConstants.TOKEN, value).apply() }
        var email : String
            get() = sharedPreferences.getString(AppConstants.EMAIL, "")!!
            set(value) { sharedPreferencesEditor.putString(AppConstants.EMAIL, value).apply() }
        var password : String
            get() = sharedPreferences.getString(AppConstants.PASSWORD, "")!!
            set(value) { sharedPreferencesEditor.putString(AppConstants.PASSWORD, value).apply() }

        fun isActivityVisible(): Boolean {
            return activityVisible
        }

        fun activityResumed() {
            activityVisible = true
            showNotfs = false
        }

        fun activityPaused() {
            activityVisible = false
            showNotfs = true
        }
    }


    override fun onCreate() {
        super.onCreate()
        AppLocale.supportedLocales = listOf(Locale.ENGLISH, LOCALE_ARABIC)
        Restring.init(this)
        Restring.localeProvider = AppLocaleLocaleProvider
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sharedPreferencesEditor = sharedPreferences.edit()
        ViewPump.init(RewordInterceptor)
        /*sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sharedPreferencesEditor = sharedPreferences.edit()*/
        instance =this
        setupServices()
    }
    override fun getResources(): Resources {
        return Restring.wrapResources(applicationContext, super.getResources())
    }

    override fun attachBaseContext(newBa: Context) {
        var newBase = newBa
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
