package com.ids.librascan.controller.Activities

import Base.ActivityCompactBase
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.barcode.Barcode
import com.ids.librascan.R
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivityQrCodeBinding
import com.ids.librascan.databinding.ActivityQrDataBinding
import com.ids.librascan.db.QrCode
import com.ids.librascan.utils.AppHelper
import info.bideens.barcode.BarcodeReader
import utils.hide
import utils.show
import utils.toast

lateinit var barcodeReader: BarcodeReader
class QrCodeActivity : ActivityCompactBase(), BarcodeReader.BarcodeReaderListener {
    lateinit var qrCodeBinding: ActivityQrCodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        qrCodeBinding = ActivityQrCodeBinding.inflate(layoutInflater)
        setContentView(qrCodeBinding.root)

        barcodeReader = supportFragmentManager.findFragmentById(R.id.barcodeReader) as BarcodeReader
        barcodeReader.setListener(this)

    }
    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this@QrCodeActivity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@QrCodeActivity,
                arrayOf(Manifest.permission.CAMERA),
                1
            )
        } else { //permissions granted
            //show barcode
            qrCodeBinding.rlBarcode.show()
            barcodeReader.resumeScanning()
        }
    }
    override fun onBitmapScanned(sparseArray: SparseArray<Barcode>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onScannedMultiple(barcodes: MutableList<Barcode>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCameraPermissionDenied() {
        toast(AppHelper.getRemoteString("camera_error",this))
    }

    override fun onScanned(barcode: Barcode?) {
        val value = barcode.let { it!!.displayValue }
        if (barcode == null) {
            return
        }
        if (value == "") {
            return
        }
        // playing barcode reader beep sound
        barcodeReader.playBeep()
        barcodeReader.pauseScanning()
        this.runOnUiThread {
            qrCodeBinding.rlBarcode.hide()
            MyApplication.clientKey = value
            finish()
        }
    }
    override fun onBackPressed() {
        if (qrCodeBinding.rlBarcode.visibility == View.VISIBLE) {
            barcodeReader.pauseScanning()
            qrCodeBinding.rlBarcode.hide()
            super.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    override fun onScanError(errorMessage: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}