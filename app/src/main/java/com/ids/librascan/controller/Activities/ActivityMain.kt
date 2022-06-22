package com.ids.librascan.controller.Activities


import Base.ActivityCompactBase
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.SparseArray
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.vision.barcode.Barcode
import com.ids.librascan.R
import com.ids.librascan.controller.Adapters.UnitsSpinnerAdapter
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivityMainBinding
import com.ids.librascan.databinding.ItemDropDownBinding
import com.ids.librascan.databinding.PopupBarcodeBinding
import com.ids.librascan.db.QrCode
import com.ids.librascan.db.QrCodeDatabase
import com.ids.librascan.db.Unit
import com.ids.librascan.utils.AppHelper
import info.bideens.barcode.BarcodeReader
import kotlinx.coroutines.launch
import utils.hide
import utils.show
import utils.toast

class ActivityMain : ActivityCompactBase(), BarcodeReader.BarcodeReaderListener {

    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private var spinnerUnits: ArrayList<Unit> = arrayListOf()
    private lateinit var spinnerAdapter: UnitsSpinnerAdapter
    lateinit var barcodeReader: BarcodeReader
    private lateinit var barcodeAlertDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        init()
    }

   /* private fun addUnit(){
        if (MyApplication.isFirst){
            launch {
                    QrCodeDatabase(application).getUnit().insertUnit(Unit("Kg"))
                    QrCodeDatabase(application).getUnit().insertUnit(Unit("box"))
                    QrCodeDatabase(application).getUnit().insertUnit(Unit("item"))
            }
            MyApplication.isFirst = false
        }
    }*/

    private fun init() {
        AppHelper.setAllTexts(activityMainBinding.rootMain, this)
      // addUnit()
       activityMainBinding.llScan.setOnClickListener {
           showAddBarcodeAlertDialog("",this)
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
       spinnerAdapter = UnitsSpinnerAdapter(this, spinnerUnits)

    }

    private fun createDialogLogout() {
        val builder = android.app.AlertDialog.Builder(this)
            .setCancelable(true)
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

    fun showAddBarcodeAlertDialog(barcode:String) {
        var quantity = 1
        var selectedUnit = Unit()
        spinnerUnits.clear()

        val builder = AlertDialog.Builder(this)
        val popupBarcodeBinding = PopupBarcodeBinding.inflate(layoutInflater)

        launch {
                spinnerUnits.addAll(QrCodeDatabase(application).getUnit().getUnitData())
                popupBarcodeBinding.spUnits.adapter = spinnerAdapter
        }
        popupBarcodeBinding.spUnits.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedUnit = parent?.getItemAtPosition(position) as Unit
            }
        }

        popupBarcodeBinding.ivScan.setOnClickListener {
            try {
                AppHelper.closeKeyboard(this@ActivityMain)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            checkCameraPermissions()
            barcodeAlertDialog.cancel()
        }
        popupBarcodeBinding.ivBarcode.setOnClickListener {
            popupBarcodeBinding.ivScan.performClick()
        }

        popupBarcodeBinding.etQty.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (arg0.isEmpty()) {
                    quantity = 1
                    popupBarcodeBinding.etQty.setText(quantity.toString())
                } else {
                    quantity = popupBarcodeBinding.etQty.text.toString().toInt()
                }
            }
            override fun beforeTextChanged(
                arg0: CharSequence, arg1: Int, arg2: Int,
                arg3: Int
            ) {
            }

            override fun afterTextChanged(arg0: Editable) {
            }
        })
        popupBarcodeBinding.etQty.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                when {
                    popupBarcodeBinding.etQty.text.isEmpty() -> {
                        quantity = 1
                        popupBarcodeBinding.etQty.setText(quantity.toString())
                    }
                    popupBarcodeBinding.etQty.text.toString() == "0" -> {
                        quantity = 1
                        popupBarcodeBinding.etQty.setText(quantity.toString())
                    }
                    else -> {
                        quantity = popupBarcodeBinding.etQty.text.toString().toInt()
                        popupBarcodeBinding.etQty.setText(popupBarcodeBinding.etQty.text.toString())
                    }
                }
            }
        }

        popupBarcodeBinding.tvDecrement.setOnClickListener {
            if (quantity > 1) {
                quantity -= 1
                try {
                    popupBarcodeBinding.etQty.setText(quantity.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        popupBarcodeBinding.tvIncrement.setOnClickListener {
            quantity += 1
            try {
                popupBarcodeBinding.etQty.setText(quantity.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        fun createDialog(message: String) {
            val builder = AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes))
                { dialog, _ ->
                    val itemDropDownBinding = ItemDropDownBinding.inflate(layoutInflater)
                    popupBarcodeBinding.tvInsert.hide()
                    popupBarcodeBinding.tvCode.isFocusable = false
                    itemDropDownBinding.tvItem.isFocusable = true
                    popupBarcodeBinding.spUnits.adapter = spinnerAdapter
                    popupBarcodeBinding.tvInsertClose.setText(AppHelper.getRemoteString("update",this))
                    dialog.cancel()
                }
                .setNegativeButton(getString(R.string.no))
                { dialog, _ ->
                   dialog.cancel()

                }
            val alert = builder.create()
            alert.show()
        }

        popupBarcodeBinding.tvCode.setText(barcode)
        fun insertCode (){
            launch {
                QrCodeDatabase(application).getCodeDao().insertCode(QrCode(popupBarcodeBinding.tvCode.text.toString().trim(),selectedUnit.id,quantity.toString().trim()))
                popupBarcodeBinding.tvCode.setText("")
                popupBarcodeBinding.etQty.setText("1")
                spinnerUnits.clear()
                spinnerUnits.addAll(QrCodeDatabase(application).getUnit().getUnitData())
                popupBarcodeBinding.spUnits.adapter = spinnerAdapter
            }
        }

        popupBarcodeBinding.tvInsertClose.setOnClickListener {
            if (popupBarcodeBinding.tvCode.text.toString() != ""  && selectedUnit.toString() != " " && quantity != 0) {
                launch {
                    if(popupBarcodeBinding.tvInsertClose.text == (getString(R.string.insert_and_close))){
                        val qrCodeQuery = QrCodeDatabase(application).getCodeDao().getCode(popupBarcodeBinding.tvCode.text.toString().trim(),selectedUnit.id)
                        if (qrCodeQuery !=null ) {
                            createDialog(AppHelper.getRemoteString("item_exist",this@ActivityMain) + "\n" + "\n" + AppHelper.getRemoteString("item_update",this@ActivityMain))
                            popupBarcodeBinding.tvCode.setText(qrCodeQuery.code)
                            popupBarcodeBinding.etQty.setText(qrCodeQuery.quantity)
                        } else {
                            insertCode()
                            toast(AppHelper.getRemoteString("item_save",this@ActivityMain))
                            barcodeAlertDialog.cancel()
                        }
                    }
                    else{
                        insertCode()
                        toast(AppHelper.getRemoteString("message_update",this@ActivityMain))
                        barcodeAlertDialog.cancel()
                    }
                }

            }
            else{
                when {
                    popupBarcodeBinding.tvCode.text.toString() == "" -> AppHelper.createDialogPositive(
                        this@ActivityMain,
                        AppHelper.getRemoteString("error_filled_barcode",this)
                    )
                    selectedUnit.toString() == " "-> AppHelper.createDialogPositive(
                        this@ActivityMain,
                        AppHelper.getRemoteString("error_filled_unit",this)
                    )
                    quantity == 0 -> AppHelper.createDialogPositive(
                        this@ActivityMain,
                        AppHelper.getRemoteString("error_filled_qty",this)
                    )
                }
            }

        }
        popupBarcodeBinding.tvInsert.setOnClickListener {
            if (popupBarcodeBinding.tvCode.text.toString() != ""  && selectedUnit.toString() != " " && quantity != 0) {
                launch {
                    if(popupBarcodeBinding.tvInsertClose.text == (getString(R.string.insert_and_close))){
                        val qrCodeQuery = QrCodeDatabase(application).getCodeDao().getCode(popupBarcodeBinding.tvCode.text.toString().trim(),selectedUnit.id)
                        if (qrCodeQuery !=null ) {
                            createDialog(AppHelper.getRemoteString("item_exist",this@ActivityMain) + "\n" + "\n" + AppHelper.getRemoteString("item_update",this@ActivityMain))
                            popupBarcodeBinding.tvCode.setText(qrCodeQuery.code)
                            popupBarcodeBinding.etQty.setText(qrCodeQuery.quantity)
                        } else {
                            insertCode()
                            toast(AppHelper.getRemoteString("item_save",this@ActivityMain))
                        }
                    }
                    else{
                        insertCode()
                        toast(AppHelper.getRemoteString("message_update",this@ActivityMain))
                        barcodeAlertDialog.cancel()
                    }
                }

            }
            else{
                when {
                    popupBarcodeBinding.tvCode.text.toString() == "" -> AppHelper.createDialogPositive(
                        this@ActivityMain,
                        AppHelper.getRemoteString("error_filled_barcode",this)
                    )
                    selectedUnit.toString() == " "-> AppHelper.createDialogPositive(
                        this@ActivityMain,
                        AppHelper.getRemoteString("error_filled_unit",this)
                    )
                    quantity == 0 -> AppHelper.createDialogPositive(
                        this@ActivityMain,
                        AppHelper.getRemoteString("error_filled_qty",this)
                    )
                }
            }
        }

        popupBarcodeBinding.btClose.setOnClickListener {
            barcodeAlertDialog.cancel()
        }
        AppHelper.overrideFonts(this@ActivityMain, popupBarcodeBinding.llPagerItem)
        builder.setView(popupBarcodeBinding.root)
        barcodeAlertDialog = builder.create()
        try {
            barcodeAlertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        barcodeAlertDialog.show()

    }

    private fun checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(
                this@ActivityMain,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this@ActivityMain,
                arrayOf(Manifest.permission.CAMERA),
                1
            )
        } else {
            activityMainBinding.rlBarcode.show()
            barcodeReader.resumeScanning()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //GRANTED
                activityMainBinding.rlBarcode.visibility = View.VISIBLE
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
            toast("Barcode: $value")
            activityMainBinding.rlBarcode.hide()
            showAddBarcodeAlertDialog(value,this)
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
    override fun onResume() {
        super.onResume()
        try {
            activityMainBinding.rlBarcode.let {

                if (it.visibility == View.VISIBLE) {
                    barcodeReader.resumeScanning()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onPause() {
        super.onPause()

        try {
            activityMainBinding.rlBarcode.let {

                if (it.visibility == View.VISIBLE) {
                    barcodeReader.pauseScanning()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}