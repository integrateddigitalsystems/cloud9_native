package com.ids.librascan.controller.Activities

import Base.ActivityCompactBase
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.ids.librascan.R
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivitySplashBinding

class ActivitySplash : ActivityCompactBase() {
    lateinit var activitySplashBinding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         activitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(activitySplashBinding.root)


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