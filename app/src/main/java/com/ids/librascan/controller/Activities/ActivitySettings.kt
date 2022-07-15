package com.ids.librascan.controller.Activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivitySettingsBinding
import com.ids.librascan.databinding.ToolbarBinding
import com.ids.librascan.utils.AppHelper

class ActivitySettings : AppCompatActivity() {
    lateinit var activitySettingsBinding: ActivitySettingsBinding
    lateinit var toolbarBinding: ToolbarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySettingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(activitySettingsBinding.root)
        init()
    }

    fun init() {
        AppHelper.setAllTexts(activitySettingsBinding.rootSettings, this)
        toolbarBinding = ToolbarBinding.inflate(layoutInflater)
        if (MyApplication.enableInsert){
            activitySettingsBinding.tgEnable.isChecked
        }

    }

    fun back(v: View) {
        onBackPressed()
    }

    fun setUpEnable(view: View) {
        val tb = view as ToggleButton
        if (tb.isChecked) {
            tb.isChecked = true
            MyApplication.enableInsert =true
        } else {
            MyApplication.enableInsert=false
            tb.isChecked = false
        }
    }
}