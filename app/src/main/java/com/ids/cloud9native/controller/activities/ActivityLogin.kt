package com.ids.cloud9native.controller.activities


import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.custom.AppCompactBase
import com.ids.cloud9native.databinding.ActivityLoginBinding
import com.ids.cloud9native.model.*
import com.ids.cloud9native.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class ActivityLogin : AppCompactBase() {
    var binding : ActivityLoginBinding?=null
    var showPass = true
    var selectedField = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
       // binding!!.etEmail.text =Editable.Factory.getInstance().newEditable("mobile@crm.ids.com.lb")
       // binding!!.etPassword.text =Editable.Factory.getInstance().newEditable("P@ssw0rd1")
        listeners()
    }
    fun listeners(){
        binding!!.etEmail.setOnFocusChangeListener { view, b ->
            if(selectedField !=1){
                selectedField = 1
                binding!!.etEmail.setBackgroundResource(R.drawable.rounded_primary_black)
                binding!!.etPassword.setBackgroundResource(R.drawable.rounded_primary_border)
            }
        }
        /*binding!!.btTestUser1.setOnClickListener {
            binding!!.etEmail.text = Editable.Factory.getInstance().newEditable("mobile@crm.ids.com.lb")
            binding!!.etPassword.text =Editable.Factory.getInstance().newEditable("P@ssw0rd1")
        }
        binding!!.btTestUser2.setOnClickListener {
            binding!!.etEmail.text =Editable.Factory.getInstance().newEditable("mobile@mydomain.com")
            binding!!.etPassword.text =Editable.Factory.getInstance().newEditable("P@ssw0rd!@#")
        }*/
        binding!!.etPassword.setOnFocusChangeListener { view, b ->
            if(selectedField !=2){
                selectedField = 2
                binding!!.etPassword.setBackgroundResource(R.drawable.rounded_primary_black)
                binding!!.etEmail.setBackgroundResource(R.drawable.rounded_primary_border)
            }
        }
        binding!!.btLogin.setOnClickListener {
            if(!binding!!.etEmail.text.toString().trim().isEmpty() && !binding!!.etPassword.text.toString().trim().isEmpty()) {
                if(!binding!!.etEmail.text.toString().isEmailValid()){
                    createDialog(getString(R.string.email_invalid))
                }else {
                    login(binding!!.etEmail.text.toString().trim(), binding!!.etPassword.text.toString().trim())
                }
            }else{
                createDialog(getString(R.string.fill_details))
            }
        }
        binding!!.btShowPassword.setOnClickListener {
                if(showPass){
                    binding!!.btShowPassword.setImageResource(R.drawable.lock_open)
                    binding!!.etPassword.inputType= InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    showPass = false
                }else{
                    binding!!.btShowPassword.setImageResource(R.drawable.lock)
                    binding!!.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())
                    showPass = true
                }
        }
    }
   fun login(email : String , password : String ){
       binding!!.btLogin.hide()
       binding!!.loadingLogin.show()
       wtf(MyApplication.BASE_URL)
       RetrofitClient.client?.create(RetrofitInterface::class.java)
           ?.loginUser(
              email,
               password
           )?.enqueue(object : Callback<ResponseLogin> {
               override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                   try {
                       if(response.body()!!.success!!) {
                           MyApplication.token = response.body()!!.token!!
                           MyApplication.BASE_USER_URL = response.body()!!.apiURL!!
                           wtf(MyApplication.token)
                           MyApplication.email = email
                           MyApplication.password = password
                           MyApplication.userItem = JWTDecoding.decoded(MyApplication.token)
                           Log.wtf("JAD JWT", Gson().toJson(MyApplication.userItem))
                           updateToken(MyApplication.userItem!!.applicationUserId!!.toInt())
                           MyApplication.deviceUserId = response.body()!!.userId!!
                           updateDevice(MyApplication.deviceUserId)
                       }else{
                           createDialog(response.body()!!.message!!)
                           binding!!.btLogin.show()
                       }
                   }catch (ex:Exception){
                       binding!!.btLogin.show()
                       wtf(ex.toString())
                   }
               }
               override fun onFailure(call: Call<ResponseLogin>, throwable: Throwable) {
                   binding!!.btLogin.show()
                   wtf(throwable.toString())
               }
           })
   }
    fun updateToken(id:Int){
        val tokenReq  = TokenResource(
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
    fun getReasons(){
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java).getUnits(AppConstants.REASON_LOOKUP_CODE)
            .enqueue(object : Callback<UnitList> {
                override fun onResponse(
                    call: Call<UnitList>,
                    response: Response<UnitList>
                ) {
                    safeCall {
                        if(response.body()!!.size >0) {
                            MyApplication.lookupsReason.clear()
                            MyApplication.lookupsReason.addAll(response.body()!!)
                        }
                    }
                }
                override fun onFailure(call: Call<UnitList>, t: Throwable) {
                }
            })
    }

    fun updateDevice(userId : Int){

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
                Log.wtf("TAG-DEVICE_TOKEN",task.result)
                if(userId!=0){
                    updateDevice.userId = userId
                }
                RetrofitClient.client!!.create(RetrofitInterface::class.java).updateDevice(
                    updateDevice
                ).enqueue(object : Callback<UpdateDeviceResponse> {
                    override fun onResponse(
                        call: Call<UpdateDeviceResponse>,
                        response: Response<UpdateDeviceResponse>
                    ) {
                        if(response.isSuccessful){
                            wtf("Success")
                            finalStepLogin()
                        }else {
                            createRetryDialog(
                                getString(R.string.error_getting_data)
                            ) {
                                updateDevice(userId)
                            }
                        }

                    }
                    override fun onFailure(call: Call<UpdateDeviceResponse>, t: Throwable) {
                        createRetryDialog(
                            getString(R.string.error_getting_data)
                        ) {
                            updateDevice(userId)
                        }
                    }
                })


            }



        )
    }

    fun finalStepLogin(){
        binding!!.loadingLogin.hide()
        binding!!.btLogin.show()
        getUnits()
        getReasons()
        MyApplication.loggedIn = true
        startActivity(Intent(this@ActivityLogin,ActivityMain::class.java))
    }
    fun getUnits(){
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java).getUnits(AppConstants.PRODUCTION_LOOKUP_CODE)
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

}