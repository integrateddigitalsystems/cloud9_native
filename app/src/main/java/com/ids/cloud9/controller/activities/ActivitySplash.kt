package com.ids.cloud9.controller.activities

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.ids.cloud9.BuildConfig
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.databinding.ActivitySplashBinding
import com.ids.cloud9.databinding.ActivitySplshBindingImpl
import com.ids.cloud9.model.LocMessages
import com.ids.cloud9.model.MobileConfigTypes
import com.ids.cloud9.utils.AppConstants
import com.ids.cloud9.utils.AppHelper

class ActivitySplash : Activity() {

    var mFirebaseRemoteConfig : FirebaseRemoteConfig?=null
    private lateinit var binding: ActivitySplshBindingImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splsh)
        startFirebase()

    }

    fun startFirebase(){

        mFirebaseRemoteConfig = Firebase.remoteConfig!!
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        mFirebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig!!.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    setUpFirebase()
                }else{
                    AppHelper.createDialogAgain(this, getString(R.string.error_getting_data)){

                    }
                }


            }


    }

    fun setUpFirebase(){
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

        if(MyApplication.mobileConfig ==null){
            AppHelper.createDialogAgain(this, AppHelper.getRemoteString("error_getting_data",this)){
                startFirebase()
            }
        }else{
            Handler(Looper.getMainLooper()).postDelayed({

            }, 2000)
        }
    }

}