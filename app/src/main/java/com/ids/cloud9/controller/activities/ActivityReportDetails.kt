package com.ids.cloud9.controller.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.custom.AppCompactBase
import com.ids.cloud9.databinding.ActivityAddProductBinding
import com.ids.cloud9.databinding.ActivityReportDetailsBinding
import com.ids.cloud9.utils.AppConstants
import com.ids.cloud9.utils.hide
import com.ids.cloud9.utils.show
import java.text.SimpleDateFormat

class ActivityReportDetails : AppCompactBase() {

    var binding: ActivityReportDetailsBinding? = null
    var simp = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
    var simpDMY = SimpleDateFormat("dd/MM/yyyy")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }
    fun init() {
        setUpData()
        setUpWeb()
    }
    fun setUpWeb() {
        var web =
            "https://www.formsite.com/templates/registration-form-templates/club-registration-signup-form/"
        binding!!.wvReport.settings.javaScriptEnabled = true
        binding!!.wvReport.settings.loadWithOverviewMode = true
        binding!!.wvReport.settings.useWideViewPort = false
        binding!!.wvReport.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        binding!!.wvReport.settings.builtInZoomControls = false
        binding!!.wvReport.settings.displayZoomControls = false
        binding!!.wvReport.settings.domStorageEnabled = true
        binding!!.wvReport.loadUrl(web)
        binding!!.wvReport.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding!!.llLoading.hide()
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

            }
        }
    }

    fun setUpData() {
        var id = intent.getIntExtra("RepId", 0)
        binding!!.llTool.ivDrawer.hide()
        binding!!.llTool.btBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding!!.llTool.layoutFragment.show()
        binding!!.llTool.tvTitleTool.text = MyApplication.selectedProduct!!.reports.find {
            it.id == id
        }!!.name
        binding!!.tvVisit.text = MyApplication.selectedVisit!!.title
        binding!!.tvProductName.text = MyApplication.selectedProduct!!.product!!.name
        binding!!.tvClientName.text =
            if (MyApplication.languageCode.equals(AppConstants.LANG_ENGLISH)) MyApplication.selectedVisit!!.company!!.companyName else MyApplication.selectedVisit!!.company!!.companyNameAr
        binding!!.tvContactName.text =
            if (MyApplication.languageCode.equals(AppConstants.LANG_ENGLISH)) {
                MyApplication.selectedVisit!!.contact!!.firstName + " " + MyApplication.selectedVisit!!.contact!!.lastName
            } else {
                MyApplication.selectedVisit!!.contact!!.firstNameAr + " " + MyApplication.selectedVisit!!.contact!!.lastNameAr
            }
        binding!!.tvContactNumber.text =
            if (!MyApplication.selectedVisit!!.contact!!.personalPhoneNumber.isNullOrEmpty()) MyApplication.selectedVisit!!.contact!!.personalPhoneNumber else getString(
                R.string.n_a
            )
        binding!!.tvContractNumber.text =
            if (MyApplication.selectedVisit!!.contract != null) "contract" else getString(
                R.string.n_a
            )
        binding!!.tvFax.text =
            if (!MyApplication.selectedVisit!!.contact!!.fax.isNullOrEmpty()) MyApplication.selectedVisit!!.contact!!.fax else getString(
                R.string.n_a
            )
        binding!!.tvSiteLocation.text =
            if (!MyApplication.selectedVisit!!.contact!!.address.isNullOrEmpty()) MyApplication.selectedVisit!!.contact!!.address else getString(
                R.string.n_a
            )
        binding!!.tvDate.text =
            simpDMY.format(simp.parse(MyApplication.selectedProduct!!.creationDate))
        binding!!.tvOpportunity.text =
            if (MyApplication.selectedVisit!!.opportunity != null) MyApplication.selectedVisit!!.opportunity!!.name else getString(
                R.string.n_a
            )
        binding!!.tvMatcoEng.text =
            MyApplication.userItem!!.firstName + " " + MyApplication.userItem!!.lastName
    }
}