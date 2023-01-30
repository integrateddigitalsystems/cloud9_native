package com.ids.cloud9.controller.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ids.cloud9.databinding.ActivityAddProductBinding
import com.ids.cloud9.databinding.ActivityAddReccomendBinding
import com.ids.cloud9.databinding.ActivityVisitDetailsBinding

class ActivityAddReccomendations : AppCompatActivity() {

    var binding : ActivityAddReccomendBinding ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReccomendBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


    }
}