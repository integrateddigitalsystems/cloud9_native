package com.ids.cloud9.controller.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.vision.text.Line
import com.google.api.Distribution.BucketOptions.Linear
import com.ids.cloud9.Adapters.AdapterDialog
import com.ids.cloud9.Adapters.AdapterProducts
import com.ids.cloud9.Adapters.AdapterReccomendations
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ActivityMainBinding
import com.ids.cloud9.databinding.ActivityVisitDetailsBinding
import com.ids.cloud9.databinding.ReasonDialogBinding
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ActivtyVisitDetails : Activity() , RVOnItemClickListener{

    var binding : ActivityVisitDetailsBinding ?=null
    var simp : SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    var simpTime : SimpleDateFormat = SimpleDateFormat("HH:mm")
    var arrayProd : ArrayList<ProductsItem> = arrayListOf()
    var arrayReccomend : ArrayList<ActivitiesListItem> = arrayListOf()
    var ctProd =0
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
        currLayout = binding!!.visitLayout
        setUpVisitDetails()
        binding!!.tvVisitTitle.text = MyApplication.selectedVisit!!.title


    }

    fun setDataReccomend(){
        binding!!.layoutReccomend.rvReccomendations.layoutManager = LinearLayoutManager(this)
        binding!!.layoutReccomend.rvReccomendations.adapter = AdapterReccomendations(arrayReccomend,this,this)
        binding!!.llLoading.hide()
    }
    fun getReccomendations(){
        binding!!.llLoading.show()
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getReccomendations(
            MyApplication.userItem!!.applicationUserId!!.toInt()
        ).enqueue(object :Callback<ActivitiesList>{
            override fun onResponse(
                call: Call<ActivitiesList>,
                response: Response<ActivitiesList>
            ) {
                arrayReccomend.clear()
                arrayReccomend.addAll(response.body()!!
                    .filter {
                        it.entity.equals(MyApplication.selectedVisit!!.title)
                    })
                setDataReccomend()

            }

            override fun onFailure(call: Call<ActivitiesList>, t: Throwable) {
                binding!!.llLoading.hide()
            }

        })
    }

    fun setUpProductsPage(){
        binding!!.layoutProdcution.rvProducts.layoutManager = LinearLayoutManager(this)
        binding!!.layoutProdcution.rvProducts.adapter = AdapterProducts(arrayProd,this,this)
        binding!!.llLoading.hide()
    }

    fun getReports(position: Int){

        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getReports(
            arrayProd.get(position).product.categoryId
        )?.enqueue(object : Callback<ArrayList<Report>> {
            override fun onResponse(call: Call<ArrayList<Report>>, response: Response<ArrayList<Report>>) {
                if(response.body()!!.size > 0){
                    arrayProd.get(position).reports.clear()
                    arrayProd.get(position).reports.addAll(response.body()!!)

                }

                ctProd++
                if(ctProd == arrayProd.size){
                    setUpProductsPage()
                }

            }
            override fun onFailure(call: Call<ArrayList<Report>>, throwable: Throwable) {

                binding!!.llLoading.hide()
            }
        })

    }
    fun setUpProducts(){
        if(arrayProd.size > 0) {
            for (i in arrayProd.indices) {
                arrayProd.get(i).reports = arrayListOf()
                getReports(i)
            }
        }else{
            binding!!.llLoading.hide()
        }
    }

    fun getProducts(){
        binding!!.llLoading.show()
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getProducts(
            MyApplication.selectedVisit!!.id!!
        )?.enqueue(object : Callback<Products> {
            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                arrayProd.clear()
                arrayProd.addAll(response.body()!!)
                setUpProducts()


            }
            override fun onFailure(call: Call<Products>, throwable: Throwable) {

                binding!!.llLoading.hide()
            }
        })
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
        binding!!.layoutVisit.tvDuration.text =if(hours>0) {hours.toString()+" hrs "+(if(min>0)
            min.toString()+" mins"
        else
            "")}else {if(min>0)
            min.toString()+" mins"
        else
            ""}


        binding!!.layoutVisit.tvStatusReason.text = if(MyApplication.selectedVisit!!.reasonId == AppConstants.ON_THE_WAY_REASON_ID){
            AppConstants.ON_THE_WAY_REASON
        }else if(MyApplication.selectedVisit!!.reasonId == AppConstants.ARRIVED_REASON_ID){
            AppConstants.ARRIVED_REASON
        }else if(MyApplication.selectedVisit!!.reasonId == AppConstants.PENDING_REASON_ID){
            AppConstants.PENDING_REASON
        }else if(MyApplication.selectedVisit!!.reasonId == AppConstants.COMPLETED_REASON_ID){
            AppConstants.COMPLETED_REASON
        }else{
            AppConstants.SCHEDULED_REASON
        }

        binding!!.layoutVisit.tvCompany.text = MyApplication.selectedVisit!!.company!!.companyName
        if(!MyApplication.selectedVisit!!.actualArrivalTime.isNullOrEmpty())
            binding!!.layoutVisit.tvActualArrivalTime.text = simpTimeAA.format(simpOrg.parse(MyApplication.selectedVisit!!.actualArrivalTime!!))
        else
            binding!!.layoutVisit.tvActualArrivalTime.text = ""

        if(!MyApplication.selectedVisit!!.actualCompletedTime.isNullOrEmpty())
            binding!!.layoutVisit.tvActualCompletedTime.text = simpTimeAA.format(simpOrg.parse(MyApplication.selectedVisit!!.actualCompletedTime!!))
        else
            binding!!.layoutVisit.tvActualCompletedTime.text = ""

        if(MyApplication.selectedVisit!!.actualDuration!=null) {
           var dur = MyApplication.selectedVisit!!.actualDuration!!.toLong()


            var mins = dur / 60000


            var hours = mins/60
            var min = mins%60
            binding!!.layoutVisit.tvActualDurtionTime.text =if(hours>0) {hours.toString()+" hrs "+(if(min>0)
                min.toString()+" mins"
            else
                "")}else {if(min>0)
                min.toString()+" mins"
            else
                ""}


        }else
            binding!!.layoutVisit.tvActualDurtionTime.text = ""

        if(!MyApplication.selectedVisit!!.remark.isNullOrEmpty())
            binding!!.layoutVisit.tvRemarks.text = MyApplication.selectedVisit!!.remark!!.toEditable()
        else
            binding!!.layoutVisit.tvRemarks.text = "".toEditable()


    }


    fun setUpCompanyData(){

        binding!!.layoutCompany.tvCompanyName.setTextLang(MyApplication.selectedVisit!!.company!!.companyName,MyApplication.selectedVisit!!.company!!.companyNameAr)
        binding!!.layoutCompany.tvEmail.setCheckText(MyApplication.selectedVisit!!.company!!.email)
        binding!!.layoutCompany.tvWebite.setCheckText(MyApplication.selectedVisit!!.company!!.website)
        binding!!.layoutCompany.tvPhone.setCheckText(MyApplication.selectedVisit!!.company!!.phoneNumber)
        binding!!.layoutCompany.tvFax.setCheckText(MyApplication.selectedVisit!!.company!!.fax)
        binding!!.layoutCompany.tvContactName.setTextLang(MyApplication.selectedVisit!!.contact!!.firstName + " "+MyApplication.selectedVisit!!.contact!!.lastName,MyApplication.selectedVisit!!.contact!!.firstNameAr + " "+MyApplication.selectedVisit!!.contact!!.lastNameAr)
        binding!!.layoutCompany.tvContactNumber.setCheckText(MyApplication.selectedVisit!!.contact!!.personalPhoneNumber)
        if(MyApplication.selectedVisit!!.companyAddress!=null)
            binding!!.layoutCompany.tvAddress.setCheckText(MyApplication.selectedVisit!!.companyAddress!!.address)
        else
            binding!!.layoutCompany.tvAddress.text = ""
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
            if(currLayPost!=1) {
                restartLayouts()
                AppHelper.setTextColor(this, binding!!.tvCompany, R.color.medium_blue)
                binding!!.llBorderCompany.hide()
                binding!!.llSelectedCompanyBorder.show()
                if(currLayPost<1) {
                    var leftOut = AnimationUtils.loadAnimation(this, R.anim.left_out)
                    var rightIn = AnimationUtils.loadAnimation(this, R.anim.left_in)
                    currLayout!!.startAnimation(leftOut)
                    binding!!.companyLayout.startAnimation(rightIn)
                    binding!!.companyLayout.show()
                    currLayout!!.hide()

                    setUpCompanyData()
                }else{
                    var leftOut = AnimationUtils.loadAnimation(this, R.anim.right_out)
                    var rightIn = AnimationUtils.loadAnimation(this, R.anim.right_in)
                    currLayout!!.startAnimation(rightIn)
                    binding!!.companyLayout.startAnimation(leftOut)
                    binding!!.companyLayout.show()
                    currLayout!!.hide()

                    setUpCompanyData()
                }

                currLayout = binding!!.companyLayout
                currLayPost = 1
            }


        }

        binding!!.llVisit.setOnClickListener {
            if(currLayPost!=0) {

                restartLayouts()
                AppHelper.setTextColor(this, binding!!.tvVisit, R.color.medium_blue)
                binding!!.llBorderVisit.hide()
                binding!!.llSelectedVisitBorder.show()

                var leftOut = AnimationUtils.loadAnimation(this, R.anim.right_in)
                var rightIn = AnimationUtils.loadAnimation(this, R.anim.right_out)
                binding!!.visitLayout.startAnimation(leftOut)
                binding!!.companyLayout.startAnimation(rightIn)
                binding!!.companyLayout.hide()
                binding!!.visitLayout.show()

                currLayout = binding!!.visitLayout
                currLayPost = 0
            }
        }

        binding!!.layoutCompany.llGetDirection.setOnClickListener {
            if(MyApplication.selectedVisit!!.companyAddress!=null && !MyApplication.selectedVisit!!.companyAddress!!.lat.isNullOrEmpty() && !MyApplication.selectedVisit!!.companyAddress!!.long.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(
                    "geo:0,0?q=" + MyApplication.selectedVisit!!.companyAddress!!.lat + "," + MyApplication.selectedVisit!!.companyAddress!!.long + "(" + MyApplication.selectedVisit!!.companyAddress!!.name + ")"
                )
                startActivity(intent)
            }
        }

        binding!!.llMedia.setOnClickListener {
            restartLayouts()
            AppHelper.setTextColor(this,binding!!.tvMedia,R.color.medium_blue)
            binding!!.llMediaBorder.hide()
            binding!!.llSelectedMediaBorder.show()
            if(currLayPost!=5) {
                if(currLayPost<5) {
                    var leftOut = AnimationUtils.loadAnimation(this, R.anim.left_out)
                    var rightIn = AnimationUtils.loadAnimation(this, R.anim.left_in)
                    currLayout!!.startAnimation(leftOut)
                    binding!!.mediaLayout.startAnimation(rightIn)
                    binding!!.mediaLayout.show()
                    currLayout!!.hide()

                    setUpCompanyData()
                }else{
                    var leftOut = AnimationUtils.loadAnimation(this, R.anim.right_in)
                    var rightIn = AnimationUtils.loadAnimation(this, R.anim.right_out)
                    currLayout!!.startAnimation(rightIn)
                    binding!!.mediaLayout.startAnimation(leftOut)
                    binding!!.mediaLayout.show()
                    currLayout!!.hide()

                    setUpCompanyData()
                }

                currLayout = binding!!.mediaLayout
                currLayPost = 5
            }

        }


        binding!!.layoutSignature.redoClient.setOnClickListener {
            binding!!.layoutSignature.drawClient.redo()
        }

        binding!!.layoutSignature.undoClient.setOnClickListener {
            binding!!.layoutSignature.drawClient.undo()
        }

        binding!!.layoutSignature.redoEmployee.setOnClickListener {
            binding!!.layoutSignature.drawEmployee.redo()
        }

        binding!!.layoutSignature.undoEmployee.setOnClickListener {
            binding!!.layoutSignature.drawEmployee.undo()
        }

        binding!!.layoutSignature.btClearClient.setOnClickListener {
            binding!!.layoutSignature.drawClient.clearCanvas()
        }

        binding!!.layoutSignature.btClearEmployee.setOnClickListener {
            binding!!.layoutSignature.drawEmployee.clearCanvas()
        }


        binding!!.llProducts.setOnClickListener {
            restartLayouts()
            AppHelper.setTextColor(this,binding!!.tvProducts,R.color.medium_blue)
            binding!!.llProductsBorder.hide()
            binding!!.llSelectedProductsBorder.show()
            if(currLayPost!=2) {
                if(currLayPost<2) {
                    var leftOut = AnimationUtils.loadAnimation(this, R.anim.left_out)
                    var rightIn = AnimationUtils.loadAnimation(this, R.anim.left_in)
                    currLayout!!.startAnimation(leftOut)
                    binding!!.productionLayout.startAnimation(rightIn)
                    binding!!.productionLayout.show()
                    currLayout!!.hide()

                    getProducts()
                }else{
                    var leftOut = AnimationUtils.loadAnimation(this, R.anim.right_in)
                    var rightIn = AnimationUtils.loadAnimation(this, R.anim.right_out)
                    currLayout!!.startAnimation(rightIn)
                    binding!!.productionLayout.startAnimation(leftOut)
                    binding!!.productionLayout.show()
                    currLayout!!.hide()

                    getProducts()
                }

                currLayout = binding!!.productionLayout
                currLayPost = 2
            }
        }

        binding!!.llRecommendations.setOnClickListener {
            restartLayouts()
            AppHelper.setTextColor(this,binding!!.tvReccom,R.color.medium_blue)
            binding!!.llBorderRecc.hide()
            binding!!.llSelectedReccBorder.show()
            if(currLayPost!=3) {
                if(currLayPost<3) {
                    var leftOut = AnimationUtils.loadAnimation(this, R.anim.left_out)
                    var rightIn = AnimationUtils.loadAnimation(this, R.anim.left_in)
                    currLayout!!.startAnimation(leftOut)
                    binding!!.reccomLayout.startAnimation(rightIn)
                    binding!!.reccomLayout.show()
                    currLayout!!.hide()

                    getReccomendations()
                }else{
                    var leftOut = AnimationUtils.loadAnimation(this, R.anim.right_in)
                    var rightIn = AnimationUtils.loadAnimation(this, R.anim.right_out)
                    currLayout!!.startAnimation(rightIn)
                    binding!!.reccomLayout.startAnimation(leftOut)
                    binding!!.reccomLayout.show()
                    currLayout!!.hide()

                   getReccomendations()
                }

                currLayout = binding!!.reccomLayout
                currLayPost = 3
            }
        }

        binding!!.llSignature.setOnClickListener {
            restartLayouts()
            AppHelper.setTextColor(this,binding!!.tvSignature,R.color.medium_blue)
            binding!!.llBorderSignature.hide()
            binding!!.llSelectedSignatureBorder.show()

            if(currLayPost!=4) {
                if(currLayPost<4) {
                    var leftOut = AnimationUtils.loadAnimation(this, R.anim.left_out)
                    var rightIn = AnimationUtils.loadAnimation(this, R.anim.left_in)
                    currLayout!!.startAnimation(leftOut)
                    binding!!.signLayout.startAnimation(rightIn)
                    binding!!.signLayout.show()
                    currLayout!!.hide()

                    setUpCompanyData()
                }else{
                    var leftOut = AnimationUtils.loadAnimation(this, R.anim.right_in)
                    var rightIn = AnimationUtils.loadAnimation(this, R.anim.right_out)
                    currLayout!!.startAnimation(rightIn)
                    binding!!.signLayout.startAnimation(leftOut)
                    binding!!.signLayout.show()
                    currLayout!!.hide()

                    setUpCompanyData()
                }

                currLayout = binding!!.signLayout
                currLayPost = 4
            }
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