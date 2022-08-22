package com.ids.librascan.utils


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.ids.librascan.R
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ErrorDialogBinding
import com.ids.librascan.databinding.ItemDialogSyncBinding
import dev.b3nedikt.restring.Restring
import dev.b3nedikt.restring.toMutableRepository
import dev.b3nedikt.reword.Reword
import utils.*
import java.io.IOException
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

            MyApplication.languageCode = language
            val configuration = Configuration()
            configuration.setLocale(Locale.getDefault())
            configuration.setLayoutDirection(Locale.getDefault())
            context.createConfigurationContext(configuration)

        }

          fun setLocal() {

            if (MyApplication.languageCode == AppConstants.LANG_ENGLISH) {
                LocaleUtils.setLocale(Locale("en"))
           } else if (MyApplication.languageCode == AppConstants.LANG_ARABIC) {
                LocaleUtils.setLocale(Locale("ar"))
            }

        }

        fun setLocalStrings(activity: Activity,language: String,locale: Locale) {
            Restring.stringRepository.toMutableRepository().strings.clear()
            if(MyApplication.localizeArray != null){
            try {
                val myStringsMap: HashMap<String, String> = HashMap()
                MyApplication.localizeArray!!.messages!!.forEachIndexed { index, _ ->
                    run {
                        myStringsMap[MyApplication.localizeArray!!.messages!![index].localize_Key!!] =
                            if(language == "en") MyApplication.localizeArray!!.messages!![index].message_en!! else MyApplication.localizeArray!!.messages!![index].message_ar!!
                    }
                }
                Restring.putStrings(locale, myStringsMap)
                Restring.locale = locale
                updateView(activity)
            } catch (exception: IOException) {
                wtf( exception.message.toString() )
            }
            }
        }

        private fun updateView(activity: Activity) {
            val rootView: View = activity.window.decorView.findViewById(android.R.id.content)
            Reword.reword(rootView)
        }

        private fun setAllTexts(v: View?, context: Context) {
            if (MyApplication.localizeArray != null) {
                try {
                    if (v is ViewGroup) {
                        for (i in 0 until v.childCount) {
                            val child = v.getChildAt(i)
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

        private fun setHintTag(view: View, tag: String, context: Context) {
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
                return try {
                    MyApplication.localizeArray!!.messages!!.find { it.localize_Key == key }!!
                        .getMessage()!!
                } catch (e: Exception) {
                    try {
                        val resId = con.resources.getIdentifier(key, "string", con.packageName)
                        con.resources.getString(resId)
                    } catch (e: Exception) {
                        ""
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

        fun createDialogPositive(c: Activity, message: String) {
            val builder = AlertDialog.Builder(c)
            builder
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton(c.getString(R.string.ok)){ dialog, _ -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        }

        @SuppressLint("ResourceAsColor")
        fun createDialogError(activity: Activity, errorMessage: String, titleMessage:String, isSuccess:Boolean) {
            val errorDialogBinding = ErrorDialogBinding.inflate(activity.layoutInflater)
            val builder = AlertDialog.Builder(activity)

            errorDialogBinding.tvDetails.text = errorMessage
            errorDialogBinding.tvTitle.text = titleMessage

            if (isSuccess)  errorDialogBinding.tvDetails.setTextColor(Color.parseColor("#009688"))
            errorDialogBinding.llItem.setOnClickListener {
                if (errorDialogBinding.tvDetails.visibility == View.VISIBLE)
                    errorDialogBinding.tvDetails.hide()
                else
                    errorDialogBinding.tvDetails.show()
            }

            builder.setView(errorDialogBinding.root)
                .setNegativeButton(activity.resources.getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }
            val d = builder.create()
            d.setOnShowListener {
                d.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
                d.getButton(AlertDialog.BUTTON_NEGATIVE).transformationMethod = null
                d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
            }
            d.setCancelable(false)
            d.show()
        }

        fun handleCrashes(context: Activity) {
            Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandler(context))
        }

    }



}
