package com.ids.cloud9native.controller.Fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.adapters.AdapterProducts
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.controller.activities.ActivityAddProduct
import com.ids.cloud9native.controller.activities.ActivityRecords
import com.ids.cloud9native.controller.activities.ActivityReportDetails
import com.ids.cloud9native.controller.activities.ActivtyVisitDetails
import com.ids.cloud9native.controller.adapters.AdapterContracts
import com.ids.cloud9native.controller.adapters.AdapterDialog
import com.ids.cloud9native.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9native.controller.adapters.StickyAdapter
import com.ids.cloud9native.databinding.LayoutContractsBinding
import com.ids.cloud9native.databinding.LayoutProductsBinding
import com.ids.cloud9native.databinding.ReasonDialogBinding


import com.ids.cloud9native.model.*
import com.ids.cloud9native.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FragmentContract : Fragment(), RVOnItemClickListener {

    var binding: LayoutContractsBinding? = null
    var arrayContract: ArrayList<Visit> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutContractsBinding.inflate(inflater, container, false)
        return binding!!.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        val fromNotf = (requireActivity() as ActivtyVisitDetails).fromNotf
        if (fromNotf == 1) {
            (requireActivity() as ActivtyVisitDetails).fromNotf++
            getVisits()
        } else {
            filterDate(MyApplication.allVisits)
        }
    }


    fun getVisits() {
        binding!!.llLoading.show()
        RetrofitClientSpecificAuth.client?.create(RetrofitInterface::class.java)
            ?.getVisits(
                -1,
                MyApplication.userItem!!.applicationUserId!!.toInt()
            )?.enqueue(object : Callback<VisitList> {
                override fun onResponse(call: Call<VisitList>, response: Response<VisitList>) {
                    filterDate(response.body()!!)
                }

                override fun onFailure(call: Call<VisitList>, throwable: Throwable) {
                    binding!!.llLoading.hide()
                    filterDate(arrayListOf())
                }
            })
    }

    fun filterDate(arrayList: ArrayList<Visit>) {
        arrayContract.clear()
        arrayContract.addAll(arrayList.filter {
            MyApplication.selectedVisit!!.contractId==it.contractId  && it.contractId !=null && MyApplication.selectedVisit!!.id!=it.id
        }
        )
        setUpData()
    }

    fun setUpData() {
        if (arrayContract.size >0){
            for (it in arrayContract) {
                it.appearDuration = AppHelper.durationToString(it.duration!!.toFloat())
                var millFrom  = Calendar.getInstance().timeInMillis
                var millTo = Calendar.getInstance().timeInMillis
                safeCall {
                    millFrom = SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ssss",
                        Locale.ENGLISH
                    ).parse(it.fromTime!!)!!.time
                }
                safeCall{
                    millTo = SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ssss",
                        Locale.ENGLISH
                    ).parse(it.toTime!!)!!.time
                }
                var reasonStatus: String
                var reasonBg:Int
                var reasonText:Int
                if(it.reasonId == AppHelper.getReasonID(AppConstants.REASON_ARRIVED)){
                    reasonBg = R.drawable.arrived_bg
                    reasonText = R.color.arrived_text
                    reasonStatus =AppHelper.getReasonName(AppConstants.REASON_ARRIVED)
                }else if(it.reasonId == AppHelper.getReasonID(AppConstants.REASON_COMPLETED)){
                    reasonBg = R.drawable.completed_bg
                    reasonText = R.color.comp_text
                    reasonStatus = AppHelper.getReasonName(AppConstants.REASON_COMPLETED)
                }else if(it.reasonId == AppHelper.getReasonID(AppConstants.REASON_SCHEDULED)){
                    reasonBg = R.drawable.scheduled_bg
                    reasonText = R.color.scheduled_text
                    reasonStatus = AppHelper.getReasonName(AppConstants.REASON_SCHEDULED)
                }else if(it.reasonId == AppHelper.getReasonID(AppConstants.REASON_ON_THE_WAY)){
                    reasonBg = R.drawable.on_the_way_bg
                    reasonText = R.color.otw_text
                    reasonStatus =AppHelper.getReasonName(AppConstants.REASON_ON_THE_WAY)
                }else if(it.reasonId == AppHelper.getReasonID(AppConstants.REASON_PENDING)){
                    reasonBg = R.drawable.pending_bg
                    reasonText = R.color.pending_text
                    reasonStatus = AppHelper.getReasonName(AppConstants.REASON_PENDING)
                }else{
                    reasonBg = R.drawable.on_the_way_bg
                    reasonText = R.color.otw_text
                    reasonStatus =AppHelper.getReasonName(AppConstants.REASON_ON_THE_WAY)
                }
                val show = ShowStickyItem(
                    it.isHeader,
                    it.title,
                    if(it.company!=null && !it.company!!.companyName.isNullOrEmpty()) it.company!!.companyName else "",
                    SimpleDateFormat(
                        "hh:mm aa",
                        Locale.ENGLISH
                    ).format(Date(millFrom)) + "-\n" + SimpleDateFormat(
                        "hh:mm aa",
                        Locale.ENGLISH
                    ).format(Date(millTo)) + " (" + it.appearDuration + ")",
                    reasonStatus,
                    "",
                    "",
                    it.reasonId,
                    false,
                    reasonText,
                    reasonBg
                )
                it.showData = show
            }

            val adapter = AdapterContracts(requireActivity(), arrayContract, this)
            binding!!.rvContracts.layoutManager = LinearLayoutManager(requireContext())
            binding!!.rvContracts.adapter = adapter
            binding!!.rvContracts.isNestedScrollingEnabled = false

            binding!!.llLoading.hide()
            binding!!.rvContracts.show()
            binding!!.tvNoData.hide()
        }
        else {
            binding!!.rvContracts.hide()
            binding!!.tvNoData.show()
            binding!!.llLoading.hide()
        }

    }

    override fun onResume() {
        super.onResume()
        MyApplication.selectedVisit = MyApplication.selectedVisitCurrent
    }

    override fun onItemClicked(view: View, position: Int) {
        MyApplication.selectedVisit = arrayContract.get(position)
        Log.wtf("JSON_DATA",Gson().toJson(MyApplication.selectedVisit))
        startActivity(Intent(requireActivity(), ActivtyVisitDetails::class.java))
    }
}