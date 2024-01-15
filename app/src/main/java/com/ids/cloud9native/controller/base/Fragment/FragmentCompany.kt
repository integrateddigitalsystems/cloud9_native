package com.ids.cloud9native.controller.base.Fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.databinding.LayoutCompanyBinding
import com.ids.cloud9native.model.CompanyRequest
import com.ids.cloud9native.model.ResponseMessage
import com.ids.cloud9native.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentCompany : Fragment(),ApiListener {
    var binding : LayoutCompanyBinding?=null
    var mPermissionResult: ActivityResultLauncher<Array<String>>? = null
    val BLOCKED = -1
    val BLOCKED_OR_NEVER_ASKED = 2
    val GRANTED = 0
    val DENIED = 1
    var firstLocation: Location? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutCompanyBinding.inflate(inflater, container, false)
        return binding!!.root
    }
    fun init(){
        setUpCompanyData()
        listenrs()
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
                }
              else {
                    changeLocation()
                }
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
    fun listenrs(){
        binding!!.llGetDirection.setOnClickListener {
            val geoUri =
                "http://maps.google.com/maps?q=loc:" + MyApplication.selectedVisit!!.company!!.lat + "," + MyApplication.selectedVisit!!.company!!.long + " (" + MyApplication.selectedVisit!!.company!!.companyName + ")"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
            startActivity(intent)
        }

        binding!!.llSendDirection.setOnClickListener {
            changeLocation()
        }

        if(MyApplication.selectedVisit!!.reasonId != AppHelper.getReasonID(AppConstants.REASON_ARRIVED))
            binding!!.ivEditCompany.hide()

        binding!!.ivEditCompany.setOnClickListener {
            binding!!.tvEmailValue.hide()
            binding!!.tvEmail.show()
            binding!!.tvEmail.setBackgroundResource(R.drawable.light_rounded_border)
            binding!!.tvWebsiteValue.hide()
            binding!!.tvWebite.show()
            binding!!.tvWebite.setBackgroundResource(R.drawable.light_rounded_border)
            binding!!.tvPhoneValue.hide()
            binding!!.tvPhone.show()
            binding!!.tvPhone.setBackgroundResource(R.drawable.light_rounded_border)
            binding!!.tvFaxValue.hide()
            binding!!.tvFax.setBackgroundResource(R.drawable.light_rounded_border)
            binding!!.tvFax.show()
          //  binding!!.tvContactName.setBackgroundResource(R.drawable.light_rounded_border)
            binding!!.tvContactNumber.show()
            binding!!.tvContactNumberValue.hide()
            binding!!.tvContactNumber.setBackgroundResource(R.drawable.light_rounded_border)
            binding!!.tvAddress.show()
            binding!!.tvAddress.setBackgroundResource(R.drawable.light_rounded_border)
            binding!!.tvEmail.isEnabled = true
            binding!!.tvWebite.isEnabled = true
            binding!!.tvPhone.isEnabled = true
            binding!!.tvFax.isEnabled = true
        //    binding!!.tvContactName.isEnabled = true
            binding!!.tvContactNumber.isEnabled = true
            binding!!.tvAddress.isEnabled = true
            binding!!.llButtons.show()
            binding!!.ivEditCompany.hide()
        }

        binding!!.btCancel.setOnClickListener {
            hideTexts()
            setUpCompanyData()
            binding!!.llButtons.hide()
            binding!!.ivEditCompany.show()
        }
        binding!!.btSave.setOnClickListener {
            hideTexts()
            editCompany()
        }
    }

    fun changeLocation() {
        firstLocation = GetLocation(requireActivity()).getLocation(this,this,null)

        if (firstLocation!=null){
            editCompany()
        }

    }

    fun editCompany(){
        binding!!.llLoading.show()
        if (firstLocation!=null){
            MyApplication.selectedVisit!!.company!!.lat = firstLocation!!.latitude.toString()
            MyApplication.selectedVisit!!.company!!.long = firstLocation!!.longitude.toString()
        }
        val editCompanyReq  = CompanyRequest(
            binding!!.tvAddress.text.toString().trim(),
            binding!!.tvAddress.text.toString().trim(),
            binding!!.tvCompanyName.text.toString().trim(),
            binding!!.tvCompanyName.text.toString().trim(),
            binding!!.tvEmail.text.toString().trim(),
            binding!!.tvFax.text.toString().trim(),
            MyApplication.selectedVisit!!.company!!.id!!,
            binding!!.tvPhone.text.toString().trim(),
            binding!!.tvContactNumber.text.toString().trim(),
            binding!!.tvWebite.text.toString(),
            MyApplication.selectedVisit!!.company!!.long,
            MyApplication.selectedVisit!!.company!!.lat
        )
        Log.wtf("JAD_COMPANY",Gson().toJson(editCompanyReq))
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java).editCompany(editCompanyReq)
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.isSuccessful){
                        binding!!.llLoading.hide()
                        if (response.body()!!.success=="true"){
                            createDialog(response.body()!!.message!!)
                            MyApplication.selectedVisit!!.company!!.email = binding!!.tvEmail.text.toString()
                            MyApplication.selectedVisit!!.company!!.fax = binding!!.tvFax.text.toString()
                            MyApplication.selectedVisit!!.company!!.website = binding!!.tvWebite.text.toString()
                            MyApplication.selectedVisit!!.company!!.phoneNumber = binding!!.tvPhone.text.toString()
                            MyApplication.selectedVisit!!.contact!!.personalPhoneNumber = binding!!.tvContactNumber.text.toString()
                            MyApplication.selectedVisit!!.company!!.address = binding!!.tvAddress.text.toString()
                            setUpCompanyData()
                            binding!!.llButtons.hide()
                            binding!!.ivEditCompany.show()
                            wtf("Success")
                        }
                        else {
                            binding!!.llLoading.hide()
                            createDialog(response.body()!!.message!!)
                        }

                    }
                    else{
                        binding!!.llLoading.hide()
                        createDialog(getString(R.string.failure))
                    }
                }
                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    binding!!.llLoading.hide()
                    wtf("Failure")
                }
            })
    }

    fun hideTexts(){
        binding!!.tvEmailValue.show()
        binding!!.tvEmail.hide()
        binding!!.tvEmail.setBackgroundResource(R.color.transparent)
        binding!!.tvWebsiteValue.show()
        binding!!.tvWebite.hide()
        binding!!.tvWebite.setBackgroundResource(R.color.transparent)
        binding!!.tvPhoneValue.show()
        binding!!.tvPhone.hide()
        binding!!.tvPhone.setBackgroundResource(R.color.transparent)
        binding!!.tvFaxValue.show()
        binding!!.tvFax.hide()
        binding!!.tvFax.setBackgroundResource(R.color.transparent)
        binding!!.tvContactName.setBackgroundResource(R.color.transparent)
        binding!!.tvContactNumberValue.show()
        binding!!.tvContactNumber.hide()
        binding!!.tvContactNumber.setBackgroundResource(R.color.transparent)
        binding!!.tvAddress.setBackgroundResource(R.color.transparent)
        binding!!.tvEmail.isEnabled = false
        binding!!.tvWebite.isEnabled = false
        binding!!.tvPhone.isEnabled = false
        binding!!.tvFax.isEnabled = false
        binding!!.tvContactName.isEnabled = false
        binding!!.tvContactNumber.isEnabled = false
        binding!!.tvAddress.isEnabled = false
    }
    fun setUpCompanyData(){
        if ( MyApplication.selectedVisit!!.reasonId==AppHelper.getReasonID(AppConstants.REASON_ARRIVED)){
            binding!!.llSendDirection.show()
        }
        if(MyApplication.selectedVisit!!.company!=null) {
            binding!!.tvCompanyName.setTextLang(
                MyApplication.selectedVisit!!.company!!.companyName!!,
                MyApplication.selectedVisit!!.company!!.companyNameAr!!
            )
            if(!MyApplication.selectedVisit!!.company!!.website!!.isNullOrEmpty()){
                binding!!.tvWebsiteValue.text = MyApplication.selectedVisit!!.company!!.website!!
            }else{
                binding!!.tvWebsiteValue.text = ""
            }
            if(!MyApplication.selectedVisit!!.company!!.website!!.isNullOrEmpty()){
                binding!!.tvWebite.text = MyApplication.selectedVisit!!.company!!.website!!.toEditable()
            }else{
                binding!!.tvWebite.text = "".toEditable()
            }
            if(!MyApplication.selectedVisit!!.company!!.email.isNullOrEmpty()){
                binding!!.tvEmailValue.text = MyApplication.selectedVisit!!.company!!.email!!.toEditable()
            }else{
                binding!!.tvEmailValue.text = ""
            }
            if(!MyApplication.selectedVisit!!.company!!.email!!.isNullOrEmpty()){
                binding!!.tvEmail.text = MyApplication.selectedVisit!!.company!!.email!!.toEditable()
            }else{
                binding!!.tvEmail.text = "".toEditable()
            }
            binding!!.tvWebite.setCheckText(MyApplication.selectedVisit!!.company!!.website!!)
            binding!!.tvWebsiteValue.text = MyApplication.selectedVisit!!.company!!.website!!
            if(MyApplication.selectedVisit!!.company!!.phoneNumber!=null && MyApplication.selectedVisit!!.company!!.phoneNumber!!.isNotEmpty()) {
                binding!!.tvPhone.setCheckText(MyApplication.selectedVisit!!.company!!.phoneNumber!!)
                binding!!.tvPhoneValue.text = MyApplication.selectedVisit!!.company!!.phoneNumber
            }else {
                binding!!.tvPhone.setText("")
                binding!!.tvPhoneValue.text = ""
            }
        }
        if(AppHelper.isValidPhoneNumber(binding!!.tvPhone.text.toString()))
        {
            AppHelper.setTextColor(requireContext(),binding!!.tvPhoneValue, R.color.colorPrimaryDark)
            binding!!.tvPhoneValue.underline()
            binding!!.tvPhoneValue.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:"+binding!!.tvPhoneValue.text)
                startActivity(intent)
            }
        }
        binding!!.tvFaxValue.text = MyApplication.selectedVisit!!.company!!.fax
        binding!!.tvFax.setCheckText(MyApplication.selectedVisit!!.company!!.fax!!)
        if(MyApplication.selectedVisit!!.contact!=null) {
            binding!!.tvContactName.setTextLang(
                MyApplication.selectedVisit!!.contact!!.firstName + " " + MyApplication.selectedVisit!!.contact!!.lastName,
                MyApplication.selectedVisit!!.contact!!.firstNameAr + " " + MyApplication.selectedVisit!!.contact!!.lastNameAr
            )
            binding!!.tvContactNumberValue.text = MyApplication.selectedVisit!!.contact!!.personalPhoneNumber!!
            binding!!.tvContactNumber.setCheckText(MyApplication.selectedVisit!!.contact!!.personalPhoneNumber!!)
        }else{
            binding!!.tvContactName.setText("")
            binding!!.tvContactNumber.setText("")
        }
        if(AppHelper.isValidPhoneNumber(binding!!.tvContactNumberValue.text.toString()))
        {
            AppHelper.setTextColor(requireContext(),binding!!.tvContactNumberValue, R.color.colorPrimaryDark)
            binding!!.tvContactNumberValue.underline()
            binding!!.tvContactNumberValue.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:"+binding!!.tvContactNumberValue.text)
                startActivity(intent)
            }
        }
        binding!!.tvAddress.setText(MyApplication.selectedVisit!!.company!!.address)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPermission()
        init()
    }

    override fun onDataRetrieved(success: Boolean, currLoc: Location?) {
        if (success && currLoc!=null){
            editCompany()
        }
        else {
            changeLocation()
        }
    }
}