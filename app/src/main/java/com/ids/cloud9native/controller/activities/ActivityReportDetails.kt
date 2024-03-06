package com.ids.cloud9native.controller.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.*
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.custom.AppCompactBase
import com.ids.cloud9native.databinding.ActivityReportDetailsBinding
import com.ids.cloud9native.utils.*
import java.text.SimpleDateFormat
import java.util.*


class ActivityReportDetails : AppCompactBase() {

    var binding: ActivityReportDetailsBinding? = null
    var simp = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH)
    var simpDMY = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    var reportId =0
    var url =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }
    fun init() {
        setUpData()
        url = intent.getStringExtra("url")!!
        if (url.isNotEmpty()) url += MyApplication.token
       // url ="http://78.47.192.120:40999/Builder/form-viewer.html?recordId=9"
        setUpWeb()
    }

    override fun onResume() {
        super.onResume()
        MyApplication.activityResumed()
    }
    override fun onPause() {
        super.onPause()
        MyApplication.activityPaused()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setUpWeb() {
        binding!!.llLoading.show()
      /*  val web = "https://www.formsite.com/templates/registration-form-templates/club-registration-signup-form/"*/
        binding!!.wvReport.settings.javaScriptEnabled = true
        binding!!.wvReport.settings.loadWithOverviewMode = true
        binding!!.wvReport.settings.useWideViewPort = false
        binding!!.wvReport.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        binding!!.wvReport.settings.builtInZoomControls = false
        binding!!.wvReport.settings.displayZoomControls = false
        binding!!.wvReport.settings.domStorageEnabled = true
        binding!!.wvReport.loadUrl(url)
        Log.wtf("URL_WEBVIEW",url)
        binding!!.wvReport.webViewClient = object : WebViewClient() {


            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.wtf("JAD-URL",url)
                binding!!.llLoading.hide()
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                binding!!.llLoading.show()
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun doUpdateVisitedHistory(view: WebView, url: String?, isReload: Boolean) {
                if (url!!.contains(MyApplication.url_contains)){
                    onBackPressedDispatcher.onBackPressed()
                }
                super.doUpdateVisitedHistory(view, url, isReload)
            }

        }
        binding!!.wvReport.webChromeClient = object : WebChromeClient(){

            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                return super.onJsAlert(view, url, message, result)
            }
        }
    }

    fun setUpData() {
        reportId = intent.getIntExtra("RepId", 0)
        val name = intent.getStringExtra("name")
        binding!!.llTool.ivDrawer.hide()
        binding!!.llTool.btBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding!!.llTool.ivCalendar.setOnClickListener {
            finishAffinity()
            startActivity(
                Intent(
                    this,
                    ActivityMain::class.java
                )
            )
        }
        binding!!.llTool.layoutFragment.show()
        binding!!.llTool.tvTitleTool.text = name


    }
}