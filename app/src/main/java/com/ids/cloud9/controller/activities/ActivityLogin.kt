package com.ids.cloud9.controller.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.annotation.RequiresApi
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.custom.AppCompactBase
import com.ids.cloud9.databinding.ActivityLoginBinding
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityLogin : AppCompactBase() {
    var binding : ActivityLoginBinding?=null
    var showPass = true
    var selectedField = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.etEmail.text =Editable.Factory.getInstance().newEditable("info@mte.com.ae")
        binding!!.etPassword.text =Editable.Factory.getInstance().newEditable("P@ssw0rd!@#")
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
        binding!!.etPassword.setOnFocusChangeListener { view, b ->
            if(selectedField !=2){
                selectedField = 2
                binding!!.etPassword.setBackgroundResource(R.drawable.rounded_primary_black)
                binding!!.etEmail.setBackgroundResource(R.drawable.rounded_primary_border)
            }
        }
        binding!!.btLogin.setOnClickListener {
            if(!binding!!.etEmail.text.toString().isNullOrEmpty() && !binding!!.etPassword.text.toString().isNullOrEmpty()) {
                if(!binding!!.etEmail.text.toString().isEmailValid()){
                    AppHelper.createDialogPositive(this,getString(R.string.email_invalid))
                }else {
                    login(binding!!.etEmail.text.toString(), binding!!.etPassword.text.toString())
                }
            }else{
                AppHelper.createDialogPositive(this,getString(R.string.fill_details))
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
                    binding!!.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPass = true
                }
        }
    }
   fun login(email : String , password : String ){
       binding!!.btLogin.hide()
       binding!!.loadingLogin.show()
       Log.wtf("TAG_URL",MyApplication.BASE_URL)
       var newReq = RequestLogin(email , password , true )
       RetrofitClient.client?.create(RetrofitInterface::class.java)
           ?.logIn(
               newReq
           )?.enqueue(object : Callback<ResponseLogin> {
               override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                   try {
                       MyApplication.token = response.body()!!.token!!
                       Log.wtf("TOKEN",MyApplication.token)
                       MyApplication.email = email
                       MyApplication.password = password
                       MyApplication.userItem = JWTDecoding.decoded(MyApplication.token!!)
                       updateToken(MyApplication.userItem!!.applicationUserId!!.toInt())
                       binding!!.loadingLogin.hide()
                       binding!!.btLogin.show()
                       getUnits()
                       MyApplication.loggedIn = true
                       startActivity(Intent(this@ActivityLogin,ActivityMain::class.java))
                   }catch (ex:Exception){
                       binding!!.btLogin.show()
                       Log.wtf("TAG_URL",ex.toString())
                   }
               }
               override fun onFailure(call: Call<ResponseLogin>, throwable: Throwable) {
                   binding!!.btLogin.show()
                   Log.wtf("TAG_URL",throwable.toString())
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

}