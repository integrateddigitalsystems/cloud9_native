package com.ids.librascan.controller.Activities

import Base.ActivityCompactBase
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import com.ids.librascan.R
import com.ids.librascan.databinding.ActivityScanBinding
import com.ids.librascan.databinding.PopDialogBinding
import com.ids.librascan.db.QrCode
import com.ids.librascan.db.QrCodeDatabase
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView
import utils.toast

class ActivityScan : ActivityCompactBase(), ZXingScannerView.ResultHandler {
    lateinit var activityScanBinding: ActivityScanBinding
    private var scannerView: ZXingScannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
        setContentView(scannerView)
      //  activityScanBinding = ActivityScanBinding.inflate(layoutInflater)
      //  setContentView(activityScanBinding.root)
        setPermission()

    }

    override fun handleResult(title: Result?) {
        showDialog(title.toString())
    }

   private fun showDialog(title: String?){
       val popDialogBinding = PopDialogBinding.inflate(layoutInflater)
        val mDialogView = popDialogBinding.root
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle(getString(R.string.save_data))
        val mAlertDialog = mBuilder.show()
       popDialogBinding.tVTitle.text = title.toString()
       popDialogBinding.btSave.setOnClickListener {

            launch {
                val qrCode = QrCode(title.toString())
                application?.let {
                    QrCodeDatabase(it).getUrlDao().insertUrl(qrCode)
                    toast(getString(R.string.qr_code))
                }
            }
            startActivity(Intent(this, ActivityScan::class.java))
            mAlertDialog.dismiss()

        }
       popDialogBinding.btCancel.setOnClickListener {
            startActivity(Intent(this, ActivityScan::class.java))
            mAlertDialog.dismiss()

        }
    }

    override fun onResume() {
        super.onResume()

        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }

    override fun onStop() {
        super.onStop()
        scannerView?.stopCamera()
        onBackPressed()
    }

    private fun setPermission() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    toast(getString(R.string.permission_message))
                }
            }
        }

    }

}

