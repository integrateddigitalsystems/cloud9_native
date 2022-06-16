package com.ids.librascan.utils


import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.ids.librascan.controller.MyApplication
import utils.AppConstants
import utils.LocaleUtils
import utils.textRemote
import java.util.*



class AppHelper {
    companion object {
        var fragmentAvailable: Int? = null
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


        /*fun AddFragment(
            fragmentManager: FragmentManager,
            selectedFragment: Int,
            myFragment: Fragment,
            myTag: String
        ) {
            fragmentAvailable = selectedFragment
            fragmentManager.beginTransaction()
                .add(com.ids.logindesign.R.id.viewPager, myFragment, myTag)
                .addToBackStack(null)
                .commit()
        }*/


       /* fun ReplaceFragment(
            fragmentManager: FragmentManager,
            selectedFragment: Int,
            myFragment: Fragment
        ) {
            fragmentAvailable = selectedFragment
            fragmentManager.beginTransaction()
                .replace(R.id.viewPager, myFragment)
                .commit()

        }*/

    }
}
