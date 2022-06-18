package com.ids.librascan.controller.Activities

import Base.ActivityCompactBase
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ids.librascan.R
import com.ids.librascan.controller.Adapters.AdapterQrCode
import com.ids.librascan.controller.Adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.librascan.databinding.ActivityQrDataBinding
import com.ids.librascan.databinding.LoadingTransBinding
import com.ids.librascan.db.QrCode
import com.ids.librascan.db.QrCodeDatabase
import kotlinx.coroutines.launch
import utils.hide
import utils.show

class ActivityQrData : ActivityCompactBase(), RVOnItemClickListener {
    lateinit var activityQrDataBinding: ActivityQrDataBinding
    val qrCode : QrCode? = null
    private lateinit var adapterQrCode: AdapterQrCode
    private var arrQrCode = ArrayList<QrCode>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityQrDataBinding = ActivityQrDataBinding.inflate(layoutInflater)
        setContentView(activityQrDataBinding.root)

        init()
    }

     fun init() {
         val loadingTransBinding = LoadingTransBinding.inflate(layoutInflater)
         loadingTransBinding.loading.show()
         launch {
             arrQrCode.addAll(QrCodeDatabase(application).getUrlDao().getAllUrl())
             loadingTransBinding.loading.hide()
             setData()
         }
     }

    private fun setData() {
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        activityQrDataBinding.rVQrData.layoutManager = layoutManager
        adapterQrCode = AdapterQrCode(arrQrCode, this)
        activityQrDataBinding.rVQrData.adapter = adapterQrCode
    }


    override fun onItemClicked(view: View, position: Int) {
        if (view.id == R.id.iVDelete)
            deleteQrData(position)
    }

    private fun deleteQrData(position: Int){
        launch {
                 QrCodeDatabase(application).getUrlDao().deleteUrl(arrQrCode.removeAt(position))
                 adapterQrCode.notifyItemChanged(position)
        }
    }
}