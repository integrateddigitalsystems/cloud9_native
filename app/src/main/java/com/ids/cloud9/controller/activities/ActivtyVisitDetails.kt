package com.ids.cloud9.controller.activities

import android.app.Activity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.google.android.gms.vision.text.Line
import com.ids.cloud9.R
import com.ids.cloud9.databinding.ActivityMainBinding
import com.ids.cloud9.databinding.ActivityVisitDetailsBinding
import com.ids.cloud9.utils.AppHelper
import com.ids.cloud9.utils.hide
import com.ids.cloud9.utils.show

class ActivtyVisitDetails : Activity(){

    var binding : ActivityVisitDetailsBinding ?=null
    var currLayout : LinearLayout ?=null
    var currLayPost = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
        listeners()
    }

    fun init(){

        AppHelper.setTextColor(this,binding!!.tvVisit,R.color.medium_blue)
        binding!!.llBorderVisit.hide()
        binding!!.llSelectedVisitBorder.show()
        currLayout = binding!!.llBorderVisit

    }

    fun restartLayouts(){
        AppHelper.setTextColor(this,binding!!.tvCompany, R.color.gray_border_tab)
        AppHelper.setTextColor(this,binding!!.tvVisit, R.color.gray_border_tab)
        AppHelper.setTextColor(this,binding!!.tvMedia, R.color.gray_border_tab)
        AppHelper.setTextColor(this,binding!!.tvReccom, R.color.gray_border_tab)
        AppHelper.setTextColor(this,binding!!.tvProducts, R.color.gray_border_tab)
        AppHelper.setTextColor(this,binding!!.tvSignature, R.color.gray_border_tab)

        binding!!.llBorderCompany.show()
        binding!!.llSelectedCompanyBorder.hide()
        binding!!.llBorderVisit.show()
        binding!!.llSelectedVisitBorder.hide()
        binding!!.llMediaBorder.show()
        binding!!.llSelectedMediaBorder.hide()
        binding!!.llBorderRecc.show()
        binding!!.llSelectedReccBorder.hide()
        binding!!.llProductsBorder.show()
        binding!!.llSelectedProductsBorder.hide()
        binding!!.llBorderSignature.show()
        binding!!.llSelectedSignatureBorder.hide()
    }

    fun listeners(){
        binding!!.llCompany.setOnClickListener {
            restartLayouts()
            AppHelper.setTextColor(this,binding!!.tvCompany,R.color.medium_blue)
            binding!!.llBorderCompany.hide()
            binding!!.llSelectedCompanyBorder.show()

            var  leftOut =  AnimationUtils.loadAnimation(this, R.anim.left_out)
            binding!!.visitLayout.startAnimation(leftOut)
            var  rightIn =  AnimationUtils.loadAnimation(this, R.anim.left_in)
            binding!!.visitLayout.startAnimation(leftOut)
            binding!!.companyLayout.startAnimation(rightIn)
            binding!!.companyLayout.show()
            binding!!.visitLayout.hide()



        }

        binding!!.llVisit.setOnClickListener {
            restartLayouts()
            AppHelper.setTextColor(this,binding!!.tvVisit,R.color.medium_blue)
            binding!!.llBorderVisit.hide()
            binding!!.llSelectedVisitBorder.show()

            var  leftOut =  AnimationUtils.loadAnimation(this, R.anim.right_in)
            binding!!.visitLayout.startAnimation(leftOut)
            var  rightIn =  AnimationUtils.loadAnimation(this, R.anim.right_out)
            binding!!.visitLayout.startAnimation(leftOut)
            binding!!.companyLayout.startAnimation(rightIn)
            binding!!.companyLayout.hide()
            binding!!.visitLayout.show()
        }

        binding!!.llMedia.setOnClickListener {
            restartLayouts()
            AppHelper.setTextColor(this,binding!!.tvMedia,R.color.medium_blue)
            binding!!.llMediaBorder.hide()
            binding!!.llSelectedMediaBorder.show()
        }

        binding!!.llProducts.setOnClickListener {
            restartLayouts()
            AppHelper.setTextColor(this,binding!!.tvProducts,R.color.medium_blue)
            binding!!.llProductsBorder.hide()
            binding!!.llSelectedProductsBorder.show()
        }

        binding!!.llRecommendations.setOnClickListener {
            restartLayouts()
            AppHelper.setTextColor(this,binding!!.tvReccom,R.color.medium_blue)
            binding!!.llBorderRecc.hide()
            binding!!.llSelectedReccBorder.show()
        }

        binding!!.llSignature.setOnClickListener {
            restartLayouts()
            AppHelper.setTextColor(this,binding!!.tvSignature,R.color.medium_blue)
            binding!!.llBorderSignature.hide()
            binding!!.llSelectedSignatureBorder.show()
        }

        binding!!.btBack.setOnClickListener {
            super.onBackPressed()
        }
    }
}