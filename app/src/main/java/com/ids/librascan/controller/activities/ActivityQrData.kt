package com.ids.librascan.controller.activities

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
import com.ids.librascan.controller.adapters.AdapterQrCode
import com.ids.librascan.controller.adapters.OnInsertUpdate.OnInsertUpdate
import com.ids.librascan.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivityQrDataBinding
import com.ids.librascan.db.QrCode
import com.ids.librascan.db.QrCodeDatabase
import com.ids.librascan.db.SessionQrcode
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
    private lateinit var barcodeReader: BarcodeReader
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityQrDataBinding = ActivityQrDataBinding.inflate(layoutInflater)
        setContentView(activityQrDataBinding.root)
        init()
    }
    private fun init() {
        barcodeReader = supportFragmentManager.findFragmentById(R.id.barcodeReader) as BarcodeReader

        getAllData()
        listener()
    }

    private fun getAllData(){
        activityQrDataBinding.loading.show()
        launch {
            arrQrCode.addAll(QrCodeDatabase(application).getCodeDao().getCodes(MyApplication.sessionId))
            checkQrcodeData()
            activityQrDataBinding.loading.hide()
            setData()
        }
    }

    private fun setData() {
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        activityQrDataBinding.rVQrData.layoutManager = layoutManager
        arrFilter.clear()
        arrFilter.addAll(arrQrCode)
        adapterQrCode = AdapterQrCode(arrFilter, this)
        activityQrDataBinding.rVQrData.adapter = adapterQrCode

    }

    private fun listener(){
        activityQrDataBinding.ivScan.setOnClickListener {
            checkCameraPermissions(view = activityQrDataBinding.btShowScan)
            MyApplication.isScan = false
            activityQrDataBinding.tvNameSession.text = MyApplication.sessionName
        }

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

            else showAddBarcodeAlertDialog( false, QrCode(), this,true,SessionQrcode())
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
            it.code!!.lowercase().replaceFirstChar(Char::lowercase).contains(text.lowercase().replaceFirstChar(Char::lowercase))
        })
        adapterQrCode.notifyDataSetChanged()

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClicked(view: View, position: Int) {
        if (view.id == R.id.tVDelete){
            createDialogDelete(position)
            adapterQrCode.notifyDataSetChanged()
        }
        else if (view.id == R.id.tVUpdate) {
            MyApplication.isScan = true
            showAddBarcodeAlertDialog( true, arrFilter[position], this,false, SessionQrcode())
            adapterQrCode.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteQrData(position: Int){
        launch {
                QrCodeDatabase(application).getCodeDao().deleteCode(arrFilter[position].idQrcode)
                arrQrCode.remove(arrFilter[position])
                adapterQrCode.notifyDataSetChanged()
                activityQrDataBinding.tvBarcode.setText("")
                checkQrcodeData()
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
              activityQrDataBinding.tvNoData.hide()
              adapterQrCode.notifyDataSetChanged()
        }
    }

    fun back(v: View) {
        startActivity(Intent(this@ActivityQrData, ActivitySessions::class.java))
        finish()
    }

    private fun createDialogDelete(position: Int) {
        val builder = android.app.AlertDialog.Builder(this)
            .setMessage(resources.getString(R.string.delete_message))
            .setCancelable(true)
            .setPositiveButton(resources.getString(R.string.yes))
            { dialog, _ ->
                deleteQrData(position)
                dialog.cancel()
            }
            .setNegativeButton(resources.getString(R.string.no))
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
                toast(resources.getString(R.string.camera_error))
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
                    insertScanAuto(QrCode(value,0,1,MyApplication.sessionId), this)
                    activityQrDataBinding.ivScan.show()
                }

                else  if (MyApplication.enableInsert && MyApplication.enableNewLine){
                    insertNewLineAuto(QrCode(value,0,1,MyApplication.sessionId),this)
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
        toast(resources.getString(R.string.camera_error))
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

    private fun checkQrcodeData(){
        launch {
            arrQrCode.clear()
            arrQrCode.addAll(QrCodeDatabase(application).getCodeDao().getCodes(MyApplication.sessionId))
            if (arrQrCode.isEmpty()){
                activityQrDataBinding.tvNoData.show()
                activityQrDataBinding.ivScan.hide()
            }
            else{
                activityQrDataBinding.tvNoData.hide()
                activityQrDataBinding.ivScan.show()
            }
        }
    }
}