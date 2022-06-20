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
import com.ids.librascan.databinding.PopupBarcodeBinding
import com.ids.librascan.db.QrCode
import com.ids.librascan.db.QrCodeDatabase
import com.ids.librascan.db.Unit
import com.ids.librascan.utils.AppHelper
import info.bideens.barcode.BarcodeReader
import kotlinx.coroutines.launch
import utils.hide
import utils.toast

class ActivityMain : ActivityCompactBase(), BarcodeReader.BarcodeReaderListener {
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private var spinnerUnits: ArrayList<Unit> = arrayListOf()
    private lateinit var spinnerAdapter: UnitsSpinnerAdapter
    lateinit var barcodeReader: BarcodeReader
    private lateinit var barcodeAlertDialog: AlertDialog
    var FromSearchRecycleAdapter = false
    private var arrQrCode: ArrayList<QrCode> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        init()

    }

    fun addUnit(){
        if (MyApplication.isFirst){
            launch {
                application?.let {
                    QrCodeDatabase(it).getUnit().insertUnit(Unit("Kg"))
                    QrCodeDatabase(it).getUnit().insertUnit(Unit("box"))
                    QrCodeDatabase(it).getUnit().insertUnit(Unit("item"))
                }
            }
            MyApplication.isFirst = false
        }
    }

   fun init() {
       addUnit()
       activityMainBinding.llScan.setOnClickListener {
           showAddBarcodeAlertDialog("")
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
           mGoogleSignInClient.signOut().addOnCompleteListener {
             //  AppHelper.createDialog(this@ActivityMain,"Do you logout")
               MyApplication.isLogin = false
               val intent= Intent(this, ActivityLogin::class.java)
               toast(getString(R.string.logout))
               startActivity(intent)
               finish()
           }
       }

       barcodeReader = supportFragmentManager.findFragmentById(R.id.barcodeReader) as BarcodeReader
       spinnerAdapter = UnitsSpinnerAdapter(this, spinnerUnits)

    }

    private fun showAddBarcodeAlertDialog(barcode:String) {
        var quantity = 1
        var selectedUnit = Unit()
        spinnerUnits.clear()
        var selected = QrCode()

        val builder = AlertDialog.Builder(this)
        val popupBarcodeBinding = PopupBarcodeBinding.inflate(layoutInflater)
        val dialogLayout = popupBarcodeBinding.root



        launch {
            application?.let {
                spinnerUnits.addAll(QrCodeDatabase(it).getUnit().getUnitData())
                popupBarcodeBinding.spUnits.adapter = spinnerAdapter
            }
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
        popupBarcodeBinding.tvTitleCode.setText(barcode)
        popupBarcodeBinding.tvInsertClose.setOnClickListener {
            launch {
                val qrCode = QrCode(popupBarcodeBinding.tvTitleCode.text.toString(),quantity.toString(),selectedUnit.title)
                application?.let {
                    QrCodeDatabase(it).getUrlDao().insertUrl(qrCode)
                    toast(getString(R.string.qr_code))
                }
            }
            barcodeAlertDialog.cancel()
        }

        launch {
            arrQrCode.addAll(QrCodeDatabase(application).getUrlDao().getAllUrl())

        }
        popupBarcodeBinding.tvInsert.setOnClickListener {
            for (item in arrQrCode){
                if (popupBarcodeBinding.tvTitleCode.text.toString() == item.title && selectedUnit.title == item.unitId){
                    AppHelper.createDialog(
                        this@ActivityMain,
                        "item is exist" + "\n" + "\n" + "Do you want update this record!"
                    )
                    popupBarcodeBinding.tvInsert.visibility= View.INVISIBLE
                    popupBarcodeBinding.tvInsertClose.visibility= View.INVISIBLE
                    popupBarcodeBinding.tvUpdate.visibility= View.VISIBLE
                    popupBarcodeBinding.etQty.setText(item.quantity)
                }
                else{
                    if (!popupBarcodeBinding.tvTitleCode.equals("") && !selectedUnit.equals("") && quantity != 0){
                        /* launch {
                             val qrCode = QrCode(popupBarcodeBinding.tvTitleCode.text.toString(),popupBarcodeBinding.etQty.text.toString(),selectedUnit.title)
                             application?.let {
                                 QrCodeDatabase(it).getUrlDao().insertUrl(qrCode)
                                 toast(getString(R.string.qr_code))
                             }
                         }*/
                        /* quantity = 1
                         tvTitleCode.setText("")
                         etQty.setText(quantity.toString())
                         spinnerUnits.clear()*/
                        //  spinnerAdapter.notifyDataSetChanged()
                    }
                    else{
                        when {
                            popupBarcodeBinding.tvTitleCode.equals("") -> AppHelper.createDialog(
                                this@ActivityMain,
                                resources.getString(R.string.error_filled_title)
                            )
                            selectedUnit.equals("")-> AppHelper.createDialog(
                                this@ActivityMain,
                                resources.getString(R.string.error_filled_unit)
                            )
                            quantity == 0 -> AppHelper.createDialog(
                                this@ActivityMain,
                                resources.getString(R.string.error_filled_qty)
                            )
                        }
                    }
                }

            }



        }
        popupBarcodeBinding.btClose.setOnClickListener {
            barcodeAlertDialog.cancel()
        }

        AppHelper.overrideFonts(this@ActivityMain, popupBarcodeBinding.llPagerItem)
        builder.setView(dialogLayout)
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
        } else { //permissions granted

            //show barcode
            activityMainBinding.rlBarcode.visibility = View.VISIBLE
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

                activityMainBinding.rlBarcode.visibility = View.VISIBLE
                barcodeReader.resumeScanning()
            } else {
                toast(getString(R.string.camera_error))
            }
        }
    }

    override fun onScanned(barcode: Barcode?) {
        val value = barcode.let { it!!.displayValue }
        barcodeReader.playBeep()
        barcodeReader.pauseScanning()

        this.runOnUiThread {
            toast("Barcode: $value")
            activityMainBinding.rlBarcode.visibility = View.GONE
            showAddBarcodeAlertDialog(value)
//            tvTitleCode.setText(value)
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
        toast(getString(R.string.camera_error))
    }

    override fun onBackPressed() {
        if (activityMainBinding.rlBarcode.visibility == View.VISIBLE) {

            barcodeReader.pauseScanning()

            activityMainBinding.rlBarcode.visibility = View.GONE
           // showAddQrCodeAlertDialog()

        } else {
            super.onBackPressed()
        }
    }

    fun back(v: View) {
        onBackPressed()
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