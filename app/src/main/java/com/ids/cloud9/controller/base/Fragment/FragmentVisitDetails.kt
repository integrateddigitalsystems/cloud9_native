package com.ids.cloud9.controller.base.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.ids.cloud9.R
import com.ids.cloud9.controller.adapters.AdapterDialog
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.activities.ActivtyVisitDetails
import com.ids.cloud9.controller.adapters.AdapterSpinner
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.LayoutVisitBinding
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FragmentVisitDetails : Fragment(), RVOnItemClickListener {
    var alertDialog: androidx.appcompat.app.AlertDialog? = null
    var edtitVisit: Visit? = null
    var adapter: AdapterDialog? = null
    var changed: Boolean = false
    var adapterSpin: AdapterSpinner? = null
    var simp: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    var simpTime: SimpleDateFormat = SimpleDateFormat("HH:mm")
    var simpTimeAA: SimpleDateFormat = SimpleDateFormat("hh:mm aa")
    var simpOrg: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
    var simpOrgss: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssss")
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

    fun setUpVisitDetails() {
        var date = ""
        try {
            date = simp.format(simpOrg.parse(edtitVisit!!.visitDate))
        } catch (ex: Exception) {

        }
        binding!!.tvVisitDate.text = date
        try {
            binding!!.fromTime.text =
                simpTime.format(simpOrg.parse(edtitVisit!!.fromTime))
            binding!!.tvToTime.text =
                simpTime.format(simpOrg.parse(edtitVisit!!.toTime))
            var millFrom =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss").parse(edtitVisit!!.fromTime).time
            var millTo =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss").parse(edtitVisit!!.toTime).time
            var diff = millTo - millFrom
            var mins = diff / 60000
            var hours = mins / 60
            var min = mins % 60
            binding!!.tvDuration.text = if (hours > 0) {
                hours.toString() + " hrs " + (if (min > 0)
                    min.toString() + " mins"
                else
                    "")
            } else {
                if (min > 0)
                    min.toString() + " mins"
                else ""
            }

            if (edtitVisit!!.company != null) {
                binding!!.tvCompany.text = edtitVisit!!.company!!.companyName
            } else {
                binding!!.tvCompany.text = ""
            }
            if (!edtitVisit!!.actualArrivalTime.isNullOrEmpty())
                binding!!.tvActualArrivalTime.text = simpTimeAA.format(
                    simpOrg.parse(
                        edtitVisit!!.actualArrivalTime!!
                    )
                )
            else
                binding!!.tvActualArrivalTime.text = ""

            if (!edtitVisit!!.actualCompletedTime.isNullOrEmpty())
                binding!!.tvActualCompletedTime.text = simpTimeAA.format(
                    simpOrg.parse(
                        edtitVisit!!.actualCompletedTime!!
                    )
                )
            else
                binding!!.tvActualCompletedTime.text = ""

            if (edtitVisit!!.actualDuration != null) {
                binding!!.tvActualDurtionTime.text = edtitVisit!!.actualDuration.toString()
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
            AdapterSpinner(requireContext(), R.layout.spinner_text_item, arrSpinner, 0, true)
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
                        var pos = arrSpinner.indexOf(
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
                            if (arrSpinner.get(position).id == AppConstants.ARRIVED_REASON_ID) {
                                if(edtitVisit!!.actualArrivalTime.isNullOrEmpty() || changed) {
                                    var cal = Calendar.getInstance()
                                    binding!!.tvActualArrivalTime.text = simpTimeAA.format(cal.time)
                                    edtitVisit!!.actualArrivalTime = simpOrgss.format(cal.time)
                                    binding!!.tvActualCompletedTime.text = ""
                                    binding!!.tvActualDurtionTime.text = ""
                                }
                            } else if (arrSpinner.get(position).id == AppConstants.COMPLETED_REASON_ID) {
                                changed = true
                                var cal = Calendar.getInstance()
                                binding!!.tvActualCompletedTime.text = simpTimeAA.format(cal.time)
                                edtitVisit!!.actualCompletedTime = simpOrgss.format(cal.time)
                                var millFrom =
                                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss").parse(edtitVisit!!.actualArrivalTime).time
                                var millTo =
                                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss").parse(edtitVisit!!.actualCompletedTime).time
                                var diff = millTo - millFrom
                                var mins = diff / 60000
                                var hours = mins / 60
                                var min = mins % 60
                                binding!!.tvActualDurtionTime.text =
                                    if (hours > 0) {
                                        hours.toString() + "hrs" + min + "mins"
                                    } else {
                                        min.toString() + " mins"
                                    }
                            } else {
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
        var pos = arrSpinner.indexOf(
            arrSpinner.find {
                it.id == edtitVisit!!.reasonId
            }
        )
        binding!!.spStatusReason.setSelection(pos)
    }

    fun updateVisit() {
        binding!!.llLoading.show()
        for (item in edtitVisit!!.visitResources)
            item.id = 0
        var str = Gson().toJson(edtitVisit)
        wtf(str)
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java)
            .updateVisit(edtitVisit!!)
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    binding!!.llLoading.hide()
                    MyApplication.selectedVisit = edtitVisit
                    if(edtitVisit!!.reasonId != AppConstants.ON_THE_WAY_REASON_ID && edtitVisit!!.reasonId!=AppConstants.ARRIVED_REASON_ID&&edtitVisit!!.reasonId!=AppConstants.COMPLETED_REASON_ID || !response.body()!!.message.equals("Visit updated successfully")){
                        createDialog( response.body()!!.message!!)
                    }else if(edtitVisit!!.reasonId == AppConstants.COMPLETED_REASON_ID) {
                        requireContext().createRetryDialog(
                            response.body()!!.message!!){
                            MyApplication.onTheWayVisit = edtitVisit
                            (requireActivity() as ActivtyVisitDetails).changeState(false,0)
                        }
                    }else{
                        requireContext().createRetryDialog(
                            response.body()!!.message!!){
                            MyApplication.onTheWayVisit = edtitVisit
                            (requireActivity() as ActivtyVisitDetails).changeState(true,0)
                        }
                    }

                    if(response.body()!!.message.equals(getString(R.string.visit_succ))){
                        initialData()
                        setUpVisitDetails()
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
        setUpVisitDetails()
    }

    fun init() {
        listeners()
        var fromNotf = (requireActivity() as ActivtyVisitDetails).fromNotf
        if (fromNotf == 1) {
            (requireActivity() as ActivtyVisitDetails).fromNotf++
            binding!!.llLoading.show()
            var visitId = (requireActivity() as ActivtyVisitDetails).visitId
            getData(visitId)
        } else {
            setUpData()
            initialData()
        }
    }

    fun getCompany() {
        RetrofitClientAuth.client!!.create(
            RetrofitInterface::class.java
        ).getCompanies().enqueue(object : Callback<ArrayList<Company>> {
            override fun onResponse(
                call: Call<ArrayList<Company>>,
                response: Response<ArrayList<Company>>
            ) {
                var company = response.body()!!.find {
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
        RetrofitClientAuth.client!!.create(
            RetrofitInterface::class.java
        ).getVisitById(id)
            .enqueue(object : Callback<Visit> {
                override fun onResponse(call: Call<Visit>, response: Response<Visit>) {
                    edtitVisit = response.body()
                    MyApplication.selectedVisit = response.body()
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
        if (edtitVisit!!.reasonId == AppConstants.COMPLETED_REASON_ID) {
            binding!!.btSave.setBackgroundResource(R.drawable.rounded_trans_primary)
            binding!!.btSave.isEnabled = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    fun initialData() {
        arrSpinner.clear()
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
            it.id == edtitVisit!!.reasonId
        }!!.selected = true


        if (edtitVisit!!.reasonId == AppConstants.ARRIVED_REASON_ID) {
            setUpStatusReasonSpinner()
            for (item in arrSpinner)
                if (item.id != AppConstants.ARRIVED_REASON_ID && item.id != AppConstants.COMPLETED_REASON_ID)
                    item.selectable = false
        } else if (edtitVisit!!.reasonId == AppConstants.COMPLETED_REASON_ID) {
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
        } else if (edtitVisit!!.reasonId == AppConstants.PENDING_REASON_ID) {
            setUpStatusReasonSpinner()
            for (item in arrSpinner)
                if (item.id != AppConstants.PENDING_REASON_ID && item.id != AppConstants.ON_THE_WAY_REASON_ID)
                    item.selectable = false
        } else if (edtitVisit!!.reasonId == AppConstants.ON_THE_WAY_REASON_ID) {
            setUpStatusReasonSpinner()
            for (item in arrSpinner)
                if (item.id == AppConstants.SCHEDULED_REASON_ID || item.id == AppConstants.COMPLETED_REASON_ID)
                    item.selectable = false
        } else if (edtitVisit!!.reasonId == AppConstants.SCHEDULED_REASON_ID) {
            setUpStatusReasonSpinner()
            for (item in arrSpinner)
                if (item.id == AppConstants.PENDING_REASON_ID || item.id == AppConstants.COMPLETED_REASON_ID)
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
            if (arrSpinner.get(position).id == AppConstants.ARRIVED_REASON_ID) {
                var cal = Calendar.getInstance()
                binding!!.tvActualArrivalTime.text = simpTimeAA.format(cal.time)
                edtitVisit!!.actualArrivalTime = simpOrgss.format(cal.time)
            } else if (arrSpinner.get(position).id == AppConstants.COMPLETED_REASON_ID) {
                var cal = Calendar.getInstance()
                binding!!.tvActualCompletedTime.text = simpTimeAA.format(cal.time)
                edtitVisit!!.actualCompletedTime = simpOrgss.format(cal.time)
            } else {
                binding!!.tvActualCompletedTime.text = ""
            }
            edtitVisit!!.reasonId = arrSpinner.get(position).id
        }
    }
}