package com.ids.cloud9.controller.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ids.cloud9.databinding.ActivityAddProductBinding
import com.ids.cloud9.databinding.ActivityVisitDetailsBinding

class ActivityAddProduct : AppCompatActivity() {

    var binding : ActivityAddProductBinding ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


    }
}