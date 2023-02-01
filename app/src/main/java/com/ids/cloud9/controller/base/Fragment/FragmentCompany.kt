package com.ids.cloud9.controller.base.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.databinding.LayoutCompanyBinding
import com.ids.cloud9.databinding.LayoutMediaBinding
import com.ids.cloud9.utils.setCheckText
import com.ids.cloud9.utils.setTextLang

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
        binding!!.tvFax.setCheckText(MyApplication.selectedVisit!!.company!!.fax)
        binding!!.tvContactName.setTextLang(
            MyApplication.selectedVisit!!.contact!!.firstName + " "+ MyApplication.selectedVisit!!.contact!!.lastName,
            MyApplication.selectedVisit!!.contact!!.firstNameAr + " "+ MyApplication.selectedVisit!!.contact!!.lastNameAr)
        binding!!.tvContactNumber.setCheckText(MyApplication.selectedVisit!!.contact!!.personalPhoneNumber)
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