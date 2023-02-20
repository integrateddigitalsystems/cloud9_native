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
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.ViewPumpAppCompatDelegate
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.utils.AppConstants
import com.ids.cloud9.utils.AppHelper
import com.ids.cloud9.utils.LocaleUtils
import dev.b3nedikt.restring.Restring
import java.util.*

open class AppCompactBase : AppCompatActivity() {
    private val appCompatDelegate: AppCompatDelegate by lazy {
        ViewPumpAppCompatDelegate(
            baseDelegate = super.getDelegate(),
            baseContext = this,
            wrapContext = Restring::wrapContext
        )
    }
    init {
        AppHelper.setLocal()
    }
    override fun getDelegate(): AppCompatDelegate {
        return appCompatDelegate
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppHelper.setLocal()
        if (MyApplication.languageCode == AppConstants.LANG_ENGLISH) {
            AppHelper.setLocalStrings(this, "en", Locale("en"))
        } else if (MyApplication.languageCode == AppConstants.LANG_ARABIC) {
            AppHelper.setLocalStrings(this, "ar", Locale("ar"))
        }
    }
    override fun attachBaseContext(newBase: Context) {
        var newBase = newBase
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val config = newBase.resources.configuration
            config.setLocale(Locale.getDefault())
            newBase = newBase.createConfigurationContext(config)
        }
        super.attachBaseContext(newBase)
    }
}
