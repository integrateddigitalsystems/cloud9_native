package com.ids.cloud9.controller.activities

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.vision.text.Line
import com.ids.cloud9.Adapters.AdapterDialog
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ActivityMainBinding
import com.ids.cloud9.databinding.ActivityVisitDetailsBinding
import com.ids.cloud9.databinding.ReasonDialogBinding
import com.ids.cloud9.model.ItemSpinner
import com.ids.cloud9.model.VisitListItem
import com.ids.cloud9.utils.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ActivtyVisitDetails : Activity() , RVOnItemClickListener{

    var binding : ActivityVisitDetailsBinding ?=null
    var simp : SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    var simpTime : SimpleDateFormat = SimpleDateFormat("HH:mm")
    var simpTimeAA : SimpleDateFormat = SimpleDateFormat("hh:mm aa")
    var simpOrg : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
    var currLayout : LinearLayout ?=null
    var alertDialog : androidx.appcompat.app.AlertDialog ?=null
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
        setUpVisitDetails()

    }

    fun setUpVisitDetails(){
        var date = simpOrg.parse(MyApplication.selectedVisit!!.visitDate)
        binding!!.layoutVisit.tvVisitDate.text = simp.format(date)
        binding!!.layoutVisit.fromTime.text = simpTime.format(simpOrg.parse(MyApplication.selectedVisit!!.fromTime))
        binding!!.layoutVisit.tvToTime.text = simpTime.format(simpOrg.parse(MyApplication.selectedVisit!!.toTime))

        var millFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss").parse(MyApplication.selectedVisit!!.fromTime).time
        var millTo = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss").parse(MyApplication.selectedVisit!!.toTime).time

        var diff = millTo - millFrom

        var mins = diff / 60000


        var hours = mins/60
        var min = mins%60
        binding!!.layoutVisit.tvDuration.text =if(hours>0) {hours.toString()+"hrs"+(if(min>0)
            min.toString()+"mins"
        else
            "")}else {if(min>0)
            min.toString()+"mins"
        else
            ""}


        binding!!.layoutVisit.tvCompany.text = MyApplication.selectedVisit!!.company!!.companyName
        binding!!.layoutVisit.tvActualArrivalTime.text = simpTimeAA.format(simpOrg.parse(MyApplication.selectedVisit!!.modificationDate!!))

        if(!MyApplication.selectedVisit!!.actualCompletedTime.isNullOrEmpty())
            binding!!.layoutVisit.tvActualCompletedTime.text = simp.format(simpOrg.parse(MyApplication.selectedVisit!!.actualCompletedTime))
        else
            binding!!.layoutVisit.tvActualCompletedTime.text = ""

        if(MyApplication.selectedVisit!!.actualDuration!=null)
            binding!!.layoutVisit.tvActualDurtionTime.text = MyApplication.selectedVisit!!.actualDuration!!.toString()
        else
            binding!!.layoutVisit.tvActualDurtionTime.text = ""

        if(!MyApplication.selectedVisit!!.remark.isNullOrEmpty())
            binding!!.layoutVisit.tvRemarks.text = MyApplication.selectedVisit!!.remark!!.toEditable()
        else
            binding!!.layoutVisit.tvRemarks.text = "".toEditable()


    }


    fun setUpCompanyData(){

    }

    fun setUpDataVisit(){

        var arrSpinner : ArrayList<ItemSpinner> = arrayListOf()

        arrSpinner.add(ItemSpinner(AppConstants.SCHEDULED_REASON_ID,AppConstants.SCHEDULED_REASON,false))
        arrSpinner.add(ItemSpinner(AppConstants.COMPLETED_REASON_ID,AppConstants.COMPLETED_REASON,true))
        arrSpinner.add(ItemSpinner(AppConstants.ARRIVED_REASON_ID,AppConstants.ARRIVED_REASON,false))
        arrSpinner.add(ItemSpinner(AppConstants.PENDING_REASON_ID,AppConstants.PENDING_REASON,false))
        arrSpinner.add(ItemSpinner(AppConstants.ON_THE_WAY_REASON_ID,AppConstants.ON_THE_WAY_REASON,false))



        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
       var popupItemMultipleBinding = ReasonDialogBinding.inflate(layoutInflater)

        popupItemMultipleBinding.rvReasonStatus.layoutManager = LinearLayoutManager(this)
        popupItemMultipleBinding.rvReasonStatus.adapter = AdapterDialog(arrSpinner,this,this)

        builder.setView(popupItemMultipleBinding.root)
        alertDialog = builder.create()
        alertDialog!!.setCanceledOnTouchOutside(false)
        safeCall {
            alertDialog!!.show()
            alertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

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

            setUpCompanyData()


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

        binding!!.layoutVisit.spStatusReason.setOnClickListener {
            setUpDataVisit()
        }
    }

    override fun onItemClicked(view: View, position: Int) {

    }
}