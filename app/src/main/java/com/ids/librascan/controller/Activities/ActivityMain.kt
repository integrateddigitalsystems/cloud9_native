package com.ids.librascan.controller.Activities


import Base.ActivityCompactBase
import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.vision.barcode.Barcode
import com.google.firebase.firestore.FirebaseFirestore
import com.ids.librascan.R
import com.ids.librascan.controller.Adapters.OnInsertUpdate
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivityMainBinding
import com.ids.librascan.db.QrCode
import com.ids.librascan.db.QrCodeDatabase
import com.ids.librascan.db.Unit
import com.ids.librascan.utils.AppHelper
import info.bideens.barcode.BarcodeReader
import kotlinx.coroutines.launch
import utils.hide
import utils.show
import utils.toast
import utils.wtf
import java.util.*
import kotlin.collections.ArrayList


class ActivityMain : ActivityCompactBase(), BarcodeReader.BarcodeReaderListener,OnInsertUpdate{
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var barcodeReader: BarcodeReader
    private var db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        init()
    }

    override fun onInsertUpdate(boolean: Boolean) {

    }

    private fun init() {
        db = FirebaseFirestore.getInstance()
        addUnit()
        AppHelper.setAllTexts(activityMainBinding.rootMain, this)
        activityMainBinding.llScan.setOnClickListener {
           showAddBarcodeAlertDialog(this,false, QrCode(),this)
       }
       activityMainBinding.llData.setOnClickListener {
           startActivity(Intent(this, ActivityQrData::class.java))
       }

       val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
           .requestIdToken(getString(R.string.default_web_client_id_auth))
           .requestEmail()
           .build()
       mGoogleSignInClient= GoogleSignIn.getClient(this,gso)

       activityMainBinding.iVLogout.setOnClickListener {
           createDialogLogout()
       }

        barcodeReader = supportFragmentManager.findFragmentById(R.id.barcodeReader) as BarcodeReader

        activityMainBinding.llSync.setOnClickListener {
            addDataToFirestore()
        }


    }


    fun addDataToFirestore(){
        launch {
            val arrayQrcode = ArrayList<QrCode>()
            val docData: MutableMap<String, Any> = HashMap()
            arrayQrcode.addAll(QrCodeDatabase(application).getCodeDao().getAllCode())
            if (arrayQrcode.isNotEmpty()){
                docData.put("QrCode", arrayQrcode)
                db!!.collection("Data")
                    .add(docData)
                    .addOnSuccessListener { documentReference ->
                        toast(AppHelper.getRemoteString("sucess_added",this@ActivityMain))
                        wtf("DocumentSnapshot written with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        wtf("Error adding document" + e)
                    }

                QrCodeDatabase(application).getCodeDao().deleteAllCode()
            }
            else{
                toast(AppHelper.getRemoteString("added_faild",this@ActivityMain))
            }
            }


    }


    fun addUnit(){
        if (MyApplication.isFirst){
            launch {
                QrCodeDatabase(application).getUnit().insertUnit(Unit("Kg"))
                QrCodeDatabase(application).getUnit().insertUnit(Unit("box"))
                QrCodeDatabase(application).getUnit().insertUnit(Unit("item"))
            }
            MyApplication.isFirst = false
        }
    }

    private fun createDialogLogout() {
        val builder = android.app.AlertDialog.Builder(this)
            .setCancelable(true)
            .setMessage(AppHelper.getRemoteString("logout_message",this))
            .setPositiveButton(AppHelper.getRemoteString("yes",this))
            { dialog, _ ->
                mGoogleSignInClient.signOut().addOnCompleteListener {
                    MyApplication.isLogin = false
                    val intent= Intent(this, ActivityLogin::class.java)
                    toast(AppHelper.getRemoteString("logout",this))
                    startActivity(intent)
                    finish()
                }
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
        if (ContextCompat.checkSelfPermission(this@ActivityMain,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@ActivityMain,
                arrayOf(Manifest.permission.CAMERA),
                1
            )
        } else { //permissions granted
            //show barcode
            activityMainBinding.rlBarcode.show()
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
                activityMainBinding.rlBarcode.show()
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
            activityMainBinding.rlBarcode.hide()
          showAddBarcodeAlertDialog(this,false,QrCode(value),this)
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
        if (activityMainBinding.rlBarcode.visibility == View.VISIBLE) {
            barcodeReader.pauseScanning()
            activityMainBinding.rlBarcode.hide()
        } else {
            super.onBackPressed()
        }
    }



}