package com.ids.librascan.controller.Activities


import Base.ActivityCompactBase
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.ids.librascan.R
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivityMainBinding
import com.ids.librascan.databinding.PopupBarcodeBinding
import com.ids.librascan.db.QrCode
import com.ids.librascan.db.QrCodeDatabase
import com.ids.librascan.db.Unit
import com.ids.librascan.utils.AppHelper
import kotlinx.coroutines.launch
import utils.toast

class ActivityMain : ActivityCompactBase() {
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private var spinnerUnits: ArrayList<String> = arrayListOf()
    var FromSearchRecycleAdapter = false
    private var allSearchProducts: java.util.ArrayList<QrCode> = arrayListOf()
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
           showAddQrCodeAlertDialog("")
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
               MyApplication.isLogin = false
               val intent= Intent(this, ActivityLogin::class.java)
               toast(getString(R.string.logout))
               startActivity(intent)
               finish()
           }
       }

    }
    private fun showAddQrCodeAlertDialog(barcode : String) {
        var quantity = 1
        var selectedUnit = ""
        val popupBarcodeBinding = PopupBarcodeBinding.inflate(layoutInflater)
        val dialogLayout = popupBarcodeBinding.root
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setView(dialogLayout)
        val mAlertDialog = mBuilder.show()

        val btClose = dialogLayout.findViewById(R.id.btClose) as Button
        val ivScan = dialogLayout.findViewById(R.id.ivScan) as ImageView
        val spUnits = dialogLayout.findViewById(R.id.spUnits) as Spinner
        val ivBarcode = dialogLayout.findViewById(R.id.ivBarcode) as ImageView
        val tvTitleCode = dialogLayout.findViewById(R.id.tvTitleCode) as AutoCompleteTextView
        val tvDecrement = dialogLayout.findViewById(R.id.tvDecrement) as TextView
        val tvIncrement = dialogLayout.findViewById(R.id.tvIncrement) as TextView
        val etQty = dialogLayout.findViewById(R.id.etQty) as EditText
        val tvInsert = dialogLayout.findViewById(R.id.tvInsert) as TextView
        val tvInsertClose = dialogLayout.findViewById(R.id.tvInsertClose) as TextView

        val llRvSearchProduct = dialogLayout.findViewById(R.id.llRvSearchProduct) as LinearLayout


        tvTitleCode.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (FromSearchRecycleAdapter) {
                    FromSearchRecycleAdapter = false
                } else if (!tvTitleCode.isPerformingCompletion) {

                    if (arg0.isEmpty()) {

                        try {
                            AppHelper.closeKeyboard(this@ActivityMain)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        spinnerUnits.clear()
                        allSearchProducts.clear()

                    }
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


        spinnerUnits.clear()
        launch {
            application?.let {
                spinnerUnits.addAll(QrCodeDatabase(it).getUnit().getUnitData())
                val arrayAdapter = ArrayAdapter(
                    this@ActivityMain,
                    android.R.layout.simple_spinner_dropdown_item,
                    spinnerUnits
                )
                spUnits.adapter = arrayAdapter
            }
        }
        spUnits.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedUnit = spinnerUnits[position]
            }
        }

        ivScan.setOnClickListener {
            mAlertDialog.dismiss()
        }
        ivBarcode.setOnClickListener {
            ivScan.performClick()
        }

        etQty.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

                if (arg0.isEmpty()) {
                    quantity = 1
                    etQty.setText(quantity.toString())
                } else {

                    quantity = etQty.text.toString().toInt()
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
        etQty.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                when {
                    etQty.text.isEmpty() -> {
                        quantity = 1
                        etQty.setText(quantity.toString())
                    }
                    etQty.text.toString() == "0" -> {
                        quantity = 1
                        etQty.setText(quantity.toString())
                    }
                    else -> {
                        quantity = etQty.text.toString().toInt()
                        etQty.setText(etQty.text.toString())
                    }
                }
            }
        }

        tvDecrement.setOnClickListener {
            if (quantity > 1) {
                quantity -= 1
                try {
                    etQty.setText(quantity.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        tvIncrement.setOnClickListener {
            quantity += 1
            try {
                etQty.setText(quantity.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        tvTitleCode.setText(barcode)
        tvInsertClose.setOnClickListener {
            launch {
                val qrCode = QrCode(tvTitleCode.text.toString(),quantity.toString(),selectedUnit)
                application?.let {
                    QrCodeDatabase(it).getUrlDao().insertUrl(qrCode)
                    toast(getString(R.string.qr_code))
                }
            }
            startActivity(Intent(this, ActivityMain::class.java))
            finish()
        }
        tvInsert.setOnClickListener {
            launch {
                val qrCode = QrCode(tvTitleCode.text.toString(),quantity.toString(),selectedUnit)
                application?.let {
                    QrCodeDatabase(it).getUrlDao().insertUrl(qrCode)
                    toast(getString(R.string.qr_code))
                }
            }
           /* tvTitleCode.setText("")
            etQty.setText("")
            spinnerUnits.clear()*/
        }

        btClose.setOnClickListener {
            startActivity(Intent(this, ActivityMain::class.java))
            finish()
        }
    }

   /* override fun handleResult(p0: Result?) {
        toast(getString(R.string.barcode) + ":" +p0.toString())
        showAddQrCodeAlertDialog(p0.toString())
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

    }*/

   /* private fun checkCameraPermissions() {
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
            scannerView = ZXingScannerView(this)
            scannerView?.setResultHandler(this)
            scannerView?.startCamera()
            setContentView(scannerView)
        }
    }*/
}