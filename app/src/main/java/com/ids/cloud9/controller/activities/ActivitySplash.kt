package com.ids.cloud9.controller.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.ids.cloud9.BuildConfig
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.databinding.ActivityNewSplashBinding
import com.ids.cloud9.databinding.ActivitySplashBinding
import com.ids.cloud9.databinding.ActivitySplshBindingImpl
import com.ids.cloud9.databinding.LayoutReccomendationsBinding
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivitySplash : Activity() {

    var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    private lateinit var binding: ActivityNewSplashBinding
    var token : String ?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewSplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        startFirebase()
    }
    fun startFirebase() {
        mFirebaseRemoteConfig = Firebase.remoteConfig!!
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        mFirebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig!!.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    setUpFirebase()
                } else {
                    AppHelper.createDialogAgain(this, getString(R.string.error_getting_data)) {
                    }
                }
    }
    }
    fun getUnits(){
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getUnits(AppConstants.PRODUCTION_LOOKUP_CODE)
            .enqueue(object : Callback<UnitList> {
                override fun onResponse(
                    call: Call<UnitList>,
                    response: Response<UnitList>
                ) {
                    safeCall {
                        if(response.body()!!.size >0) {
                            MyApplication.units.clear()
                            MyApplication.units.addAll(response.body()!!)
                        }
                    }
                }
                override fun onFailure(call: Call<UnitList>, t: Throwable) {
                }
            })
    }
    fun nextStep(token: String){
        MyApplication.token =token
        Log.wtf("TOKEN",token)
        MyApplication.userItem = JWTDecoding.decoded(token)
        if(MyApplication.userItem != JWTResponse()) {
            updateToken(MyApplication.userItem!!.applicationUserId!!.toInt())
            getUnits()
            MyApplication.loggedIn = true
            try {
                var visitId = intent.extras!!.getInt("visitId")
                if (visitId!=null && visitId!=0) {
                    startActivity(
                        Intent(this, ActivtyVisitDetails::class.java)
                            .putExtra("visitId", visitId)
                            .putExtra("fromNotf",true)
                    )
                    finishAffinity()
                } else {
                    finishAffinity()
                    startActivity(Intent(this, ActivityMain::class.java))
                }
            }catch (ex:Exception){
                finishAffinity()
                startActivity(Intent(this, ActivityMain::class.java))
            }
        }else{
            MyApplication.loggedIn = false
            startApp()
        }
    }
    fun login(){
        var req = RequestLogin(
            MyApplication.email,
            MyApplication.password,
            true
        )
        RetrofitClientAuth.client!!.create(
            RetrofitInterface::class.java
        ).logIn(
            req
        ).enqueue(object : Callback<ResponseLogin>{
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                nextStep(response.body()!!.token!!)
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
            }

        })
    }
    fun updateToken(id:Int){
        var tokenReq  = TokenResource(
            MyApplication.firebaseToken,
            id
        )
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).saveToken(tokenReq)
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    wtf("Success")
                }
                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    wtf("Failure")
                }
            })
    }
    fun startApp(){
        if (MyApplication.mobileConfig!!.forceVersion > BuildConfig.VERSION_NAME.toDouble()) {
        } else {

            Handler(Looper.getMainLooper()).postDelayed({
                if(!MyApplication.loggedIn) {
                    finishAffinity()
                    startActivity(Intent(this, ActivityLogin::class.java))
                }else{
                    login()
                }
            }, 2000)
        }
    }
    fun setUpFirebase() {
        var mobConfig = Gson().fromJson(
            mFirebaseRemoteConfig!!.getString(AppConstants.FIREBASE_CONFIG),
            MobileConfigTypes::class.java
        )
        var locMessages = Gson().fromJson(
            mFirebaseRemoteConfig!!.getString(AppConstants.LOCALISED_MESSAGES),
            LocMessages::class.java
        )
        MyApplication.locMessages.addAll(locMessages.values)
        MyApplication.mobileConfig = mobConfig.android.find {
            it.version.toDouble() == BuildConfig.VERSION_NAME.toDouble()
        }
        if (MyApplication.mobileConfig == null) {
            AppHelper.createDialogAgain(
                this,
                getString(R.string.error_getting_data)
            ) {
                startFirebase()
            }
        } else {
            startApp()
        }
    }
    private fun showDialogUpdate(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        val textView: TextView
        val inflater = activity.layoutInflater
        val textEntryView = inflater.inflate(R.layout.item_dialog, null)
        textView = textEntryView.findViewById(R.id.dialogMsg)
        textView.gravity = Gravity.CENTER
        textView.text =  getString(R.string.update)
        builder.setTitle(AppHelper.getRemoteString("update_title",this))
        builder.setView(textEntryView)
            .setPositiveButton(AppHelper.getRemoteString("update_button",this)) { dialog, _ ->
                dialog.dismiss()
                val appPackageName = activity.packageName
                try {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                    activity.finish()
                } catch (anfe: android.content.ActivityNotFoundException) {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                    activity.finish()
                }
            }
            .setNegativeButton(AppHelper.getRemoteString("update_cancel",this)) { dialog, _ ->
                dialog.dismiss()
            }
        val d = builder.create()
        d.setOnShowListener {
            d.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            d.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            d.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).transformationMethod = null
            d.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
            d.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            d.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            d.getButton(android.app.AlertDialog.BUTTON_POSITIVE).transformationMethod = null
            d.getButton(android.app.AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        }
        d.setCancelable(false)
        d.show()
    }
    private fun showDialogForceUpdate(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        val textView: TextView
        val inflater = activity.layoutInflater
        val textEntryView = inflater.inflate(R.layout.item_dialog, null)
        textView = textEntryView.findViewById(R.id.dialogMsg)
        textView.gravity = Gravity.START
        textView.text = AppHelper.getRemoteString("update_message",this)
        builder.setTitle(AppHelper.getRemoteString("update_title",this))
        builder.setView(textEntryView)
            .setNegativeButton(AppHelper.getRemoteString("update_button",this)) { dialog, _ ->
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
            d.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this@ActivitySplash, R.color.colorPrimary))
            d.getButton(AlertDialog.BUTTON_NEGATIVE).transformationMethod = null
            d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
        }
        d.setCancelable(false)
        d.show()
    }
}