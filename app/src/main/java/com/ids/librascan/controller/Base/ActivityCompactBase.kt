package Base


import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ids.librascan.utils.AppHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

import utils.LocaleUtils
import java.util.*
import kotlin.coroutines.CoroutineContext

open class ActivityCompactBase : AppCompatActivity(),CoroutineScope {

    private lateinit var job: Job
    init {
        AppHelper.setLocal(this)
        LocaleUtils.updateConfig(this)
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppHelper.setLocal(this)
        job =Job()
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

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


}
