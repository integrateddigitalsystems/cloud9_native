package com.ids.librascan.controller.Activities


import Base.ActivityCompactBase
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.SparseArray
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.vision.barcode.Barcode
import com.google.firebase.firestore.FirebaseFirestore
import com.ids.librascan.R
import com.ids.librascan.apis.RetrofitClient
import com.ids.librascan.apis.RetrofitInterface
import com.ids.librascan.controller.Adapters.OnInsertUpdate.OnInsertUpdate
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivityMainBinding
import com.ids.librascan.databinding.PopupLanguageBinding
import com.ids.librascan.db.QrCode
import com.ids.librascan.db.QrCodeDatabase
import com.ids.librascan.db.Unit
import com.ids.librascan.db.Warehouse
import com.ids.librascan.model.ResponseGetWareHouse
import com.ids.librascan.utils.AppHelper
import info.bideens.barcode.BarcodeReader
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.*
import java.util.*
import kotlin.collections.ArrayList


class ActivityMain : ActivityCompactBase(), BarcodeReader.BarcodeReaderListener, OnInsertUpdate, CompoundButton.OnCheckedChangeListener {
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var popupLanguageBinding : PopupLanguageBinding
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var barcodeReader: BarcodeReader
    private lateinit var languageAlertDialog: androidx.appcompat.app.AlertDialog
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
        addWarehouse()
        AppHelper.setAllTexts(activityMainBinding.rootMain, this)
        activityMainBinding.llScan.setOnClickListener {
           showAddBarcodeAlertDialog(this,false, QrCode(),this)
       }
       activityMainBinding.llSession.setOnClickListener {
           startActivity(Intent(this, ActivitySessions::class.java))
       }

       val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
           .requestIdToken(getString(R.string.default_web_client_id_auth))
           .requestEmail()
           .build()
       mGoogleSignInClient= GoogleSignIn.getClient(this,gso)

       activityMainBinding.iVMore.setOnClickListener {
           val popupMenu = PopupMenu(this,it)
           popupMenu.setOnMenuItemClickListener { item->
               when(item.itemId){
                   R.id.action_logout ->{
                       createDialogLogout()
                       true
                   }
                   R.id.action_language ->{
                       createDialogLanguage()
                       true
                   }
                   R.id.action_settings ->{
                       startActivity(Intent(this, ActivitySettings::class.java))
                       true
                   }
                   else ->false
               }
           }
           popupMenu.inflate(R.menu.menu)
           popupMenu.show()
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
                activityMainBinding.loading.show()
                docData.put("QrCode", arrayQrcode)
                db!!.collection("Data")
                    .add(docData)
                    .addOnSuccessListener { documentReference ->
                        toast(AppHelper.getRemoteString("success_added",this@ActivityMain))
                        wtf("DocumentSnapshot written with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        wtf("Error adding document$e")
                    }
                Handler(Looper.getMainLooper()).postDelayed({
                    activityMainBinding.loading.hide()
                }, 500)


                QrCodeDatabase(application).getCodeDao().deleteAllCode()
            }
            else{
                toast(AppHelper.getRemoteString("added_faild",this@ActivityMain))
                activityMainBinding.loading.hide()
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

    fun addWarehouse(){
     //   getWarehouse()

    }

    private fun getWarehouse() {
        RetrofitClient.client?.create(RetrofitInterface::class.java)
            ?.getWarehouse()?.enqueue(object :
                Callback<ResponseGetWareHouse> {
                override fun onResponse(
                    call: Call<ResponseGetWareHouse>,
                    response: Response<ResponseGetWareHouse>
                ) {
                    if (response.isSuccessful && response.body()!!.wareHouses!!.size>0) {

                        for (item in response.body()!!.wareHouses!!) {
                            launch {
                                QrCodeDatabase(application).getWarehouse().insertWarehouse(
                                    Warehouse(
                                    item.id!!,item.name.toString())
                                )
                            }
                        }

                    } else {
                        toast("Faild")
                    }
                }
                override fun onFailure(call: Call<ResponseGetWareHouse>, t: Throwable) {

                }
            })
    }

    private fun createDialogLogout() {
        val builder = AlertDialog.Builder(this)
            .setCancelable(true)
            .setMessage(AppHelper.getRemoteString("logout_message",this))
            .setPositiveButton(AppHelper.getRemoteString("yes",this))
            { dialog, _ ->
                mGoogleSignInClient.signOut().addOnCompleteListener {
                    MyApplication.isLogin = false
                    val intent= Intent(this, ActivityLogin::class.java)
                   // toast(AppHelper.getRemoteString("logout",this))
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

    private fun createDialogLanguage(){
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        popupLanguageBinding = PopupLanguageBinding.inflate(layoutInflater)
        AppHelper.setAllTexts(popupLanguageBinding.llLanguage, this)
        builder.setView(popupLanguageBinding.root)
        if (MyApplication.languageCode == "en")
            popupLanguageBinding.rbEnglish.isChecked = true
        else popupLanguageBinding.rbArabic.isChecked =true

        popupLanguageBinding.rbEnglish.setOnCheckedChangeListener(this)
        popupLanguageBinding.rbArabic.setOnCheckedChangeListener(this)

        languageAlertDialog = builder.create()
        languageAlertDialog.show()
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
            if (MyApplication.enableInsert){
                 popupBarcodeBinding.tvCode.setText(value)
                  insert(this)
            }
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

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when {
            buttonView!!.id == R.id.rbArabic -> {
                if (isChecked) {
                    AppHelper.changeLanguage(this,"ar")
                    startActivity(Intent(this@ActivityMain, ActivityMain::class.java))
                    finish()
                }
            }
            buttonView.id == R.id.rbEnglish -> {

                if (isChecked) {
                    AppHelper.changeLanguage(this,"en")
                    startActivity(Intent(this@ActivityMain, ActivityMain::class.java))
                    finish()
                }
            }
        }
    }

}