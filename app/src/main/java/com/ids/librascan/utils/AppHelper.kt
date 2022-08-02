package com.ids.librascan.utils


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.res.Configuration
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import com.ids.librascan.R
import com.ids.librascan.controller.MyApplication
import utils.*
import java.util.*


class AppHelper {
    companion object {
        fun changeLanguage(context: Context, language: String) {
            when (language) {
                AppConstants.LANG_ARABIC -> Locale.setDefault(Locale("ar"))
                AppConstants.LANG_ENGLISH -> Locale.setDefault(Locale.ENGLISH)
                "0" -> {
                    Locale.setDefault(Locale.ENGLISH)
                }
            }

            val configuration = Configuration()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(Locale.getDefault())
                configuration.setLayoutDirection(Locale.getDefault())

            } else
                configuration.locale = Locale.getDefault()


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                context.createConfigurationContext(configuration)
            } else {
                context.resources.updateConfiguration(
                    configuration,
                    context.resources.displayMetrics
                )
            }
            MyApplication.languageCode = language


        }

             fun setLocal(context: Context) {

            if (MyApplication.languageCode == AppConstants.LANG_ENGLISH) {
                LocaleUtils.setLocale(Locale("en"))
            } else if (MyApplication.languageCode == AppConstants.LANG_ARABIC) {
                LocaleUtils.setLocale(Locale("ar"))
            }

        }

        fun setAllTexts(v: View?, context: Context) {
            if (MyApplication.localizeArray != null) {
                try {
                    if (v is ViewGroup) {
                        val vg = v
                        for (i in 0 until vg.childCount) {
                            val child = vg.getChildAt(i)
                            setAllTexts(child, context)
                        }
                    } else if (v is TextView && v !is EditText) {
                        v.textRemote(v.tag.toString(), context)
                    } else if (v is Button) {
                        v.textRemote(v.tag.toString(), context)
                    } else if (v is EditText) {
                        setHintTag(v, v.tag.toString(), context)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

        @SuppressLint("NewApi")
        fun isNetworkAvailable(context: Context): Boolean {
            val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            return capabilities?.hasCapability(NET_CAPABILITY_INTERNET) == true
        }

        fun setHintTag(view: View, tag: String, context: Context) {
            val edit = view as EditText
            if (MyApplication.localizeArray != null) {
                try {
                    edit.hint =
                        MyApplication.localizeArray!!.messages!!.find { it.localize_Key == tag }!!
                            .getMessage()
                } catch (e: Exception) {
                    try {
                        val resId =
                            context.resources.getIdentifier(tag, "string", context.packageName)
                        edit.hint = context.resources.getString(resId)
                    } catch (e: Exception) {
                    }
                }

            }
        }

        fun getRemoteString(key: String, con: Context): String {
            if (MyApplication.localizeArray != null) {
                try {
                    return MyApplication.localizeArray!!.messages!!.find { it.localize_Key == key }!!
                        .getMessage()!!
                } catch (e: Exception) {
                    try {
                        val resId = con.resources.getIdentifier(key, "string", con.packageName)
                        return con.resources.getString(resId)
                    } catch (e: Exception) {
                        return ""
                    }
                }

            } else {
                val resId = con.resources.getIdentifier(key, "string", con.packageName)
                return con.resources.getString(resId)
            }


        }
        fun getTypeFace(context: Context): Typeface {
             return if (Locale.getDefault().language == "ar")
                Typeface.createFromAsset(
                    context.applicationContext.assets,
                    "fonts/HelveticaNeueLTArabic-Bold.ttf"
                )
            else
           return Typeface.createFromAsset(context.applicationContext.assets, "fonts/HelveticaNeueLTArabic-Bold.ttf")

        }
        fun getTypeFaceBold(context: Context): Typeface {
            return if (Locale.getDefault().language == "ar")
                Typeface.createFromAsset(
                    context.applicationContext.assets,
                    "fonts/HelveticaNeueLTArabic-Bold.ttf"
                )

            else
              return  Typeface.createFromAsset(
                    context.applicationContext.assets,
                    "fonts/HelveticaNeue_bold.ttf"
                )

        }

        fun closeKeyboard(context: Activity) {

            val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                context.currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }


        fun createDialogPositive(c: Activity, message: String) {

            val builder = AlertDialog.Builder(c)
            builder
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton(c.getString(R.string.ok)){ dialog, _ -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        }

        fun createDialogError(activity: Activity, errorMessage: String,titleMessage:String) {
            val builder = android.app.AlertDialog.Builder(activity)

            val textView: TextView
            val llItem: LinearLayout
            val textViewtitle: TextView

            val inflater = activity.layoutInflater
            val textEntryView = inflater.inflate(R.layout.error_dialog, null)
            textView = textEntryView.findViewById(R.id.tvDetails)
            textViewtitle = textEntryView.findViewById(R.id.tvTitle)
            llItem = textEntryView.findViewById(R.id.llItem)

            textView.text = errorMessage
            textViewtitle.text = titleMessage



            llItem.setOnClickListener {
                if (textView.visibility == View.VISIBLE)
                    textView.hide()
                else
                    textView.show()
            }

            builder.setView(textEntryView)
                .setNegativeButton(activity.resources.getString(R.string.done)) { dialog, _ ->
                    dialog.dismiss()
                }
            val d = builder.create()
            d.setOnShowListener {
                d.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
                d.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).transformationMethod = null
                d.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
            }
            d.setCancelable(false)
            d.show()
        }

        fun handleCrashes(context: Activity) {
            Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandler(context))
        }

    }

    fun htmlString(string: String): Spanned {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY)
        } else {
            return Html.fromHtml(string)
        }
    }

}
