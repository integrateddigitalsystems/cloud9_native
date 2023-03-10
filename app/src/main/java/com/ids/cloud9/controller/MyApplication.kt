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
import dev.b3nedikt.app_locale.AppLocale
import dev.b3nedikt.restring.Restring
import dev.b3nedikt.reword.RewordInterceptor
import dev.b3nedikt.viewpump.ViewPump
import com.ids.cloud9.utils.AppConstants
import com.ids.cloud9.utils.AppConstants.LOCALE_ARABIC
import java.util.*
import kotlin.collections.ArrayList


class MyApplication : Application() {

    companion object {
        internal lateinit var instance: MyApplication
        var selectedFragmentTag : String ?=""
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
        var generalNotificaiton = 1
        var UNIQUE_REQUEST_CODE = 908
        var address : ResponseAddress ?=null
        var selectedProduct:ProductListItem  ?= null
        var selectedReccomend : FilteredActivityListItem ?=null
        var mobileConfig : MobileConfig?=null
        var languageCode : String ?="en"
        var units : kotlin.collections.ArrayList<UnitListItem> = arrayListOf()
        var isSignatureEmptyEmp = false
        var isSignatureEmptyClient = false
        var locMessages : ArrayList<LocalisedMessage> = arrayListOf()
        var selectedFragmentConstant:String=""
        @SuppressLint("StaticFieldLeak")
        var showLogs: Boolean = true
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
