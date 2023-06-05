package com.ids.cloud9.controller.base.Fragment

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.MyApplication.Companion.isFirstSettings
import com.ids.cloud9.controller.MyApplication.Companion.isFirstGps
import com.ids.cloud9.controller.MyApplication.Companion.toSettings
import com.ids.cloud9.controller.MyApplication.Companion.toSettingsGps
import com.ids.cloud9.controller.activities.ActivtyVisitDetails
import com.ids.cloud9.controller.adapters.AdapterDialog
import com.ids.cloud9.controller.adapters.AdapterSpinner
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.custom.AppCompactBase
import com.ids.cloud9.databinding.LayoutVisitBinding
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class FragmentVisitDetails : Fragment(), RVOnItemClickListener {
    var alertDialog: androidx.appcompat.app.AlertDialog? = null
    var edtitVisit: Visit? = null
    var adapter: AdapterDialog? = null
    var changed: Boolean = false
    val BLOCKED = -1
    val BLOCKED_OR_NEVER_ASKED = 2
    val GRANTED = 0
    val DENIED = 1
    var adapterSpin: AdapterSpinner? = null
    var LOCATION_REFRESH_DISTANCE = 5
    private val NOTIFICATION_ID = 12345678
    var mPermissionResult: ActivityResultLauncher<Array<String>>? = null
    var LOCATION_REFRESH_TIME = 5000
    var isScheduled=false
    var simp: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    var simpTime: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    var simpTimeAA: SimpleDateFormat = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
    var simpOrg: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    var simpOrgss: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss", Locale.ENGLISH)
    var arrSpinner: ArrayList<ItemSpinner> = arrayListOf()
    var binding: LayoutVisitBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutVisitBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    fun openChooser(){
        mPermissionResult!!.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }
    fun setUpVisitDetails() {
        var date = ""
        safeCall {
            date = simp.format(simpOrg.parse(edtitVisit!!.visitDate!!)!!)
        }
        binding!!.tvVisitDate.text = date
        try {
            binding!!.fromTime.text =
                simpTime.format(simpOrg.parse(edtitVisit!!.fromTime!!)!!)
            binding!!.tvToTime.text =
                simpTime.format(simpOrg.parse(edtitVisit!!.toTime!!)!!)
            val millFrom =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss", Locale.ENGLISH).parse(edtitVisit!!.fromTime!!)!!.time
            val millTo =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss", Locale.ENGLISH).parse(edtitVisit!!.toTime!!)!!.time
            val diff = millTo - millFrom
            val mins = diff / 60000
            binding!!.tvDuration.text = edtitVisit!!.appearDuration

            if (edtitVisit!!.company != null) {
                binding!!.tvCompany.text = edtitVisit!!.company!!.companyName
            } else {
                binding!!.tvCompany.text = ""
            }
            if (!edtitVisit!!.actualArrivalTime.isNullOrEmpty())
                binding!!.tvActualArrivalTime.text = simpTimeAA.format(
                    simpOrg.parse(
                        edtitVisit!!.actualArrivalTime!!
                    )!!
                )
            else
                binding!!.tvActualArrivalTime.text = ""

            if (!edtitVisit!!.actualCompletedTime.isNullOrEmpty())
                binding!!.tvActualCompletedTime.text = simpTimeAA.format(
                    simpOrg.parse(
                        edtitVisit!!.actualCompletedTime!!
                    )!!
                )
            else
                binding!!.tvActualCompletedTime.text = ""

            if (edtitVisit!!.actualDuration != null) {
                val mins =  edtitVisit!!.actualDuration!!.toInt()  / 60000
                val hours = mins / 60
                val min = mins % 60

                val requiredFormat =   if (hours > 0) {
                    hours.toString() + "hrs" + min + "mins"
                } else {
                    "$min mins"
                }
                binding!!.tvActualDurtionTime.text = requiredFormat
            } else
                binding!!.tvActualDurtionTime.text = ""
            if (!edtitVisit!!.remark.isNullOrEmpty())
                binding!!.etRemark.text = edtitVisit!!.remark!!.toEditable()
            else
                binding!!.etRemark.text = "".toEditable()

            (requireActivity() as ActivtyVisitDetails).binding!!.llTool.tvTitleTool.text = edtitVisit!!.title!!


        } catch (ex: Exception) {
            wtf("The message")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun listeners() {
        binding!!.btSave.setOnClickListener {
            edtitVisit!!.remark = binding!!.etRemark.text.toString()
            updateVisit()
        }
    }

    fun setUpStatusReasonSpinner() {
        adapterSpin =
            AdapterSpinner(requireContext(), R.layout.spinner_text_item, arrSpinner)
        binding!!.spStatusReason.adapter = adapterSpin
        adapterSpin!!.setDropDownViewResource(R.layout.spinner_new)
        binding!!.spStatusReason.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                        val pos = arrSpinner.indexOf(
                            arrSpinner.find {
                                it.selected!!
                            }
                        )
                        if (arrSpinner.get(position).selectable!!) {
                            binding!!.tvStatreason.text = arrSpinner.get(position).name
                            for (itm in arrSpinner)
                                itm.selected = false
                            arrSpinner.get(position).selected = true
                            edtitVisit!!.reasonId = arrSpinner.get(position).id
                            if (arrSpinner.get(position).id == AppHelper.getReasonID(AppConstants.REASON_ARRIVED)) {
                                if (isScheduled){
                                    toSettings =true
                                    toSettingsGps =true
                                }
                               else{
                                   toSettings =isFirstSettings
                                   toSettingsGps =isFirstGps
                                }
                                (requireActivity() as ActivtyVisitDetails).changeState(false)
                                if(edtitVisit!!.actualArrivalTime.isNullOrEmpty() || changed) {
                                    val cal = Calendar.getInstance()
                                    binding!!.tvActualArrivalTime.text = simpTimeAA.format(cal.time)
                                    edtitVisit!!.actualArrivalTime = simpOrgss.format(cal.time)
                                    binding!!.tvActualCompletedTime.text = ""
                                    binding!!.tvActualDurtionTime.text = ""
                                }
                            } else if (arrSpinner.get(position).id == AppHelper.getReasonID(AppConstants.REASON_COMPLETED)) {
                                changed = true
                                val cal = Calendar.getInstance()
                                isFirstGps=true
                                isFirstSettings=true
                                binding!!.tvActualCompletedTime.text = simpTimeAA.format(cal.time)
                                edtitVisit!!.actualCompletedTime = simpOrgss.format(cal.time)
                                val millFrom =
                                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss", Locale.ENGLISH).parse(edtitVisit!!.actualArrivalTime!!)!!.time
                                val millTo =
                                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss", Locale.ENGLISH).parse(edtitVisit!!.actualCompletedTime!!)!!.time
                                val diff = millTo - millFrom
                                val mins = diff / 60000
                                val hours = mins / 60
                                val min = mins % 60
                                binding!!.tvActualDurtionTime.text =
                                    if (hours > 0) {
                                        hours.toString() + "hrs" + min + "mins"
                                    } else {
                                        min.toString() + " mins"
                                    }
                             edtitVisit!!.actualDuration=diff.toDouble()

                            }else if (arrSpinner.get(position).id == AppHelper.getReasonID(AppConstants.REASON_ON_THE_WAY)){
                                if (isScheduled){
                                    toSettings =true
                                    toSettingsGps =true
                                }
                                else{
                                    toSettings =isFirstSettings
                                    toSettingsGps =isFirstGps
                                }
                                (requireActivity() as ActivtyVisitDetails).changeState(true)
                            }
                            else {
                                isFirstGps=true
                                isFirstSettings=true
                                edtitVisit!!.actualArrivalTime = ""
                                edtitVisit!!.actualCompletedTime = ""
                                edtitVisit!!.actualDuration = 0.0
                                binding!!.tvActualArrivalTime.text = ""
                                binding!!.tvActualCompletedTime.text = ""
                            }
                            adapterSpin!!.notifyDataSetChanged()
                        } else {
                            binding!!.spStatusReason.setSelection(pos)
                        }

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        val pos = arrSpinner.indexOf(
            arrSpinner.find {
                it.id == edtitVisit!!.reasonId
            }
        )
        binding!!.spStatusReason.setSelection(pos)
    }

    fun changeLocation(id:Int){
        var firstLocation : Location ?=null
        var gps_enabled = false
        var network_enabled = false
        var mLocationManager =
            requireActivity().getSystemService(Service.LOCATION_SERVICE) as LocationManager
        try {
            gps_enabled = mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled =
                mLocationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }



        if ((ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    )&& (gps_enabled || network_enabled)) {



            if (gps_enabled && firstLocation == null)
                firstLocation = mLocationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (network_enabled && firstLocation == null ) {
                firstLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }


            var visitLocationRequest = VisitLocationRequest(
                MyApplication.userItem!!.applicationUserId!!.toInt(),
                0,
                true,
                firstLocation!!.latitude,
                firstLocation.longitude,
               id


            )
            wtf(Gson().toJson(visitLocationRequest))
            RetrofitClientSpecificAuth.client!!.create(
                RetrofitInterface::class.java
            ).createVisitLocation(
                visitLocationRequest
            ).enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    try {
                        wtf(response.body()!!.message!!)
                    } catch (ex: Exception) {
                        wtf(getString(R.string.error_getting_data))
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    wtf(getString(R.string.failure))
                }

            })

        }else{
            if(gps_enabled && network_enabled)
                openChooser()
            else{
                requireActivity().createActionDialog(getString(R.string.gps_settings)
                ,0){
                    startActivity( Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS) );
                }
            }

        }
    }
    fun updateVisit() {
        binding!!.llLoading.show()
        for (item in edtitVisit!!.visitResources)
            item.id = 0
        val str = Gson().toJson(edtitVisit)
        wtf(str)
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java)
            .updateVisit(edtitVisit!!)
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    safeCall(binding!!.llLoading) {
                        binding!!.llLoading.hide()
                        MyApplication.selectedVisit = edtitVisit
                        if (response.code() != 500) {
                            FirebaseCrashlytics.getInstance().log("UPDATED:\n"+str)
                            FirebaseCrashlytics.getInstance().recordException(RuntimeException("UPDATED:\n"+str))
                            if (edtitVisit!!.reasonId != AppHelper.getReasonID(AppConstants.REASON_ON_THE_WAY) && edtitVisit!!.reasonId != AppHelper.getReasonID(AppConstants.REASON_ARRIVED) && edtitVisit!!.reasonId != AppHelper.getReasonID(AppConstants.REASON_COMPLETED) || !response.body()!!.message.equals(
                                    "Visit updated successfully"
                                )
                            ) {
                                createDialog(response.body()!!.message!!)
                            } else if (edtitVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_COMPLETED)) {
                                requireContext().createRetryDialog(
                                    response.body()!!.message!!
                                ) {
                                    MyApplication.onTheWayVisit = edtitVisit
                                    MyApplication.gettingTracked = false
                                    (requireActivity() as ActivtyVisitDetails).changeState(false)
                                }
                            }  else if (edtitVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_ARRIVED)) {
                                requireContext().createRetryDialog(
                                    response.body()!!.message!!
                                ) {
                                    changeLocation(edtitVisit!!.id!!);
                                    MyApplication.gettingTracked = false
                                    (requireActivity() as ActivtyVisitDetails).changeState(false)
                                }
                            }else {
                                requireContext().createRetryDialog(
                                    response.body()!!.message!!
                                ) {
                                    MyApplication.gettingTracked = true
                                    MyApplication.onTheWayVisit = edtitVisit
                                    /*     MyApplication.isFirstLocation =true*/
                                    (requireActivity() as ActivtyVisitDetails).changeState(true)
                                }
                            }

                            if (response.body()!!.message.equals(getString(R.string.visit_succ))) {
                                initialData()
                                setUpVisitDetails()
                            }

                        } else {
                            FirebaseCrashlytics.getInstance().log("UPDATE ERROR 500+ITEM:\n"+str)
                            FirebaseCrashlytics.getInstance().recordException(RuntimeException("UPDATE ERROR 500+ITEM:\n"+str))
                            requireContext().createRetryDialog(
                                getString(R.string.visit_succ)
                            ) {
                                MyApplication.onTheWayVisit = edtitVisit
                                MyApplication.gettingTracked = false
                                (requireActivity() as ActivtyVisitDetails).changeState(false)
                            }
                        }
                    }

                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    binding!!.llLoading.hide()
                    wtf(getString(R.string.error_getting_data))
                }

            })
    }

    fun setUpEdit() {
        edtitVisit = Visit(
            MyApplication.selectedVisit!!.account,
            MyApplication.selectedVisit!!.accountId,
            if (!MyApplication.selectedVisit!!.actualArrivalTime.isNullOrEmpty()) MyApplication.selectedVisit!!.actualArrivalTime else "",
            if (!MyApplication.selectedVisit!!.actualCompletedTime.isNullOrEmpty()) MyApplication.selectedVisit!!.actualCompletedTime else "",
            MyApplication.selectedVisit!!.actualDuration,
            MyApplication.selectedVisit!!.assignedTo,
            MyApplication.selectedVisit!!.assignedToId,
            MyApplication.selectedVisit!!.company,
            MyApplication.selectedVisit!!.companyAddress,
            MyApplication.selectedVisit!!.companyAddressId,
            MyApplication.selectedVisit!!.companyId,
            MyApplication.selectedVisit!!.contact,
            MyApplication.selectedVisit!!.contactId,
            MyApplication.selectedVisit!!.contract,
            MyApplication.selectedVisit!!.contractId,
            MyApplication.selectedVisit!!.creationDate,
            MyApplication.selectedVisit!!.duration,
            MyApplication.selectedVisit!!.email,
            MyApplication.selectedVisit!!.fromTime,
            MyApplication.selectedVisit!!.id,
            MyApplication.selectedVisit!!.inverseParentVisit,
            MyApplication.selectedVisit!!.isDeleted,
            MyApplication.selectedVisit!!.modificationDate,
            MyApplication.selectedVisit!!.number,
            MyApplication.selectedVisit!!.opportunity,
            MyApplication.selectedVisit!!.opportunityId,
            MyApplication.selectedVisit!!.order,
            MyApplication.selectedVisit!!.orderId,
            MyApplication.selectedVisit!!.owner,
            MyApplication.selectedVisit!!.ownerId,
            MyApplication.selectedVisit!!.parentVisit,
            MyApplication.selectedVisit!!.parentVisitId,
            MyApplication.selectedVisit!!.phoneNumber,
            MyApplication.selectedVisit!!.priority,
            MyApplication.selectedVisit!!.priorityId,
            MyApplication.selectedVisit!!.reason,
            MyApplication.selectedVisit!!.reasonCode,
            MyApplication.selectedVisit!!.reasonId,
            MyApplication.selectedVisit!!.remark,
            MyApplication.selectedVisit!!.resource,
            MyApplication.selectedVisit!!.resourceId,
            MyApplication.selectedVisit!!.status,
            MyApplication.selectedVisit!!.statusId,
            MyApplication.selectedVisit!!.timeStamp,
            MyApplication.selectedVisit!!.title,
            MyApplication.selectedVisit!!.toTime,
            MyApplication.selectedVisit!!.type,
            MyApplication.selectedVisit!!.typeId,
            MyApplication.selectedVisit!!.visitDate,
            MyApplication.selectedVisit!!.visitLocations,
            MyApplication.selectedVisit!!.visitProducts,
            MyApplication.selectedVisit!!.visitResources,
            MyApplication.selectedVisit!!.isHeader,
            MyApplication.selectedVisit!!.dateMill,
            MyApplication.selectedVisit!!.headerOrder
        )
        edtitVisit!!.appearDuration = MyApplication.selectedVisit!!.appearDuration
        setUpVisitDetails()
    }


    fun setUpPermission() {
        mPermissionResult =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
            { result ->
                var permission = true
                for (item in result) {
                    permission = item.value && permission
                }
                if (!permission) {
                    requireActivity().toast(getString(R.string.location_updates_disabled_arr))
                    for (item in result) {
                        if (ContextCompat.checkSelfPermission(
                                requireContext(),
                                item.key
                            ) == BLOCKED
                        ) {

                            if (getPermissionStatus(Manifest.permission.ACCESS_FINE_LOCATION) == BLOCKED_OR_NEVER_ASKED) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.please_grant_permission),
                                    Toast.LENGTH_LONG
                                ).show()
                                break
                            }
                        }
                    }
                }else{
                    changeLocation(edtitVisit!!.id!!)
                }
            }
    }
    fun init() {
        listeners()
        val fromNotf = (requireActivity() as ActivtyVisitDetails).fromNotf
        if (fromNotf == 1) {
            (requireActivity() as ActivtyVisitDetails).fromNotf++
            binding!!.llLoading.show()
            val visitId = (requireActivity() as ActivtyVisitDetails).visitId
            getData(visitId)
        } else {
            setUpData()
            initialData()
        }
    }

    fun getCompany() {
        RetrofitClientSpecificAuth.client!!.create(
            RetrofitInterface::class.java
        ).getCompanies().enqueue(object : Callback<ArrayList<Company>> {
            override fun onResponse(
                call: Call<ArrayList<Company>>,
                response: Response<ArrayList<Company>>
            ) {
                val company = response.body()!!.find {
                    it.id == MyApplication.selectedVisit!!.companyId
                }
                MyApplication.selectedVisit!!.company = company
                wtf(Gson().toJson(MyApplication.selectedVisit))
                initialData()
                setUpData()
            }

            override fun onFailure(call: Call<ArrayList<Company>>, t: Throwable) {
                binding!!.llLoading.hide()
            }

        })
    }

    fun getData(id: Int) {
        RetrofitClientSpecificAuth.client!!.create(
            RetrofitInterface::class.java
        ).getVisitById(id)
            .enqueue(object : Callback<Visit> {
                override fun onResponse(call: Call<Visit>, response: Response<Visit>) {
                    edtitVisit = response.body()
                    MyApplication.selectedVisit = response.body()
                    MyApplication.selectedVisit!!.appearDuration = AppHelper.durationToString(MyApplication.selectedVisit!!.duration!!.toFloat())
                    getCompany()
                }

                override fun onFailure(call: Call<Visit>, t: Throwable) {
                    binding!!.llLoading.hide()
                }

            })
    }

    fun setUpData() {
        binding!!.llLoading.hide()
        setUpEdit()
        if (edtitVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_COMPLETED)) {
            binding!!.btSave.setBackgroundResource(R.drawable.rounded_trans_primary)
            binding!!.btSave.isEnabled = false
        }
    }

    fun getPermissionStatus(androidPermissionName: String?): Int {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                androidPermissionName!!
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    androidPermissionName
                )
            ) {
                BLOCKED_OR_NEVER_ASKED
            } else DENIED
        } else GRANTED
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPermission()
        init()

    }

    fun initialData() {
        arrSpinner.clear()
        for(item in MyApplication.lookupsReason){
            if(!item.id.equals(AppHelper.getReasonID(AppConstants.REASON_UNSCHEDULED)) && !item.id.equals(AppHelper.getReasonID(AppConstants.REASON_CANCELLED))) {
                arrSpinner.add(
                    ItemSpinner(
                        item.id,
                        item.name,
                        false
                    )
                )
            }
        }

        arrSpinner.find {
            it.id == edtitVisit!!.reasonId
        }!!.selected = true


        if (edtitVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_ARRIVED)) {
            setUpStatusReasonSpinner()
            for (item in arrSpinner)
                if (item.id != AppHelper.getReasonID(AppConstants.REASON_ARRIVED) && item.id != AppHelper.getReasonID(AppConstants.REASON_COMPLETED))
                    item.selectable = false
        } else if (edtitVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_COMPLETED)) {
            binding!!.spStatusReason.hide()
            binding!!.ivArrowSpin.hide()
            binding!!.rlStatusSpinner.setBackgroundResource(R.drawable.disabled_rounded)
            binding!!.tvStatreason.text = getString(R.string.completed)
            binding!!.etRemark.isEnabled = false
            binding!!.etRemark.setTextColor(AppHelper.getColor(requireContext(),R.color.text_color))
            binding!!.etRemark.setBackgroundResource(R.drawable.disabled_rounded)
            binding!!.tvActualArrivalTime.setBackgroundResource(R.drawable.disabled_rounded)
            binding!!.tvActualArrivalTime.setTextColor(AppHelper.getColor(requireContext(),R.color.text_color))
            binding!!.tvActualCompletedTime.setBackgroundResource(R.drawable.disabled_rounded)
            binding!!.tvActualCompletedTime.setTextColor(AppHelper.getColor(requireContext(),R.color.text_color))
        } else if (edtitVisit!!.reasonId ==  AppHelper.getReasonID(AppConstants.REASON_PENDING)) {
            setUpStatusReasonSpinner()
            for (item in arrSpinner)
                if (item.id !=  AppHelper.getReasonID(AppConstants.REASON_PENDING) && item.id != AppHelper.getReasonID(AppConstants.REASON_ON_THE_WAY))
                    item.selectable = false
        } else if (edtitVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_ON_THE_WAY)) {
            setUpStatusReasonSpinner()
            for (item in arrSpinner)
                if (item.id == AppHelper.getReasonID(AppConstants.REASON_SCHEDULED) || item.id == AppHelper.getReasonID(AppConstants.REASON_COMPLETED))
                    item.selectable = false
        } else if (edtitVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_SCHEDULED)) {
            isScheduled=true
            setUpStatusReasonSpinner()
            for (item in arrSpinner)
                if (item.id ==  AppHelper.getReasonID(AppConstants.REASON_PENDING) || item.id == AppHelper.getReasonID(AppConstants.REASON_COMPLETED))
                    item.selectable = false
        }
    }

    override fun onItemClicked(view: View, position: Int) {
        if (arrSpinner.get(position).selectable!!) {
            for (item in arrSpinner)
                item.selected = false
            arrSpinner.get(position).selected = true
            alertDialog!!.dismiss()
            adapter!!.notifyDataSetChanged()
            if (arrSpinner.get(position).id == AppHelper.getReasonID(AppConstants.REASON_ARRIVED)) {
                val cal = Calendar.getInstance()
                binding!!.tvActualArrivalTime.text = simpTimeAA.format(cal.time)
                edtitVisit!!.actualArrivalTime = simpOrgss.format(cal.time)
            } else if (arrSpinner.get(position).id == AppHelper.getReasonID(AppConstants.REASON_COMPLETED)) {
                val cal = Calendar.getInstance()
                binding!!.tvActualCompletedTime.text = simpTimeAA.format(cal.time)
                edtitVisit!!.actualCompletedTime = simpOrgss.format(cal.time)
            } else {
                binding!!.tvActualCompletedTime.text = ""
            }
            edtitVisit!!.reasonId = arrSpinner.get(position).id
        }
    }
   /* override fun onResume() {
        super.onResume()
        MyApplication.activityResumed()
        if (accessLocation)
            (requireActivity() as ActivtyVisitDetails).changeState(true)
    }*/
}