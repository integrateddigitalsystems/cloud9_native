package com.ids.librascan.controller.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.ids.librascan.R
import com.ids.librascan.controller.MyApplication

class ActivitySplash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


       goLogin()
    }

    fun goLogin(){

        if (MyApplication.isLogin){
            Handler().postDelayed({
                val i = Intent(this@ActivitySplash, ActivityMain::class.java)
                startActivity(i)
                finish()
            }, 500)
        }
        else{
            Handler().postDelayed({
                val i = Intent(this@ActivitySplash, ActivityLogin::class.java)
                startActivity(i)
                finish()
            }, 500)
        }

    }
}