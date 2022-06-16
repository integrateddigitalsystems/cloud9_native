package com.ids.librascan.controller.Activities

import Base.ActivityCompactBase
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ids.librascan.controller.Adapters.AdapterQrCode
import com.ids.librascan.controller.Adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.librascan.databinding.ActivityQrDataBinding
import com.ids.librascan.databinding.LoadingTransBinding
import com.ids.librascan.db.QrCodeDatabase
import kotlinx.coroutines.launch
import utils.hide
import utils.show

class ActivityQrData : ActivityCompactBase(), RVOnItemClickListener {
    lateinit var activityQrDataBinding: ActivityQrDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityQrDataBinding = ActivityQrDataBinding.inflate(layoutInflater)
        setContentView(activityQrDataBinding.root)

        init()
    }

     fun init() {
         val loadingTransBinding = LoadingTransBinding.inflate(layoutInflater)
         loadingTransBinding.loading.show()
         val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
         activityQrDataBinding.rVQrData.layoutManager = layoutManager

         launch {
             application?.let {
                 val qrCodes = QrCodeDatabase(it).getUrlDao().getAllUrl()
                 activityQrDataBinding.rVQrData.adapter = AdapterQrCode(qrCodes)
             }
             loadingTransBinding.loading.hide()
         }
    }


    override fun onItemClicked(view: View, position: Int) {
        TODO("Not yet implemented")
    }
}