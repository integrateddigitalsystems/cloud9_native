package com.ids.librascan.controller.Activities

import Base.ActivityCompactBase
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.SparseArray
import android.view.View
import android.widget.AdapterView
import android.widget.DatePicker
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.vision.barcode.Barcode
import com.google.firebase.firestore.FirebaseFirestore
import com.ids.librascan.R
import com.ids.librascan.controller.Adapters.AdapterSession
import com.ids.librascan.controller.Adapters.OnInsertUpdate.OnInsertUpdate
import com.ids.librascan.controller.Adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.librascan.controller.Adapters.WarehouseSpinnerAdapter
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivitySessionsBinding
import com.ids.librascan.databinding.PopupSessionBinding
import com.ids.librascan.db.*
import com.ids.librascan.utils.AppHelper
import info.bideens.barcode.BarcodeReader
import kotlinx.coroutines.launch
import utils.hide
import utils.show
import utils.toast
import utils.wtf
import java.util.*


class ActivitySessions : ActivityCompactBase(), RVOnItemClickListener, OnInsertUpdate,
    BarcodeReader.BarcodeReaderListener, DatePickerDialog.OnDateSetListener{
    lateinit var activitySessionsBinding: ActivitySessionsBinding
    lateinit var popupSessionBinding: PopupSessionBinding
    private lateinit var sessionAlertDialog: androidx.appcompat.app.AlertDialog
    private var spinnerWarehouse: ArrayList<Warehouse> = arrayListOf()
    lateinit var warehouseSpinnerAdapter: WarehouseSpinnerAdapter
    lateinit var adapterSession: AdapterSession
    private var arrSession = ArrayList<Sessions>()
    var selectedWarehouse = Warehouse()
    lateinit var barcodeReader: BarcodeReader
    private var db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySessionsBinding = ActivitySessionsBinding.inflate(layoutInflater)
        setContentView(activitySessionsBinding.root)
        popupSessionBinding = PopupSessionBinding.inflate(layoutInflater)
        init()
    }
    fun init(){
        db = FirebaseFirestore.getInstance()
        AppHelper.setAllTexts(activitySessionsBinding.rootSessions, this)
        activitySessionsBinding.llAddSession.setOnClickListener {
            showAddSessionAlertDialog()
        }

        activitySessionsBinding.llSync.setOnClickListener {
           createDialSyncAllData(AppHelper.getRemoteString("sync_all",this))
        }
        activitySessionsBinding.loading.show()
        launch {
            arrSession.addAll(QrCodeDatabase(application).getSessions().getSessions())
            activitySessionsBinding.loading.hide()
            setData()
        }
        barcodeReader = supportFragmentManager.findFragmentById(R.id.barcodeReader) as BarcodeReader

    }
    fun back(v: View) {
        onBackPressed()
    }

    private fun showAddSessionAlertDialog() {
        spinnerWarehouse.clear()
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        AppHelper.setAllTexts(popupSessionBinding.llPagerSession, this)

        popupSessionBinding = PopupSessionBinding.inflate(layoutInflater)

        warehouseSpinnerAdapter = WarehouseSpinnerAdapter(this,spinnerWarehouse)
        launch {
            spinnerWarehouse.addAll(QrCodeDatabase(application).getWarehouse().getWarehouse())
            popupSessionBinding.spWarehouse.adapter = warehouseSpinnerAdapter
            popupSessionBinding.spWarehouse.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedWarehouse = parent?.getItemAtPosition(position) as Warehouse

                    }
                }
        }

        popupSessionBinding.tvInsert.setOnClickListener {
            insertSession(this)
        }
        popupSessionBinding.btClose.setOnClickListener {
            sessionAlertDialog.cancel()
        }
        popupSessionBinding.tvDate.setOnClickListener {
            datePickerDialog()
        }
        builder.setView(popupSessionBinding.root)
        sessionAlertDialog = builder.create()
        try {
            sessionAlertDialog.show()
            sessionAlertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (e: Exception) {

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun insertSession(c:Activity){
        if (popupSessionBinding.tvName.text.toString()!="" && popupSessionBinding.spWarehouse.isNotEmpty() && popupSessionBinding.tvDescription.text.toString()!="" && popupSessionBinding.tvDate.text.toString()!=""){
            launch {
                var sessions = Sessions()
                sessions = QrCodeDatabase(application).getSessions().getSession(selectedWarehouse.name)
                if (sessions!=null){
                   AppHelper.createDialogPositive(this@ActivitySessions,AppHelper.getRemoteString("warehouse_session_exist",this@ActivitySessions))
                }
                else{
                    QrCodeDatabase(application).getSessions().insertSessions(Sessions(popupSessionBinding.tvName.text.toString(),selectedWarehouse.name,popupSessionBinding.tvDate.text.toString(),popupSessionBinding.tvDescription.text.toString()))
                    popupSessionBinding.tvName.setText("")
                    popupSessionBinding.tvDescription.setText("")
                    spinnerWarehouse.clear()
                    spinnerWarehouse.addAll(QrCodeDatabase(application).getWarehouse().getWarehouse())
                    popupSessionBinding.spWarehouse.adapter = warehouseSpinnerAdapter
                    sessionAlertDialog.cancel()
                    arrSession.clear()
                    arrSession.addAll(QrCodeDatabase(application).getSessions().getSessions())
                    adapterSession.notifyDataSetChanged()
                    toast(AppHelper.getRemoteString("session_save", c))
                }

            }
        }
        else checkValid(this)
    }

    private fun checkValid(c: Activity){
        when {
            popupSessionBinding.tvName.text.toString() == "" -> AppHelper.createDialogPositive(c,
                AppHelper.getRemoteString("error_filled_session_name", this)
            )
            popupSessionBinding.spWarehouse.isEmpty()-> AppHelper.createDialogPositive(c,
                AppHelper.getRemoteString("error_filled_warehouse", this)
            )
            popupSessionBinding.tvDate.text.toString() == "" -> AppHelper.createDialogPositive(c,
                AppHelper.getRemoteString("error_filled_date", this)
            )
            popupSessionBinding.tvDescription.text.toString() == "" -> AppHelper.createDialogPositive(c,
                AppHelper.getRemoteString("error_filled_description", this)
            )

        }
    }
    fun datePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            this,
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000)
        datePickerDialog.show()
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        popupSessionBinding.tvDate.text = "" +dayOfMonth + "/" + (month + 1)+ "/" +year
    }

    fun showWarehouse(view: View){
        popupSessionBinding.spWarehouse.performClick()
    }

    private fun setData() {
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        activitySessionsBinding.rVSessions.layoutManager = layoutManager
        adapterSession = AdapterSession(arrSession, this)
        activitySessionsBinding.rVSessions.adapter = adapterSession
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onItemClicked(view: View, position: Int) {
        if (view.id == R.id.llScan){
            MyApplication.isScan =false
            MyApplication.sessionId = arrSession[position].id
            MyApplication.sessionName = arrSession[position].sessionName
            if (MyApplication.enableInsert && !MyApplication.enableNewLine)
               activitySessionsBinding.btShowScan.performClick()
            else if (MyApplication.enableInsert && MyApplication.enableNewLine)
                activitySessionsBinding.btShowScan.performClick()

            else showAddBarcodeAlertDialog(this, false, QrCode(), this,true,arrSession[position])
            activitySessionsBinding.tvNameSession.text = arrSession[position].sessionName
        }
        else{
            if (view.id == R.id.llSync){
                createDialSync(AppHelper.getRemoteString("sync_data",this),position)
            }
            else if (view.id == R.id.tVDelete){
                launch {
                    QrCodeDatabase(application).getCodeDao().deleteCode(arrSession[position].id)
                    QrCodeDatabase(application).getSessions().deleteSession(arrSession[position].id)
                    arrSession.remove(arrSession[position])
                    adapterSession.notifyDataSetChanged()
                }
            }
            else{
                startActivity(Intent(this@ActivitySessions, ActivityQrData::class.java))
                MyApplication.sessionId = arrSession[position].id
                MyApplication.sessionName = arrSession[position].sessionName
                finish()
            }
        }
    }

    override fun onInsertUpdate(boolean: Boolean) {
    }

    fun addDataToFirestore(position: Int){
        launch {
            val arrayQrcode = ArrayList<QrCode>()
            val docData: MutableMap<String, Any> = HashMap()
            arrayQrcode.addAll(QrCodeDatabase(application).getCodeDao().getCode(arrSession[position].id))
            if (arrayQrcode.isNotEmpty()){
                activitySessionsBinding.loadingData.show()
                docData.put("QrCode", arrayQrcode)
                db!!.collection("Data")
                    .add(docData)
                    .addOnSuccessListener { documentReference ->
                        toast(AppHelper.getRemoteString("success_added",this@ActivitySessions))
                        wtf("DocumentSnapshot written with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        wtf("Error adding document$e")
                    }
                Handler(Looper.getMainLooper()).postDelayed({
                    activitySessionsBinding.loadingData.hide()
                }, 500)
                QrCodeDatabase(application).getCodeDao().deleteCode(arrSession[position].id)
                QrCodeDatabase(application).getSessions().deleteSession(arrSession[position].id)
                arrSession.remove(arrSession[position])
                adapterSession.notifyDataSetChanged()

            }
            else{
                toast(AppHelper.getRemoteString("added_faild",this@ActivitySessions))
                activitySessionsBinding.loadingData.hide()
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAllDataToFirestore(){
        launch {
            val arrayQrcode = ArrayList<QrCode>()
            val docData: MutableMap<String, Any> = HashMap()
            arrayQrcode.addAll(QrCodeDatabase(application).getCodeDao().getAllCode())
            if (arrayQrcode.isNotEmpty()){
                activitySessionsBinding.loadingData.show()
                docData.put("QrCode", arrayQrcode)
                db!!.collection("Data")
                    .add(docData)
                    .addOnSuccessListener { documentReference ->
                        toast(AppHelper.getRemoteString("success_added",this@ActivitySessions))
                        wtf("DocumentSnapshot written with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        wtf("Error adding document$e")
                    }
                Handler(Looper.getMainLooper()).postDelayed({
                    activitySessionsBinding.loadingData.hide()
                }, 500)
                QrCodeDatabase(application).getCodeDao().deleteAllCode()
                QrCodeDatabase(application).getSessions().deleteAllSession()
                arrSession.clear()
                adapterSession.notifyDataSetChanged()

            }
            else{
                toast(AppHelper.getRemoteString("added_faild",this@ActivitySessions))
                activitySessionsBinding.loadingData.hide()
            }
        }

    }

    fun checkCameraPermissions(view: View) {
        if (ContextCompat.checkSelfPermission(this@ActivitySessions,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@ActivitySessions,
                arrayOf(Manifest.permission.CAMERA),
                1
            )
        } else { //permissions granted
            //show barcode
            activitySessionsBinding.rlBarcode.show()
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
                activitySessionsBinding.rlBarcode.show()
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
            activitySessionsBinding.rlBarcode.hide()
            if (MyApplication.enableInsert && !MyApplication.enableNewLine){
                insertScanAuto(QrCode(value,0,1,MyApplication.sessionId), this,this)
            }

            else  if (MyApplication.enableInsert && MyApplication.enableNewLine){
                insertScan(QrCode(value,0,1,MyApplication.sessionId),this,this)
            }
            else{
                barcodeAlertDialog.show()
                popupBarcodeBinding.tvCode.setText(value)
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
        if (activitySessionsBinding.rlBarcode.visibility == View.VISIBLE) {
            barcodeReader.pauseScanning()
            activitySessionsBinding.rlBarcode.hide()
        } else {
            super.onBackPressed()
        }
    }

    private fun createDialSync(message: String,position: Int) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton(AppHelper.getRemoteString("yes", this))
            { dialog, _ ->
                addDataToFirestore(position)
                dialog.cancel()
            }
            .setNegativeButton(AppHelper.getRemoteString("no", this))
            { dialog, _ ->
                dialog.cancel()

            }
        val alert = builder.create()
        alert.show()
    }

    private fun createDialSyncAllData(message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton(AppHelper.getRemoteString("yes", this))
            { dialog, _ ->
                addAllDataToFirestore()
                dialog.cancel()
            }
            .setNegativeButton(AppHelper.getRemoteString("no", this))
            { dialog, _ ->
                dialog.cancel()

            }
        val alert = builder.create()
        alert.show()
    }

}