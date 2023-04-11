package com.ids.cloud9.controller.base.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.databinding.LayoutCompanyBinding
import com.ids.cloud9.utils.*


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

        binding!!.ivEditCompany.setOnClickListener {
            binding!!.tvEmail.setBackgroundResource(R.drawable.light_rounded_border)
            binding!!.tvWebite.setBackgroundResource(R.drawable.light_rounded_border)
            binding!!.tvPhone.setBackgroundResource(R.drawable.light_rounded_border)
            binding!!.tvFax.setBackgroundResource(R.drawable.light_rounded_border)
            binding!!.tvContactName.setBackgroundResource(R.drawable.light_rounded_border)
            binding!!.tvContactNumber.setBackgroundResource(R.drawable.light_rounded_border)
            binding!!.tvAddress.setBackgroundResource(R.drawable.light_rounded_border)
            binding!!.tvEmail.isEnabled = true
            binding!!.tvWebite.isEnabled = true
            binding!!.tvPhone.isEnabled = true
            binding!!.tvFax.isEnabled = true
            binding!!.tvContactName.isEnabled = true
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
            MyApplication.selectedVisit!!.company!!.email = binding!!.tvEmail.text.toString()
            MyApplication.selectedVisit!!.company!!.website = binding!!.tvWebite.text.toString()
            MyApplication.selectedVisit!!.company!!.phoneNumber = binding!!.tvPhone.text.toString()
            MyApplication.selectedVisit!!.company!!.fax = binding!!.tvFax.text.toString()
            MyApplication.selectedVisit!!.contact!!.firstName= binding!!.tvContactName.text.toString()
            MyApplication.selectedVisit!!.contact!!.personalPhoneNumber = binding!!.tvContactNumber.text.toString()
            MyApplication.selectedVisit!!.company!!.address = binding!!.tvAddress.text.toString()
            binding!!.llButtons.hide()
            binding!!.ivEditCompany.show()
        }


    }

    fun hideTexts(){
        binding!!.tvEmail.setBackgroundResource(R.color.transparent)
        binding!!.tvWebite.setBackgroundResource(R.color.transparent)
        binding!!.tvPhone.setBackgroundResource(R.color.transparent)
        binding!!.tvFax.setBackgroundResource(R.color.transparent)
        binding!!.tvContactName.setBackgroundResource(R.color.transparent)
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
            binding!!.tvWebite.setCheckText(MyApplication.selectedVisit!!.company!!.website!!)
            if(MyApplication.selectedVisit!!.company!!.phoneNumber!=null && MyApplication.selectedVisit!!.company!!.phoneNumber!!.isNotEmpty())
                binding!!.tvPhone.setCheckText(MyApplication.selectedVisit!!.company!!.phoneNumber!!)
            else
                binding!!.tvPhone.setText("")
        }
        if(AppHelper.isValidPhoneNumber(binding!!.tvPhone.text.toString()))
        {
            AppHelper.setTextColor(requireContext(),binding!!.tvPhone, R.color.colorPrimaryDark)
            binding!!.tvPhone.underline()
            binding!!.tvPhone.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:"+binding!!.tvPhone.text)
                startActivity(intent)
            }
        }
        binding!!.tvFax.setCheckText(MyApplication.selectedVisit!!.company!!.fax!!)
        if(MyApplication.selectedVisit!!.contact!=null) {
            binding!!.tvContactName.setTextLang(
                MyApplication.selectedVisit!!.contact!!.firstName + " " + MyApplication.selectedVisit!!.contact!!.lastName,
                MyApplication.selectedVisit!!.contact!!.firstNameAr + " " + MyApplication.selectedVisit!!.contact!!.lastNameAr
            )
            binding!!.tvContactNumber.setCheckText(MyApplication.selectedVisit!!.contact!!.personalPhoneNumber!!)
        }else{
            binding!!.tvContactName.setText("")
            binding!!.tvContactNumber.setText("")
        }
        if(AppHelper.isValidPhoneNumber(binding!!.tvContactNumber.text.toString()))
        {
            AppHelper.setTextColor(requireContext(),binding!!.tvContactNumber, R.color.colorPrimaryDark)
            binding!!.tvContactNumber.underline()
            binding!!.tvContactNumber.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:"+binding!!.tvContactNumber.text)
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