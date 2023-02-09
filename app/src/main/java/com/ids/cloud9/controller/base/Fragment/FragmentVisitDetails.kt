package com.ids.cloud9.controller.base.Fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ids.cloud9.controller.adapters.AdapterDialog
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.LayoutReccomendationsBinding
import com.ids.cloud9.databinding.LayoutVisitBinding
import com.ids.cloud9.databinding.ReasonDialogBinding
import com.ids.cloud9.model.ItemSpinner
import com.ids.cloud9.model.ResponseMessage
import com.ids.cloud9.model.VisitListItem
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar

class FragmentVisitDetails : Fragment() , RVOnItemClickListener{

    var alertDialog: androidx.appcompat.app.AlertDialog? = null
    var editVisit : VisitListItem ?=null
    var adapter : AdapterDialog?=null
    var simp : SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    var simpTime : SimpleDateFormat = SimpleDateFormat("HH:mm")
    var simpTimeAA : SimpleDateFormat = SimpleDateFormat("hh:mm aa")
    var simpOrg : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
    var simpOrgss : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssss")

    var arrSpinner: ArrayList<ItemSpinner> = arrayListOf()

    var binding : LayoutVisitBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = LayoutVisitBinding.inflate(inflater, container, false)
        return binding!!.root
    }


    fun setUpDataVisit() {





        val builder = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
        var popupItemMultipleBinding = ReasonDialogBinding.inflate(layoutInflater)

        popupItemMultipleBinding.rvReasonStatus.layoutManager = LinearLayoutManager(requireActivity())
        adapter =  AdapterDialog(arrSpinner, requireActivity(), this, true)
        popupItemMultipleBinding.rvReasonStatus.adapter = adapter


        builder.setView(popupItemMultipleBinding.root)
        alertDialog = builder.create()
        alertDialog!!.setCanceledOnTouchOutside(true)
        safeCall {
            alertDialog!!.show()
            alertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

    }


    fun setUpVisitDetails(){
        var date = simpOrg.parse(MyApplication.selectedVisit!!.visitDate)
        binding!!.tvVisitDate.text = simp.format(date)
        binding!!.fromTime.text = simpTime.format(simpOrg.parse(MyApplication.selectedVisit!!.fromTime))
        binding!!.tvToTime.text = simpTime.format(simpOrg.parse(MyApplication.selectedVisit!!.toTime))

        var millFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss").parse(MyApplication.selectedVisit!!.fromTime).time
        var millTo = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss").parse(MyApplication.selectedVisit!!.toTime).time

        var diff = millTo - millFrom

        var mins = diff / 60000


        var hours = mins/60
        var min = mins%60
        binding!!.tvDuration.text =if(hours>0) {hours.toString()+" hrs "+(if(min>0)
            min.toString()+" mins"
        else
            "")}else {if(min>0)
            min.toString()+" mins"
        else
            ""}


        binding!!.tvStatusReason.text = if(MyApplication.selectedVisit!!.reasonId == AppConstants.ON_THE_WAY_REASON_ID){
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

        binding!!.tvCompany.text = MyApplication.selectedVisit!!.company!!.companyName
        if(!MyApplication.selectedVisit!!.actualArrivalTime.isNullOrEmpty())
            binding!!.tvActualArrivalTime.text = simpTimeAA.format(simpOrg.parse(
                MyApplication.selectedVisit!!.actualArrivalTime!!))
        else
            binding!!.tvActualArrivalTime.text = ""

        if(!MyApplication.selectedVisit!!.actualCompletedTime.isNullOrEmpty())
            binding!!.tvActualCompletedTime.text = simpTimeAA.format(simpOrg.parse(
                MyApplication.selectedVisit!!.actualCompletedTime!!))
        else
            binding!!.tvActualCompletedTime.text = ""

        if(MyApplication.selectedVisit!!.actualDuration!=null) {
            var dur = MyApplication.selectedVisit!!.actualDuration!!.toLong()


            var mins = dur / 60000


            var hours = mins/60
            var min = mins%60
            binding!!.tvActualDurtionTime.text =if(hours>0) {hours.toString()+" hrs "+(if(min>0)
                min.toString()+" mins"
            else
                "")}else {if(min>0)
                min.toString()+" mins"
            else
                ""}


        }else
            binding!!.tvActualDurtionTime.text = ""

        if(!MyApplication.selectedVisit!!.remark.isNullOrEmpty())
            binding!!.etRemark.text = MyApplication.selectedVisit!!.remark!!.toEditable()
        else
            binding!!.etRemark.text = "".toEditable()


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun listeners(){
        binding!!.spStatusReason.setOnClickListener {
            if(MyApplication.selectedVisit!!.reasonId!=AppConstants.COMPLETED_REASON_ID)
                setUpDataVisit()
        }

        binding!!.btSave.setOnClickListener {
            editVisit!!.remark = binding!!.etRemark.text.toString()
            updateVisit()
        }
    }
    fun updateVisit(){
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java)
            .updateVisit(MyApplication.selectedVisit!!)
            .enqueue(object : Callback<ResponseMessage>{
                override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                   AppHelper.createDialogPositive(requireActivity(),response.body()!!.message!!)
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    Log.wtf("TAGTAG","test")
                }

            })
    }
    fun init(){
        
        setUpVisitDetails()
        listeners()
        editVisit = MyApplication.selectedVisit
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initialData()



    }

    fun initialData(){
        arrSpinner.add(
            ItemSpinner(
                AppConstants.SCHEDULED_REASON_ID,
                AppConstants.SCHEDULED_REASON,
                false
            )
        )
        arrSpinner.add(
            ItemSpinner(
                AppConstants.COMPLETED_REASON_ID,
                AppConstants.COMPLETED_REASON,
                false
            )
        )
        arrSpinner.add(
            ItemSpinner(
                AppConstants.ARRIVED_REASON_ID,
                AppConstants.ARRIVED_REASON,
                false
            )
        )
        arrSpinner.add(
            ItemSpinner(
                AppConstants.PENDING_REASON_ID,
                AppConstants.PENDING_REASON,
                false
            )
        )
        arrSpinner.add(
            ItemSpinner(
                AppConstants.ON_THE_WAY_REASON_ID,
                AppConstants.ON_THE_WAY_REASON,
                false
            )
        )

        arrSpinner.find {
            it.id == MyApplication.selectedVisit!!.reasonId
        }!!.selected = true


       if(MyApplication.selectedVisit!!.reasonId == AppConstants.ARRIVED_REASON_ID){
           for(item in arrSpinner)
               if(item.id!=AppConstants.ARRIVED_REASON_ID && item.id!=AppConstants.COMPLETED_REASON_ID)
                   item.selectable = false
       }else if(MyApplication.selectedVisit!!.reasonId == AppConstants.COMPLETED_REASON_ID){

       }else if(MyApplication.selectedVisit!!.reasonId == AppConstants.PENDING_REASON_ID){
           for(item in arrSpinner)
               if(item.id!=AppConstants.PENDING_REASON_ID && item.id!=AppConstants.ON_THE_WAY_REASON_ID)
                   item.selectable = false
       }else if(MyApplication.selectedVisit!!.reasonId == AppConstants.ON_THE_WAY_REASON_ID){
           for(item in arrSpinner)
               if(item.id==AppConstants.SCHEDULED_REASON_ID || item.id==AppConstants.COMPLETED_REASON_ID)
                   item.selectable = false
       }


    }

    override fun onItemClicked(view: View, position: Int) {
        if(arrSpinner.get(position).selectable!!) {
            for (item in arrSpinner)
                item.selected = false
            arrSpinner.get(position).selected = true
            alertDialog!!.dismiss()
            adapter!!.notifyDataSetChanged()

            if (arrSpinner.get(position).id == AppConstants.COMPLETED_REASON_ID) {
                var cal = Calendar.getInstance()
                binding!!.tvActualCompletedTime.text = simpTimeAA.format(cal.time)
                editVisit!!.actualCompletedTime = simpOrgss.format(cal.time)
            } else {
                binding!!.tvActualCompletedTime.text = ""
            }

            binding!!.tvStatusReason.text = arrSpinner.get(position).name
            editVisit!!.reasonId = arrSpinner.get(position).id
        }
    }
}