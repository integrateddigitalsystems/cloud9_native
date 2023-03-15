package com.ids.cloud9.controller.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.ids.cloud9.BuildConfig
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.databinding.ActivityNewSplashBinding
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivitySplash : Activity() {

    var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    private lateinit var binding: ActivityNewSplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startFirebase()
    }
    fun startFirebase() {
        mFirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        mFirebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig!!.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    setUpFirebase()
                } else {
                    createRetryDialog(
                        getString(R.string.error_getting_data)) {
                        startFirebase()
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
        wtf(token)
        MyApplication.userItem = JWTDecoding.decoded(token)
        if(MyApplication.userItem != JWTResponse()) {
            updateToken(MyApplication.userItem!!.applicationUserId!!.toInt())
            getUnits()
            MyApplication.loggedIn = true
            try {
                val visitId = intent.extras!!.getInt("visitId",0)
                if (visitId!=0) {
                    startActivity(
                        Intent(this, ActivtyVisitDetails::class.java)
                            .putExtra("visitId", visitId)
                            .putExtra("fromNotf",1)
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
        val req = RequestLogin(
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
        val tokenReq  = TokenResource(
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
        if (MyApplication.mobileConfig!!.forceVersion >= BuildConfig.VERSION_NAME.toDouble()) {
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
        val mobConfig = Gson().fromJson(
            mFirebaseRemoteConfig!!.getString(AppConstants.FIREBASE_CONFIG),
            MobileConfigTypes::class.java
        )
        val locMessages = Gson().fromJson(
            mFirebaseRemoteConfig!!.getString(AppConstants.LOCALISED_MESSAGES),
            LocMessages::class.java
        )
        MyApplication.locMessages.addAll(locMessages.values)
        MyApplication.mobileConfig = mobConfig.android.find {
            it.version == BuildConfig.VERSION_NAME.toDouble()
        }
        if (MyApplication.mobileConfig == null) {
            createRetryDialog(
                getString(R.string.error_getting_data)
            ) {
                startFirebase()
            }
        } else {
            startApp()
        }
    }
}