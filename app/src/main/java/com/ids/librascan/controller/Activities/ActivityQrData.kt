package com.ids.librascan.controller.Activities

import Base.ActivityCompactBase
import android.Manifest
import android.annotation.SuppressLint
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
import com.ids.librascan.databinding.ActivityQrDataBinding
import com.ids.librascan.db.QrCode
import com.ids.librascan.db.QrCodeDatabase
import com.ids.librascan.utils.AppHelper
import info.bideens.barcode.BarcodeReader
import kotlinx.coroutines.launch
import utils.hide
import utils.show
import utils.toast

class ActivityQrData : ActivityCompactBase(), RVOnItemClickListener, BarcodeReader.BarcodeReaderListener ,
    OnInsertUpdate {
    lateinit var activityQrDataBinding: ActivityQrDataBinding
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

     fun init() {
         AppHelper.setAllTexts(activityQrDataBinding.rootData, this)
         activityQrDataBinding.ivScan.setOnClickListener {
             try {
                 AppHelper.closeKeyboard(this@ActivityQrData)
             } catch (e: Exception) {
                 e.printStackTrace()
             }
             checkCameraPermissions()
         }
         activityQrDataBinding.loading.show()
         launch {
             arrQrCode.addAll(QrCodeDatabase(application).getCodeDao().getAllCode())
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
                 filter(editable.toString())

             }
         })

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
            showAddBarcodeAlertDialog(this, true, arrFilter[position], this)
            launch {
                arrFilter.clear()
                arrQrCode.clear()
                arrFilter.addAll(QrCodeDatabase(application).getCodeDao().getAllCode())
                arrQrCode.addAll(QrCodeDatabase(application).getCodeDao().getAllCode())
                adapterQrCode.notifyDataSetChanged()
            }


        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun deleteQrData(position: Int){
        launch {
                QrCodeDatabase(application).getCodeDao().deleteCode(arrFilter.removeAt(position))
                arrFilter.clear()
                arrQrCode.clear()
                arrFilter.addAll(QrCodeDatabase(application).getCodeDao().getAllCode())
                arrQrCode.addAll(QrCodeDatabase(application).getCodeDao().getAllCode())
                adapterQrCode.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onInsertUpdate(boolean: Boolean) {
        launch {
                arrQrCode.clear()
                arrQrCode.addAll(QrCodeDatabase(application).getCodeDao().getAllCode())
                arrFilter.clear()
                arrFilter.addAll(QrCodeDatabase(application).getCodeDao().getAllCode())
                adapterQrCode.notifyDataSetChanged()
        }
    }
    fun back(v: View) {
        onBackPressed()
    }

    fun createDialogDelete(position: Int) {
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

    private fun checkCameraPermissions() {
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
            activityQrDataBinding.tvBarcode.setText(value)
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
        } else {
            super.onBackPressed()
        }
    }


}