package utils

import android.content.res.Configuration
import android.view.ContextThemeWrapper
import java.util.*


class LocaleUtils {

    companion object {

        private var sLocale: Locale? = null

        fun setLocale(locale: Locale) {
            sLocale = locale
            if (sLocale != null) {
                Locale.setDefault(sLocale!!)
            }

        }

        fun updateConfig(wrapper: ContextThemeWrapper) {
            if (sLocale != null) {
                val configuration = Configuration()
                configuration.setLocale(sLocale)
                wrapper.applyOverrideConfiguration(configuration)
            }
        }

    }
}