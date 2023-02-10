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
import com.ids.cloud9.utils.AppHelper
import com.ids.cloud9.utils.setCheckText
import com.ids.cloud9.utils.setTextLang
import com.ids.cloud9.utils.underline


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
    }

    fun setUpCompanyData(){

        binding!!.tvCompanyName.setTextLang(
            MyApplication.selectedVisit!!.company!!.companyName,
            MyApplication.selectedVisit!!.company!!.companyNameAr)
        binding!!.tvEmail.setCheckText(MyApplication.selectedVisit!!.company!!.email)
        binding!!.tvWebite.setCheckText(MyApplication.selectedVisit!!.company!!.website)
        binding!!.tvPhone.setCheckText(MyApplication.selectedVisit!!.company!!.phoneNumber)
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
        binding!!.tvFax.setCheckText(MyApplication.selectedVisit!!.company!!.fax)
        binding!!.tvContactName.setTextLang(
            MyApplication.selectedVisit!!.contact!!.firstName + " "+ MyApplication.selectedVisit!!.contact!!.lastName,
            MyApplication.selectedVisit!!.contact!!.firstNameAr + " "+ MyApplication.selectedVisit!!.contact!!.lastNameAr)
        binding!!.tvContactNumber.setCheckText(MyApplication.selectedVisit!!.contact!!.personalPhoneNumber)
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
        if(MyApplication.selectedVisit!!.companyAddress!=null)
            binding!!.tvAddress.setCheckText(MyApplication.selectedVisit!!.companyAddress!!.address)
        else
            binding!!.tvAddress.text = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
}