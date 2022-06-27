package com.ids.librascan.utils


import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.provider.Settings.Global.getString
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
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


        fun overrideFonts(context: Context, v: View) {

            val typeface: Typeface

            try {
                if (v is ViewGroup) {
                    for (i in 0 until v.childCount) {
                        val child = v.getChildAt(i)
                        overrideFonts(context, child)
                    }
                } else if (v is TextView) {

                    when (MyApplication) {

                        //MyApplication.ENGLISH -> typeface = MyApplication.gilroy

                        // else -> typeface = MyApplication.droidRegular
                    }

                    //  v.typeface = typeface
                }
            } catch (e: Exception) {
                // Crashlytics.logException(e)
                e.printStackTrace()
            }

        }

        fun setLocal(context: Context) {

            if (MyApplication.languageCode == AppConstants.LANG_ENGLISH) {
                LocaleUtils.setLocale(Locale("en"))
            } else if (MyApplication.languageCode == AppConstants.LANG_ARABIC) {
                LocaleUtils.setLocale(Locale("ar"))
            }

        }
      ///
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


        fun setHintTag(view: View, tag: String, context: Context) {
            var edit = view as EditText
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
                    "fonts/Inter-Medium.ttf"
                )
            else
           return Typeface.createFromAsset(context.applicationContext.assets, "fonts/Inter-Medium.ttf")

        }
        fun getTypeFaceBold(context: Context): Typeface {
            return if (Locale.getDefault().language == "ar")
                Typeface.createFromAsset(
                    context.applicationContext.assets,
                    "fonts/Inter-Bold.ttf"
                )

            else
              return  Typeface.createFromAsset(
                    context.applicationContext.assets,
                    "fonts/Inter-Bold.ttf"
                )

        }

        fun closeKeyboard(context: Activity) {

            val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                context.currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }

        fun createDialog(c: Activity, message: String) {
            val builder = AlertDialog.Builder(c)
            builder
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(c.getString(android.R.string.ok))
                { dialog, _ ->
                    dialog.cancel()
                }
                .setNegativeButton(c.getString(android.R.string.cancel))
                { dialog, _ ->
                    dialog.cancel()
                }

            val alert = builder.create()
            alert.show()
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


    }


}
