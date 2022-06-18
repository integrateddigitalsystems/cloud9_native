package com.ids.librascan.controller.Activities

import Base.ActivityCompactBase
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import com.ids.librascan.R
import com.ids.librascan.controller.Adapters.AdapterQrCode
import com.ids.librascan.controller.Adapters.UnitsSpinnerAdapter
import com.ids.librascan.databinding.ActivityScanBinding
import com.ids.librascan.databinding.PopupBarcodeBinding
import com.ids.librascan.db.QrCode
import com.ids.librascan.db.QrCodeDatabase
import com.ids.librascan.db.Unit
import com.ids.librascan.utils.AppHelper
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView
import utils.toast

class ActivityScan : ActivityCompactBase(), ZXingScannerView.ResultHandler {
    lateinit var activityScanBinding: ActivityScanBinding
    private var scannerView: ZXingScannerView? = null
    private var spinnerUnits: ArrayList<Unit> = arrayListOf()
    private lateinit var spinnerAdapter: UnitsSpinnerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)

        setContentView(scannerView)
        setPermission()
        init()
    }

    override fun handleResult(title: Result?) {
       // toast(title.toString())
       // showAddQrCodeAlertDialog(title.toString())
    }

    fun init() {
      //  addUnit()
        setPermission()
    }




            private fun showAddQrCodeAlertDialog(title: String?) {
                var quantity = 1
                //  spinnerUnits.clear()
                val popupBarcodeBinding = PopupBarcodeBinding.inflate(layoutInflater)
                val dialogLayout = popupBarcodeBinding.root
                val mBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
                    .setView(dialogLayout)
                val mAlertDialog = mBuilder.show()

                val btClose = dialogLayout.findViewById(R.id.btClose) as Button
                val ivScan = dialogLayout.findViewById(R.id.ivScan) as ImageView
                val spUnits = dialogLayout.findViewById(R.id.spUnits) as Spinner
                val ivBarcode = dialogLayout.findViewById(R.id.ivBarcode) as ImageView

                val tvTitleCode = dialogLayout.findViewById(R.id.tvTitleCode) as AutoCompleteTextView
                val tvTitle = dialogLayout.findViewById(R.id.tvTitle) as TextView
                val tvDecrement = dialogLayout.findViewById(R.id.tvDecrement) as TextView
                val tvIncrement = dialogLayout.findViewById(R.id.tvIncrement) as TextView
                val etQty = dialogLayout.findViewById(R.id.etQty) as EditText
                val tvInsert = dialogLayout.findViewById(R.id.tvInsert) as TextView
                val tvInsertClose = dialogLayout.findViewById(R.id.tvInsertClose) as TextView



                launch {
                    application?.let {
                        val unit = QrCodeDatabase(it).getUnit().getUnitData()
                        val arrayAdapter = ArrayAdapter(
                            this@ActivityScan,
                            android.R.layout.simple_spinner_dropdown_item,
                            unit
                        )
                        spUnits.adapter = arrayAdapter
                    }



                }


                ivScan.setOnClickListener {

                    try {
                        AppHelper.closeKeyboard(this@ActivityScan)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    startActivity(Intent(this, ActivityScan::class.java))
                    mAlertDialog.dismiss()
                }

                ivBarcode.setOnClickListener {
                    ivScan.performClick()
                }

                tvTitleCode.clearFocus()
                tvTitleCode.threshold = 1


                spUnits.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {

                       val selectedUnit = parent?.getItemAtPosition(position) as Unit


                    }
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

                tvInsertClose.setOnClickListener {
                    launch {
                        // val qrCode = QrCode(title.toString(),quantity,Uni)
                        application?.let {
                            // QrCodeDatabase(it).getUrlDao().insertUrl(qrCode)
                            toast(getString(R.string.qr_code))
                        }
                    }
                    startActivity(Intent(this, ActivityMain::class.java))
                    finish()
                }
                //tvTitleCode.setText(title)
                tvInsert.setOnClickListener {
                    launch {
                         val qrCode = QrCode(title.toString(),quantity.toString(),"kg")
                        application?.let {
                              QrCodeDatabase(it).getUrlDao().insertUrl(qrCode)
                            toast(getString(R.string.qr_code))
                        }
                    }
                   // tvTitleCode.clearFocus()
                    showAddQrCodeAlertDialog(title)

                }

                btClose.setOnClickListener {

                    startActivity(Intent(this, ActivityMain::class.java))
                    finish()
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


