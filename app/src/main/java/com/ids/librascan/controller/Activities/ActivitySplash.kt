package com.ids.librascan.controller.Activities

import Base.ActivityCompactBase
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.ids.librascan.R
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivitySplashBinding
import com.ids.librascan.databinding.ItemDialogBinding
import com.ids.librascan.databinding.PopupBarcodeBinding
import com.ids.librascan.model.FirebaseBaseUrlsArray
import com.ids.librascan.model.FirebaseLocalizeArray
import com.ids.librascan.model.FirebaseUrlItems
import com.ids.librascan.utils.AppHelper
import utils.AppConstants

class ActivitySplash : ActivityCompactBase() {
    lateinit var activitySplashBinding: ActivitySplashBinding
    var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         activitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
         setContentView(activitySplashBinding.root)

        getFirebasePrefs()
    }
    private fun getFirebasePrefs() {
        auth = FirebaseAuth.getInstance()
        mFirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        mFirebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig!!.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                try {
                    MyApplication.BASE_URLS = Gson().fromJson(
                        mFirebaseRemoteConfig!!.getString(AppConstants.FIREBASE_URL_LIST),
                        FirebaseBaseUrlsArray::class.java
                    )

                } catch (e: Exception) {
                    Log.wtf("exception_firebase", e.toString())
                }
                try {
                    MyApplication.localizeArray = Gson().fromJson(
                        mFirebaseRemoteConfig!!.getString(AppConstants.FIREBASE_LOCALIZE),
                        FirebaseLocalizeArray::class.java
                    )
                    AppHelper.setAllTexts(activitySplashBinding.rootLayout, this)
                    checkUpdate()
                } catch (e: Exception) {
                    Log.wtf("exception_firebase", e.toString())
                }

            } else {
                Log.wtf("exception_firebase", "e")
            }

        }
            .addOnFailureListener {
                Log.wtf("exception_firebase", it.toString())
            }

    }
    private fun checkUpdate(){
        var currConfig : FirebaseUrlItems?=null
        currConfig = MyApplication.BASE_URLS!!.android.find {
            it.versionName == com.ids.librascan.BuildConfig.VERSION_NAME
        }
        MyApplication.BASE_URL =currConfig!!.baseUrl!!

        if(currConfig.lastVersion!! > com.ids.librascan.BuildConfig.VERSION_NAME) {

            if (currConfig.forceUpdate!!) {
                showDialogForceUpdate(this)
            } else {
                showDialogUpdate(this)
            }
        }else{
            goLogin()
        }
    }


    private fun showDialogForceUpdate(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        val itemDialogBinding = ItemDialogBinding.inflate(layoutInflater)
        itemDialogBinding.dialogMsg.gravity = Gravity.CENTER
        itemDialogBinding.dialogMsg.text = AppHelper.getRemoteString("update_message",this)
        builder.setTitle(AppHelper.getRemoteString("update_title",this))
        builder.setCancelable(false)

        builder.setView(itemDialogBinding.root)
            .setNegativeButton(AppHelper.getRemoteString("update",this)) { dialog, _ ->
                dialog.dismiss()
                val appPackageName = activity.packageName
                try {
                    activity.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName)
                        )
                    )
                    activity.finish()

                } catch (anfe: ActivityNotFoundException) {

                    activity.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)
                        )
                    )
                    activity.finish()
                }
            }
        val d = builder.create()
        d.setOnShowListener {
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.gray_bg))
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.gray_bg))
            d.getButton(AlertDialog.BUTTON_NEGATIVE).transformationMethod = null
            d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
        }
        d.setCancelable(false)
        d.show()
    }

    private fun showDialogUpdate(activity: Activity) {
        val itemDialogBinding = ItemDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(activity)
        itemDialogBinding.dialogMsg.gravity = Gravity.CENTER
        itemDialogBinding.dialogMsg.text = AppHelper.getRemoteString("update_message",this)
        builder.setTitle(AppHelper.getRemoteString("update_title",this))

        builder.setView(itemDialogBinding.root)
            .setNegativeButton(AppHelper.getRemoteString("update",this)) { dialog, _ ->
                dialog.dismiss()
                val appPackageName = activity.packageName
                try {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                    activity.finish()

                } catch (anfe:android.content.ActivityNotFoundException) {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                    activity.finish()

                }
            }
            .setPositiveButton(AppHelper.getRemoteString("cancel",this)) { dialog, _ ->
                dialog.dismiss()
                goLogin()
            }

        val d = builder.create()
        d.setOnShowListener {
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.gray_bg))
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.gray_bg))
            d.getButton(AlertDialog.BUTTON_NEGATIVE).transformationMethod = null
            d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false

            d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.gray_bg))
            d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.gray_bg))
            d.getButton(AlertDialog.BUTTON_POSITIVE).transformationMethod = null
            d.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        }
        d.setCancelable(false)
        d.show()
    }

    private fun goLogin(){
        if (MyApplication.isLogin){
            Handler(Looper.getMainLooper()).postDelayed({
                val i = Intent(this@ActivitySplash, ActivityMain::class.java)
                startActivity(i)
                finish()
            }, 500)
        }
        else{
            Handler(Looper.getMainLooper()).postDelayed({
                val i = Intent(this@ActivitySplash, ActivityLogin::class.java)
                startActivity(i)
                finish()
            }, 500)
        }

    }
}