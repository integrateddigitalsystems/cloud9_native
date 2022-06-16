package Base


import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ids.librascan.utils.AppHelper

import utils.LocaleUtils
import java.util.*

open class ActivityCompactBase : AppCompatActivity() {

    init {
        AppHelper.setLocal(this)
        LocaleUtils.updateConfig(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppHelper.setLocal(this)
      //  AppHelper.setAllTexts(rootLayout,this)
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
