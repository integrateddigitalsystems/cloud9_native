package com.ids.cloud9native.controller.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.ids.cloud9native.BuildConfig
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.custom.AppCompactBase
import com.ids.cloud9native.databinding.ActivityNewSplashBinding
import com.ids.cloud9native.model.*
import com.ids.cloud9native.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class ActivitySplash : AppCompactBase() {

    var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    private val permReqLauncher =
        this.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            startApp()
        }
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
                        getString(R.string.error_getting_data)
                    ) {
                        startFirebase()
                    }
                }
            }
    }

    fun getUnits() {
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java)
            .getUnits(AppConstants.PRODUCTION_LOOKUP_CODE)
            .enqueue(object : Callback<UnitList> {
                override fun onResponse(
                    call: Call<UnitList>,
                    response: Response<UnitList>
                ) {
                    safeCall {
                        if (response.body()!!.size > 0) {
                            MyApplication.units.clear()
                            MyApplication.units.addAll(response.body()!!)
                            getReasons()
                        }
                    }
                }

                override fun onFailure(call: Call<UnitList>, t: Throwable) {
                    createRetryDialog(
                        getString(R.string.error_getting_data)
                    ) {
                        startFirebase()
                    }
                }
            })
    }

    fun nextStep(response: ResponseLogin) {
        MyApplication.token = response.token!!
        wtf(response.token!!)
        MyApplication.deviceUserId = response.userId!!
        MyApplication.userItem = JWTDecoding.decoded(response.token!!)
        if (MyApplication.userItem != JWTResponse()) {
            updateDevice(MyApplication.deviceUserId)
            updateToken(MyApplication.userItem!!.applicationUserId!!.toInt())
        } else {
            MyApplication.loggedIn = false
            startApp()
        }
    }

    fun login() {
        RetrofitClientAuth.client!!.create(
            RetrofitInterface::class.java
        ).loginUser(
            MyApplication.email,
            MyApplication.password
        ).enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                MyApplication.BASE_USER_URL = response.body()!!.apiURL!!

                nextStep(response.body()!!)
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
            }

        })
    }

    fun updateDevice(userId: Int) {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            { task ->

                val imei = Settings.Secure.getString(
                    contentResolver,
                    Settings.Secure.ANDROID_ID
                )
                val updateDevice = UpdateDeviceRequest(
                    AppHelper.getAndroidVersion(),
                    MyApplication.simpleDate.format(Calendar.getInstance().time),
                    "",
                    AppHelper.getDeviceName(),
                    task.result,
                    2,
                    MyApplication.deviceId,
                    MyApplication.languageCode,
                    "",
                    true,
                    imei
                )
                if (userId != 0) {
                    updateDevice.userId = userId
                }
                Log.wtf("TAG-DEVICE_TOKEN",task.result)
                RetrofitClient.client!!.create(RetrofitInterface::class.java).updateDevice(
                    updateDevice
                ).enqueue(object : Callback<UpdateDeviceResponse> {
                    override fun onResponse(
                        call: Call<UpdateDeviceResponse>,
                        response: Response<UpdateDeviceResponse>
                    ) {
                        MyApplication.firstTime = false
                        MyApplication.deviceId = response.body()!!.id
                        // if(response.isSuccessful) {
                        if(userId ==0){
                            finalStep()
                        }else {
                            getUnits()
                        }
                        wtf("Success")

                    }

                    override fun onFailure(call: Call<UpdateDeviceResponse>, t: Throwable) {
                        createRetryDialog(
                            getString(R.string.error_getting_data)
                        ) {
                            startFirebase()
                        }
                    }
                })


            }


        )
    }

    fun updateToken(id: Int) {
        val tokenReq = TokenResource(
            MyApplication.firebaseToken,
            id
        )
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java).saveToken(tokenReq)
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

    fun startApp() {
        if (MyApplication.mobileConfig!!.forceVersion!! > BuildConfig.VERSION_NAME.toDouble()) {
            if (MyApplication.mobileConfig!!.force!!)
                createActionDialog(getString(R.string.update), getString(R.string.update_message), false){
                    goToPlayStore()
                } else
                    showForceUpdateDialog(this)

        } else {
            goNext()
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
        MyApplication.mobileConfig = mobConfig.android!!.find {
            it.version == BuildConfig.VERSION_NAME.toDouble()
        }
        if (MyApplication.mobileConfig == null) {
            createRetryDialog(
                getString(R.string.error_getting_data)
            ) {
                startFirebase()
            }
        } else {
            MyApplication.url_contains = MyApplication.mobileConfig!!.url_contains!!
            MyApplication.BASE_URL = MyApplication.mobileConfig!!.mainUrl!!
            permissionSendNotifications()
        }
    }

    private fun permissionSendNotifications() {
        val permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !AppHelper.hasPermission(
                this,
                permissions
            )
        ) {
            permReqLauncher.launch(permissions)
        } else {
            startApp()
        }
    }

    fun finalStep() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (!MyApplication.loggedIn) {
                finishAffinity()
                startActivity(Intent(this, ActivityLogin::class.java))
            } else {
                login()
            }
        }, 2000)
    }

    private fun goNext() {
        if (MyApplication.firstTime) {
            updateDevice(0)
        } else {
            finalStep()
        }
    }

    private fun goToPlayStore() {
        val appPackageName = packageName
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
            finish()
        } catch (anfe: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
            finish()
        }
    }

    private fun showForceUpdateDialog(c: Context) {
        val builder = AlertDialog.Builder(c)
        builder
            .setMessage(getString(R.string.update_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.update)) { dialog, _ ->
                goToPlayStore()
            }
            .setNegativeButton(getString(R.string.update_cancel)) { dialog, _ ->
                goNext()
            }
        val alert = builder.create()
        alert.show()
    }

    fun getReasons() {
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java)
            .getUnits(AppConstants.REASON_LOOKUP_CODE)
            .enqueue(object : Callback<UnitList> {
                override fun onResponse(
                    call: Call<UnitList>,
                    response: Response<UnitList>
                ) {
                    safeCall {
                        if (response.body()!!.size > 0) {
                            MyApplication.lookupsReason.clear()
                            MyApplication.lookupsReason.addAll(response.body()!!)
                        }
                        setUpRest()
                    }
                }

                override fun onFailure(call: Call<UnitList>, t: Throwable) {
                    createRetryDialog(
                        getString(R.string.error_getting_data)
                    ) {
                        startFirebase()
                    }
                }
            })
    }

    fun setUpRest() {
        MyApplication.loggedIn = true
        try {
            val visitId = intent.extras!!.getInt("visitId", 0)
            if (visitId != 0) {
                startActivity(
                    Intent(this, ActivtyVisitDetails::class.java)
                        .putExtra("visitId", visitId)
                        .putExtra("fromNotf", 1)
                )
                finishAffinity()
            } else {
                finishAffinity()
                startActivity(Intent(this, ActivityMain::class.java))
            }
        } catch (ex: Exception) {
            finishAffinity()
            startActivity(Intent(this, ActivityMain::class.java))
        }
    }
}