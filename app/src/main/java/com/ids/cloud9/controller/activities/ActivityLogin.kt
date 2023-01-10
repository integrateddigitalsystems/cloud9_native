package com.ids.cloud9.controller.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.databinding.ActivityLoginBinding
import com.ids.cloud9.model.JWTDecoding
import com.ids.cloud9.model.RequestLogin
import com.ids.cloud9.model.ResponseLogin
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityLogin : Activity() {


    var binding : ActivityLoginBinding?=null
    var showPass = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)



        binding!!.etEmail.text =Editable.Factory.getInstance().newEditable("info@mte.com.ae")
        binding!!.etPassword.text =Editable.Factory.getInstance().newEditable("P@ssw0rd!@#")
        listeners()
    }

    fun listeners(){

        binding!!.btLogin.setOnClickListener {
            if(!binding!!.etEmail.text.toString().isNullOrEmpty() && !binding!!.etPassword.text.toString().isNullOrEmpty())
                login(binding!!.etEmail.text.toString(),binding!!.etPassword.text.toString())
        }

        binding!!.btShowPassword.setOnClickListener {
                if(showPass){
                    binding!!.btShowPassword.setImageResource(R.drawable.lock)
                    binding!!.etPassword.inputType= InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    showPass = false
                }else{
                    binding!!.btShowPassword.setImageResource(R.drawable.lock_open)
                    binding!!.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPass = true
                }
        }
    }

   fun login(email : String , password : String ){

       binding!!.btLogin.hide()
       binding!!.loadingLogin.show()
       var newReq = RequestLogin(email , password , true )
       RetrofitClient.client?.create(RetrofitInterface::class.java)
           ?.logIn(
               newReq
           )?.enqueue(object : Callback<ResponseLogin> {
               override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {

                   try {
                       MyApplication.token = response.body()!!.token
                       MyApplication.userItem = JWTDecoding.decoded(MyApplication.token!!)
                       binding!!.loadingLogin.hide()

                       startActivity(Intent(this@ActivityLogin,ActivityMain::class.java))
                   }catch (ex:Exception){
                       binding!!.btLogin.show()
                   }

               }
               override fun onFailure(call: Call<ResponseLogin>, throwable: Throwable) {
                   binding!!.btLogin.show()
               }
           })
   }

}