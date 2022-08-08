package com.ids.librascan.controller.Activities

import Base.ActivityCompactBase
import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivitySettingsBinding
import com.ids.librascan.databinding.ToolbarBinding
import com.ids.librascan.utils.AppHelper

class ActivitySettings : ActivityCompactBase() {
    lateinit var activitySettingsBinding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySettingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(activitySettingsBinding.root)
        init()
    }

    fun init() {
      //  AppHelper.setAllTexts(activitySettingsBinding.rootSettings, this)
        if (MyApplication.enableInsert) activitySettingsBinding.tgEnable.isChecked = true
        if (MyApplication.enableNewLine) activitySettingsBinding.tgNewLine.isChecked = true
        activitySettingsBinding.tvEnable.typeface = AppHelper.getTypeFace(this)
        activitySettingsBinding.tvNewLine.typeface = AppHelper.getTypeFace(this)
        activitySettingsBinding.tvToolBarTitle.typeface = AppHelper.getTypeFace(this)
    }

    fun back(v: View) {
        onBackPressed()
    }

    fun setUpEnable(view: View) {
        if (activitySettingsBinding.tgEnable.isChecked) {
            activitySettingsBinding.tgEnable.isChecked = true
            MyApplication.enableInsert=true
        } else {
            MyApplication.enableInsert=false
        }
    }

    fun setUpNewLine(view: View) {
        if (activitySettingsBinding.tgNewLine.isChecked) {
            activitySettingsBinding.tgNewLine.isChecked = true
            MyApplication.enableNewLine = true
        } else {
            MyApplication.enableNewLine = false
        }
    }
}