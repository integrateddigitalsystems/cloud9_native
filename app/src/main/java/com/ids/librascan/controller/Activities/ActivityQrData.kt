package com.ids.librascan.controller.Activities

import Base.ActivityCompactBase
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.SparseArray
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.vision.barcode.Barcode
import com.ids.librascan.R
import com.ids.librascan.controller.Adapters.AdapterQrCode
import com.ids.librascan.controller.Adapters.OnInsertUpdate.OnInsertUpdate
import com.ids.librascan.controller.Adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivityQrDataBinding
import com.ids.librascan.db.QrCode
import com.ids.librascan.db.QrCodeDatabase
import com.ids.librascan.db.Sessions
import com.ids.librascan.utils.AppHelper
import info.bideens.barcode.BarcodeReader
import kotlinx.coroutines.launch
import utils.hide
import utils.show
import utils.toast
import java.util.*

class ActivityQrData : ActivityCompactBase(), RVOnItemClickListener, BarcodeReader.BarcodeReaderListener ,
    OnInsertUpdate{
    private lateinit var adapterQrCode: AdapterQrCode
    private var arrQrCode = ArrayList<QrCode>()
    private var arrFilter = ArrayList<QrCode>()
    lateinit var barcodeReader: BarcodeReader
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityQrDataBinding = ActivityQrDataBinding.inflate(layoutInflater)
        setContentView(activityQrDataBinding.root)
        init()
    }
    private fun init() {
         AppHelper.setAllTexts(activityQrDataBinding.rootData, this)
         activityQrDataBinding.ivScan.setOnClickListener {
             checkCameraPermissions(view = activityQrDataBinding.btShowScan)
             MyApplication.isScan = false
             activityQrDataBinding.tvNameSession.text = MyApplication.sessionName
         }
         activityQrDataBinding.loading.show()
         launch {
             arrQrCode.addAll(QrCodeDatabase(application).getCodeDao().getCodes(MyApplication.sessionId))
             if (arrQrCode.isEmpty()){
                 activityQrDataBinding.tvNodata.show()
                 activityQrDataBinding.ivScan.hide()
             }
             activityQrDataBinding.loading.hide()
             setData()
         }
         barcodeReader = supportFragmentManager.findFragmentById(R.id.barcodeReader) as BarcodeReader

         activityQrDataBinding.tvBarcode.addTextChangedListener(object : TextWatcher {
             override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
             }

             override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
             }

             override fun afterTextChanged(editable: Editable) {
                 if (editable.toString() ==""){
                     activityQrDataBinding.ivClear.hide()
                 }
                 else  activityQrDataBinding.ivClear.show()
                 filter(editable.toString())

             }
         })

        activityQrDataBinding.llScan.setOnClickListener {
            MyApplication.isScan = true
            if (MyApplication.enableInsert && !MyApplication.enableNewLine)
                activityQrDataBinding.btShowScan.performClick()
            else if (MyApplication.enableInsert && MyApplication.enableNewLine)
                activityQrDataBinding.btShowScan.performClick()
            else{
                showAddBarcodeAlertDialog(this, false, QrCode(), this,true,Sessions(),MyApplication.sessionId)
            }
            activityQrDataBinding.tvNameSession.text = MyApplication.sessionName
        }

        activityQrDataBinding.tvBarcode.typeface = AppHelper.getTypeFace(this)
        activityQrDataBinding.ivClear.setOnClickListener {
            activityQrDataBinding.tvBarcode.setText("")
        }
     }


        @SuppressLint("NotifyDataSetChanged")
    private fun filter(text: String) {
        arrFilter.clear()
        arrFilter.addAll(arrQrCode.filter {
            it.code.lowercase().replaceFirstChar(Char::lowercase).contains(text.lowercase().replaceFirstChar(Char::lowercase))
        })
        adapterQrCode.notifyDataSetChanged()

    }
    private fun setData() {
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        activityQrDataBinding.rVQrData.layoutManager = layoutManager
        arrFilter.clear()
        arrFilter.addAll(arrQrCode)
        adapterQrCode = AdapterQrCode(arrFilter, this)
        activityQrDataBinding.rVQrData.adapter = adapterQrCode

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClicked(view: View, position: Int) {
        if (view.id == R.id.tVDelete)
            createDialogDelete(position)
        else if (view.id == R.id.tVUpdate) {
            MyApplication.isScan = true
            showAddBarcodeAlertDialog(this, true, arrFilter[position], this,false, Sessions(),MyApplication.sessionId)
            adapterQrCode.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteQrData(position: Int){
        launch {
                arrFilter.clear()
                arrFilter.addAll(QrCodeDatabase(application).getCodeDao().getCodes(MyApplication.sessionId))
                QrCodeDatabase(application).getCodeDao().deleteCode(arrFilter[position].id)
                arrQrCode.remove(arrFilter[position])
                adapterQrCode.notifyDataSetChanged()
                activityQrDataBinding.tvBarcode.setText("")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onInsertUpdate(boolean: Boolean) {
        launch {
            with(activityQrDataBinding) { tvBarcode.setText("") }
              arrQrCode.clear()
              arrFilter.clear()
              arrQrCode.addAll(QrCodeDatabase(application).getCodeDao().getCodes(MyApplication.sessionId))
              arrFilter.addAll(arrQrCode)
              activityQrDataBinding.tvNodata.hide()
              adapterQrCode.notifyDataSetChanged()
        }
    }
    fun back(v: View) {
        startActivity(Intent(this@ActivityQrData, ActivitySessions::class.java))
        finish()
    }

    private fun createDialogDelete(position: Int) {
        val builder = android.app.AlertDialog.Builder(this)
            .setMessage(AppHelper.getRemoteString("delete_message",this))
            .setCancelable(true)
            .setPositiveButton(AppHelper.getRemoteString("yes",this))
            { dialog, _ ->
                deleteQrData(position)
                dialog.cancel()
            }
            .setNegativeButton(AppHelper.getRemoteString("no",this))
            { dialog, _ ->
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()
    }

    fun checkCameraPermissions(view: View) {
        if (ContextCompat.checkSelfPermission(this@ActivityQrData,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@ActivityQrData,
                arrayOf(Manifest.permission.CAMERA),
                1
            )
        } else { //permissions granted
            //show barcode
            activityQrDataBinding.rlBarcode.show()
            barcodeReader.resumeScanning()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //GRANTED
                activityQrDataBinding.rlBarcode.show()
                barcodeReader.resumeScanning()
            } else {
                toast(AppHelper.getRemoteString("camera_error",this))
            }
        }
    }
    override fun onScanned(barcode: Barcode?) {
        val value = barcode.let { it!!.displayValue }
        barcodeReader.playBeep()
        barcodeReader.pauseScanning()
        this.runOnUiThread {
            activityQrDataBinding.rlBarcode.hide()
            if (MyApplication.isScan){
                if (MyApplication.enableInsert && !MyApplication.enableNewLine){
                    insertScanAuto(QrCode(value,0,1,MyApplication.sessionId), this,this)
                    activityQrDataBinding.ivScan.show()
                }

                else  if (MyApplication.enableInsert && MyApplication.enableNewLine){
                    insertScan(QrCode(value,0,1,MyApplication.sessionId),this,this)
                    activityQrDataBinding.ivScan.show()
                }
                else{
                    barcodeAlertDialog.show()
                    popupBarcodeBinding.tvCode.setText(value)
                }
            }
            else activityQrDataBinding.tvBarcode.setText(value)
        }
    }

    override fun onScannedMultiple(barcodes: MutableList<Barcode>?) {
        TODO("Not yet implemented")
    }

    override fun onBitmapScanned(sparseArray: SparseArray<Barcode>?) {
        TODO("Not yet implemented")
    }

    override fun onScanError(errorMessage: String?) {
        TODO("Not yet implemented")
    }

    override fun onCameraPermissionDenied() {
        toast(AppHelper.getRemoteString("camera_error",this))
    }

    override fun onBackPressed() {
        if (activityQrDataBinding.rlBarcode.visibility == View.VISIBLE) {
            barcodeReader.pauseScanning()
            activityQrDataBinding.rlBarcode.hide()
        }
        else{
            startActivity(Intent(this@ActivityQrData, ActivitySessions::class.java))
            finish()
        }

     }


}