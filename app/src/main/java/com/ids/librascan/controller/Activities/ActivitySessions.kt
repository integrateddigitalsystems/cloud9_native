package com.ids.librascan.controller.Activities

import Base.ActivityCompactBase
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.vision.barcode.Barcode
import com.google.firebase.firestore.FirebaseFirestore
import com.ids.librascan.R
import com.ids.librascan.apis.RetrofitClient
import com.ids.librascan.apis.RetrofitInterface
import com.ids.librascan.controller.Adapters.AdapterSession
import com.ids.librascan.controller.Adapters.OnInsertUpdate.OnInsertUpdate
import com.ids.librascan.controller.Adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.librascan.controller.Adapters.WarehouseSpinnerAdapter
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.custom.CustomTypeFaceSpan
import com.ids.librascan.databinding.ActivitySessionsBinding
import com.ids.librascan.databinding.PopupLanguageBinding
import com.ids.librascan.databinding.PopupSessionBinding
import com.ids.librascan.db.*
import com.ids.librascan.model.ResponseGetWareHouse
import com.ids.librascan.utils.AppHelper
import info.bideens.barcode.BarcodeReader
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.*
import java.util.*


class ActivitySessions : ActivityCompactBase(), RVOnItemClickListener, OnInsertUpdate,
    BarcodeReader.BarcodeReaderListener, DatePickerDialog.OnDateSetListener,CompoundButton.OnCheckedChangeListener{
    lateinit var popupSessionBinding: PopupSessionBinding
    lateinit var popupLanguageBinding : PopupLanguageBinding
    private lateinit var sessionAlertDialog: androidx.appcompat.app.AlertDialog
    private lateinit var languageAlertDialog: androidx.appcompat.app.AlertDialog
    private var spinnerWarehouse: ArrayList<Warehouse> = arrayListOf()
    lateinit var warehouseSpinnerAdapter: WarehouseSpinnerAdapter
    lateinit var adapterSession: AdapterSession
    private var arrSession = ArrayList<Sessions>()
    var selectedWarehouse = Warehouse()
    lateinit var barcodeReader: BarcodeReader
    private var db: FirebaseFirestore? = null
    var arrayQrcode = ArrayList<QrCode>()

    lateinit var mGoogleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySessionsBinding = ActivitySessionsBinding.inflate(layoutInflater)
        setContentView(activitySessionsBinding.root)
        popupSessionBinding = PopupSessionBinding.inflate(layoutInflater)
        init()

    }
    @SuppressLint("ResourceType")
    fun init(){

        db = FirebaseFirestore.getInstance()
        addWarehouse()
        AppHelper.setAllTexts(activitySessionsBinding.rootSessions, this)
        activitySessionsBinding.llAddSession.setOnClickListener {
            showAddSessionAlertDialog()
        }

        activitySessionsBinding.llSync.setOnClickListener {
           createDialSyncAllData(AppHelper.getRemoteString("sync_all",this))
        }
        activitySessionsBinding.loading.show()
        launch {
            val  array=ArrayList<QrCode>()
            array.addAll(QrCodeDatabase(application).getCodeDao().getCodes(MyApplication.sessionId))
            if (array.isEmpty()) {
                QrCodeDatabase(application).getSessions().updateCount(0, MyApplication.sessionId)
            }
            arrSession.addAll(QrCodeDatabase(application).getSessions().getSessions())
            if (arrSession.isEmpty())
                activitySessionsBinding.tvNodata.show()
            activitySessionsBinding.loading.hide()
            setData()
        }
        barcodeReader = supportFragmentManager.findFragmentById(R.id.barcodeReader) as BarcodeReader

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id_auth))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)

        activitySessionsBinding.iVMore.setOnClickListener {
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
            val menu: Menu = popupMenu.menu
            for (i in 0 until menu.size()) {
                val mi: MenuItem = menu.getItem(i)
                applyFontToMenuItem(mi)
            }

        }
        launch {
                arrayQrcode.clear()
                arrayQrcode.addAll(QrCodeDatabase(application).getCodeDao().getAllCode())
                if (arrayQrcode.isNotEmpty()){
                    activitySessionsBinding.llSync.show()
                }

            }
    }
    fun back(v: View) {
        onBackPressed()
    }

    private fun applyFontToMenuItem(mi: MenuItem) {
        val font = Typeface.createFromAsset(this.assets, "fonts/HelveticaNeueLTArabic-Bold.ttf")
        val mNewTitle = SpannableString(mi.title)
        mNewTitle.setSpan(
            CustomTypeFaceSpan("", font, Color.BLACK),
            0,
            mNewTitle.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        mi.title = mNewTitle
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

        popupSessionBinding.tvName.typeface = AppHelper.getTypeFace(this)
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
        sessionAlertDialog.setCanceledOnTouchOutside(false)
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
                sessions = QrCodeDatabase(application).getSessions().getSession(selectedWarehouse.name,popupSessionBinding.tvDate.text.toString())
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
                    activitySessionsBinding.tvNodata.hide()
                    adapterSession.notifyDataSetChanged()
                    toast(AppHelper.getRemoteString("session_save", c))
                }

            }
        }
        else checkValid(this)
    }

    private fun checkValid(c: Activity){
        when {
            popupSessionBinding.tvName.text.toString() == "" -> AppHelper.createDialogPositive(c,AppHelper.getRemoteString("error_filled_session_name", this))
            popupSessionBinding.spWarehouse.isEmpty()-> AppHelper.createDialogPositive(c,AppHelper.getRemoteString("error_filled_warehouse", this))
            popupSessionBinding.tvDate.text.toString() == "" -> AppHelper.createDialogPositive(c, AppHelper.getRemoteString("error_filled_date", this))
            popupSessionBinding.tvDescription.text.toString() == "" -> AppHelper.createDialogPositive(c, AppHelper.getRemoteString("error_filled_description", this))
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
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
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

            else showAddBarcodeAlertDialog(this, false, QrCode(), this,true,arrSession[position],MyApplication.sessionId)
            activitySessionsBinding.tvNameSession.text = arrSession[position].sessionName
        }
            if (view.id == R.id.llSync){
                createDialSync(AppHelper.getRemoteString("sync_data",this),position)
            }
            if (view.id == R.id.tVDelete){
                createDialogDelete(position)
            }
                if (view.id == R.id.llItem){
                    startActivity(Intent(this@ActivitySessions, ActivityQrData::class.java))
                    MyApplication.sessionId = arrSession[position].id
                    MyApplication.sessionName = arrSession[position].sessionName
                    finish()
                }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onInsertUpdate(boolean: Boolean) {
        launch {
            arrSession.clear()
            arrSession.addAll(QrCodeDatabase(application).getSessions().getSessions())
            adapterSession.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addDataToFirestore(position: Int){
        launch {
            val arrayQrcode = ArrayList<QrCode>()
            val docData: MutableMap<String, Any> = HashMap()
            arrayQrcode.addAll(QrCodeDatabase(application).getCodeDao().getCodes(arrSession[position].id))
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
                QrCodeDatabase(application).getCodeDao().deleteCodes(arrSession[position].id)
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
            val array = ArrayList<QrCode>()
            val docData: MutableMap<String, Any> = HashMap()
            arrayQrcode.addAll(QrCodeDatabase(application).getCodeDao().getAllCode())
            if (arrayQrcode.isNotEmpty()) {
                activitySessionsBinding.loadingData.show()
                docData.put("QrCode", arrayQrcode)
                db!!.collection("Data")
                    .add(docData)
                    .addOnSuccessListener { documentReference ->
                        toast(AppHelper.getRemoteString("success_added", this@ActivitySessions))
                        wtf("DocumentSnapshot written with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        wtf("Error adding document$e")
                    }
                Handler(Looper.getMainLooper()).postDelayed({
                    activitySessionsBinding.loadingData.hide()
                }, 500)
                for (item in arrSession.indices) {
                    array.clear()
                    array.addAll(
                        QrCodeDatabase(application).getCodeDao().getCodes(arrSession[item].id)
                    )
                    if (array.isNotEmpty()) {
                        QrCodeDatabase(application).getSessions().deleteSession(arrSession[item].id)
                        QrCodeDatabase(application).getCodeDao().deleteCodes(arrSession[item].id)
                    }
                }
                arrSession.clear()
                arrSession.addAll(QrCodeDatabase(application).getSessions().getSessions())
                adapterSession.notifyDataSetChanged()
                activitySessionsBinding.llSync.hide()
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
                activitySessionsBinding.llSync.show()
            }

            else  if (MyApplication.enableInsert && MyApplication.enableNewLine){
                insertScan(QrCode(value,0,1,MyApplication.sessionId),this,this)
                activitySessionsBinding.llSync.show()

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

    private fun createDialogLogout() {
        val builder = AlertDialog.Builder(this)
            .setCancelable(true)
            .setMessage(AppHelper.getRemoteString("logout_message",this))
            .setPositiveButton(AppHelper.getRemoteString("yes",this))
            { dialog, _ ->
                mGoogleSignInClient.signOut().addOnCompleteListener {
                    MyApplication.isLogin = false
                    val intent= Intent(this, ActivityLogin::class.java)
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
        alert.setCanceledOnTouchOutside(false)
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
        popupLanguageBinding.rbEnglish.typeface =AppHelper.getTypeFace(this)
        popupLanguageBinding.rbArabic.typeface =AppHelper.getTypeFace(this)

        languageAlertDialog = builder.create()
        languageAlertDialog.show()
        languageAlertDialog.setCanceledOnTouchOutside(false)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when {
            buttonView!!.id == R.id.rbArabic -> {
                if (isChecked) {
                    AppHelper.changeLanguage(this,"ar")
                    startActivity(Intent(this@ActivitySessions, ActivitySessions::class.java))
                    finish()
                }
            }
            buttonView.id == R.id.rbEnglish -> {
                if (isChecked) {
                    AppHelper.changeLanguage(this,"en")
                    startActivity(Intent(this@ActivitySessions, ActivitySessions::class.java))
                    finish()
                }
            }
        }
    }

    fun addWarehouse(){
        launch {
            QrCodeDatabase(application).getWarehouse().deleteAllWarehouse()
        }
        getWarehouse()
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

    private fun createDialogDelete(position: Int) {
        val builder = android.app.AlertDialog.Builder(this)
            .setMessage(AppHelper.getRemoteString("delete_message_session",this))
            .setCancelable(true)
            .setPositiveButton(AppHelper.getRemoteString("yes",this))
            { dialog, _ ->
                deleteSession(position)
                dialog.cancel()
            }
            .setNegativeButton(AppHelper.getRemoteString("no",this))
            { dialog, _ ->
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteSession(position: Int){
        launch {
            QrCodeDatabase(application).getCodeDao().deleteCodes(arrSession[position].id)
            QrCodeDatabase(application).getSessions().deleteSession(arrSession[position].id)
            arrSession.remove(arrSession[position])
            adapterSession.notifyDataSetChanged()
        }
    }

}