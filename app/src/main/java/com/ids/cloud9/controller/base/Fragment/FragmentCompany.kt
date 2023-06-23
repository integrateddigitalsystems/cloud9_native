package com.ids.cloud9.controller.base.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.databinding.LayoutCompanyBinding
import com.ids.cloud9.model.CompanyRequest
import com.ids.cloud9.model.ResponseMessage
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentCompany : Fragment() {
    var binding : LayoutCompanyBinding?=null
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
    fun listenrs(){
        binding!!.llGetDirection.setOnClickListener {
            val geoUri =
                "http://maps.google.com/maps?q=loc:" + MyApplication.selectedVisit!!.company!!.lat + "," + MyApplication.selectedVisit!!.company!!.long + " (" + MyApplication.selectedVisit!!.company!!.companyName + ")"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
            startActivity(intent)
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

    fun editCompany(){
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
            binding!!.tvWebite.text.toString()
        )
        Log.wtf("JAD_COMPANY",Gson().toJson(editCompanyReq))
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java).editCompany(editCompanyReq)
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.isSuccessful){
                        MyApplication.selectedVisit!!.company!!.email = binding!!.tvEmail!!.text.toString()
                        MyApplication.selectedVisit!!.company!!.fax = binding!!.tvFax!!.text.toString()
                        MyApplication.selectedVisit!!.company!!.website = binding!!.tvWebite!!.text.toString()
                        MyApplication.selectedVisit!!.company!!.phoneNumber = binding!!.tvPhone!!.text.toString()
                        MyApplication.selectedVisit!!.contact!!.personalPhoneNumber = binding!!.tvContactNumber!!.text.toString()
                        MyApplication.selectedVisit!!.company!!.address = binding!!.tvAddress!!.text.toString()
                        setUpCompanyData()
                        binding!!.llButtons.hide()
                        binding!!.ivEditCompany.show()
                        wtf("Success")
                    }
                }
                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
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
        if(MyApplication.selectedVisit!!.company!=null) {
            binding!!.tvCompanyName.setTextLang(
                MyApplication.selectedVisit!!.company!!.companyName!!,
                MyApplication.selectedVisit!!.company!!.companyNameAr!!
            )
            binding!!.tvEmail.setCheckText(MyApplication.selectedVisit!!.company!!.email!!)
            binding!!.tvEmailValue.text = MyApplication.selectedVisit!!.company!!.email
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
        init()
    }
}