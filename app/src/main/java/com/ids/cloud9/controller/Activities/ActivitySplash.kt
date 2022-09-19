package com.ids.cloud9

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import com.ids.cloud9.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay


class ActivitySplash : Activity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            // code
        }, 2000)

    }


}
