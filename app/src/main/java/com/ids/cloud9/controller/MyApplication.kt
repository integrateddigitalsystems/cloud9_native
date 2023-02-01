package com.ids.cloud9.controller

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.ids.cloud9.apis.WifiService
import com.ids.cloud9.model.*
import dev.b3nedikt.app_locale.AppLocale
import dev.b3nedikt.restring.Restring
import dev.b3nedikt.reword.RewordInterceptor
import dev.b3nedikt.viewpump.ViewPump
import com.ids.cloud9.utils.AppConstants
import com.ids.cloud9.utils.AppConstants.LOCALE_ARABIC
import java.util.*


class MyApplication : Application() {

    companion object {
        internal lateinit var instance: MyApplication
        var selectedFragmentTag : String ?=""
        var BASE_URL ="https://crmtest.ids.com.lb/api/"
        var selectedFragment  : Fragment?=null
        var token : String ?=""
        var selectedVisit : VisitListItem ?=null
        var userItem : JWTResponse ?=null
        var permission =-1
        var selectedProduct:ProductsItem  ?= null
        var mobileConfig : MobileConfig?=null
        var languageCode : String ?="en"
        var isSignatureEmpty = false
        var locMessages : ArrayList<LocalisedMessage> = arrayListOf()
        var selectedFragmentConstant:String=""
        @SuppressLint("StaticFieldLeak")
        var showLogs: Boolean = true



    }
    override fun onCreate() {
        super.onCreate()
        AppLocale.supportedLocales = listOf(Locale.ENGLISH, LOCALE_ARABIC)
        Restring.init(this)
        Restring.localeProvider = AppLocaleLocaleProvider
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

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
