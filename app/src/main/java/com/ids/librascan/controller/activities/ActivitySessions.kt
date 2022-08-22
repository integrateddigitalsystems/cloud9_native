package com.ids.librascan.controller.activities

import com.ids.librascan.controller.base.ActivityCompactBase
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
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
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
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.controller.adapters.AdapterSession
import com.ids.librascan.controller.adapters.OnInsertUpdate.OnInsertUpdate
import com.ids.librascan.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.librascan.controller.adapters.WarehouseSpinnerAdapter
import com.ids.librascan.custom.CustomTypeFaceSpan
import com.ids.librascan.databinding.*
import com.ids.librascan.db.*
import com.ids.librascan.utils.*
import info.bideens.barcode.BarcodeReader
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class ActivitySessions : ActivityCompactBase(), RVOnItemClickListener, OnInsertUpdate,
    BarcodeReader.BarcodeReaderListener, DatePickerDialog.OnDateSetListener,CompoundButton.OnCheckedChangeListener {
    private lateinit var popupSessionBinding: PopupSessionBinding
    private lateinit var popupLanguageBinding: PopupLanguageBinding
    private lateinit var sessionAlertDialog: androidx.appcompat.app.AlertDialog
    private lateinit var languageAlertDialog: androidx.appcompat.app.AlertDialog
    private var spinnerWarehouse: ArrayList<Warehouse> = arrayListOf()
    private lateinit var warehouseSpinnerAdapter: WarehouseSpinnerAdapter
    private lateinit var adapterSession: AdapterSession
    private var arrSession = ArrayList<SessionQrcode>()
    private var arrApiStatus = ArrayList<ApiStatus>()
    var selectedWarehouse = Warehouse()
    private lateinit var barcodeReader: BarcodeReader
    private var db: FirebaseFirestore? = null
    private var arrayQrcode = ArrayList<QrCode>()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var show: Boolean? = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySessionsBinding = ActivitySessionsBinding.inflate(layoutInflater)
        setContentView(activitySessionsBinding.root)
        popupSessionBinding = PopupSessionBinding.inflate(layoutInflater)

        init()
    }
    @SuppressLint("ResourceType")
    fun init() {
        arrApiStatus.clear()
        db = FirebaseFirestore.getInstance()
        barcodeReader = supportFragmentManager.findFragmentById(R.id.barcodeReader) as BarcodeReader

        configureGoogleSignIn()
        getAllData()
        listener()
    }

    private fun configureGoogleSignIn(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id_auth))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun getAllData(){
        if (MyApplication.isFirst) {
            launch {
                apiRequest()
                MyApplication.isFirst = false
            }
        }

        activitySessionsBinding.loading.show()
        launch {
            arrSession.addAll(QrCodeDatabase(application).getSessions().getSessions())
            checkQrcodeData()
            activitySessionsBinding.loading.hide()
            setData()
        }

    }

    private fun setData() {
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        activitySessionsBinding.rVSessions.layoutManager = layoutManager
        adapterSession = AdapterSession(arrSession, this)
        activitySessionsBinding.rVSessions.adapter = adapterSession
    }

    private fun listener(){
        activitySessionsBinding.llAddSession.setOnClickListener {
            showAddSessionAlertDialog()
        }

        activitySessionsBinding.llSync.setOnClickListener {
            arrApiStatus.clear()
            createDialSyncAllData(resources.getString(R.string.sync_all))
        }

        activitySessionsBinding.iVMore.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_logout -> {
                        createDialogLogout()
                        true
                    }
                    R.id.action_language -> {
                        createDialogLanguage()
                        true
                    }
                    R.id.action_settings -> {
                        startActivity(Intent(this, ActivitySettings::class.java))
                        true
                    }
                    else -> false
                }
            }

            popupMenu.inflate(R.menu.menu)
            popupMenu.show()
            val menu: Menu = popupMenu.menu

            //set title
            for (i in 0 until menu.size()) {
                menu.getItem(i).title = AppHelper.getRemoteString(menu[i].titleCondensed.toString(),this)
            }
            //set font
            for (i in 0 until menu.size()) {
                applyFontToMenuItem(menu.getItem(i))
            }
        }
    }

    private fun applyFontToMenuItem(mi: MenuItem) {
        val font = Typeface.createFromAsset(this.assets, getString(R.string.font))
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
        popupSessionBinding = PopupSessionBinding.inflate(layoutInflater)

        warehouseSpinnerAdapter = WarehouseSpinnerAdapter(this, spinnerWarehouse)
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

        listenersPopupSession()

        builder.setView(popupSessionBinding.root)
        sessionAlertDialog = builder.create()
        sessionAlertDialog.setCanceledOnTouchOutside(false)
        try {
            sessionAlertDialog.show()
            sessionAlertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (e: Exception) {

        }
    }

    private fun listenersPopupSession(){
        popupSessionBinding.ivReloadWarehouse.setOnClickListener {
            popupSessionBinding.loadingData.show()
            launch {
                show = true
                getWarehouse()
                spinnerWarehouse.clear()
                spinnerWarehouse.addAll(QrCodeDatabase(application).getWarehouse().getWarehouse())
                popupSessionBinding.spWarehouse.adapter = warehouseSpinnerAdapter
                Handler(Looper.getMainLooper()).postDelayed({
                    popupSessionBinding.loadingData.hide()
                }, 500)
            }
        }
        popupSessionBinding.tvName.typeface = AppHelper.getTypeFace(this)
        popupSessionBinding.tvInsert.setOnClickListener {
            insertSession()
        }
        popupSessionBinding.btClose.setOnClickListener {
            sessionAlertDialog.cancel()
        }
        popupSessionBinding.tvDate.setOnClickListener {
            datePickerDialog()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun insertSession() {
        if (popupSessionBinding.tvName.text.toString().trim() != "" && popupSessionBinding.spWarehouse.isNotEmpty() && popupSessionBinding.tvDescription.text.toString().trim() != "" && popupSessionBinding.tvDate.text.toString().trim() != "") {
            launch {
                val sessions: Sessions = QrCodeDatabase(application).getSessions()
                    .getSession(selectedWarehouse.name, popupSessionBinding.tvDate.text.toString().trim())
                if (sessions.idSession != 0) {
                    AppHelper.createDialogPositive(
                        this@ActivitySessions,
                        resources.getString(R.string.warehouse_session_exist)
                    )
                } else {
                    QrCodeDatabase(application).getSessions().insertSessions(
                        Sessions(
                            popupSessionBinding.tvName.text.toString().trim(),
                            selectedWarehouse.name,
                            popupSessionBinding.tvDate.text.toString().trim(),
                            popupSessionBinding.tvDescription.text.toString().trim()
                        )
                    )
                    popupSessionBinding.tvName.setText("")
                    popupSessionBinding.tvDescription.setText("")
                    spinnerWarehouse.clear()
                    spinnerWarehouse.addAll(
                        QrCodeDatabase(application).getWarehouse().getWarehouse()
                    )
                    popupSessionBinding.spWarehouse.adapter = warehouseSpinnerAdapter
                    sessionAlertDialog.cancel()
                    arrSession.clear()
                    arrSession.addAll(QrCodeDatabase(application).getSessions().getSessions())
                    activitySessionsBinding.tvNoData.hide()
                    adapterSession.notifyDataSetChanged()
                    toast(resources.getString(R.string.session_save))
                }

            }
        } else checkValid(this)
    }

    private fun checkValid(c: Activity) {
        when {
            popupSessionBinding.tvName.text.toString().trim() == "" -> AppHelper.createDialogPositive(
                c,
              resources.getString(R.string.error_filled_session_name)
            )
            popupSessionBinding.spWarehouse.isEmpty() -> AppHelper.createDialogPositive(
                c,
                resources.getString(R.string.error_filled_warehouse)
            )
            popupSessionBinding.tvDate.text.toString().trim() == "" -> AppHelper.createDialogPositive(
                c,
                resources.getString(R.string.error_filled_date)
            )
            popupSessionBinding.tvDescription.text.toString().trim() == "" -> AppHelper.createDialogPositive(
                c,
               resources.getString(R.string.error_filled_description)
            )
        }
    }

    private fun datePickerDialog() {
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

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar[year, month] = dayOfMonth
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val formatDate = sdf.format(calendar.time)
        popupSessionBinding.tvDate.text = formatDate
    }

    fun showWarehouse(view: View) {
        popupSessionBinding.spWarehouse.performClick()
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onItemClicked(view: View, position: Int) {
        if (view.id == R.id.llScan) {
            MyApplication.isScan = false
            MyApplication.sessionId = arrSession[position].idSession
            MyApplication.sessionName = arrSession[position].sessionName
            if (MyApplication.enableInsert && !MyApplication.enableNewLine)
                activitySessionsBinding.btShowScan.performClick()
            else if (MyApplication.enableInsert && MyApplication.enableNewLine)
                activitySessionsBinding.btShowScan.performClick()
            else showAddBarcodeAlertDialog( false, QrCode(), this, true, arrSession[position])
            activitySessionsBinding.tvNameSession.text = arrSession[position].sessionName
        }
        if (view.id == R.id.llSync) {
            createDialSync(resources.getString(R.string.sync_data), position)
        }
        if (view.id == R.id.tVDelete) {
            createDialogDelete(position)
            adapterSession.notifyDataSetChanged()
        }
        if (view.id == R.id.llItem) {
            startActivity(Intent(this@ActivitySessions, ActivityQrData::class.java))
            MyApplication.sessionId = arrSession[position].idSession
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
   private suspend fun addDataToFirestore(position: Int) {
        launch {
            val arrayQrcode = ArrayList<QrCode>()
            val docData: MutableMap<String, Any> = HashMap()
            arrayQrcode.addAll(QrCodeDatabase(application).getCodeDao().getCodes(arrSession[position].idSession))
            if (AppHelper.isNetworkAvailable(this@ActivitySessions)) {
                if (arrayQrcode.isNotEmpty()) {
                    activitySessionsBinding.loadingData.show()
                    docData["QrCode"] = arrayQrcode
                    db!!.collection("Data")
                        .add(docData)
                        .addOnSuccessListener { documentReference ->
                            toast(resources.getString(R.string.success_added))
                            wtf("DocumentSnapshot written with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            wtf("Error adding document$e")
                        }
                    Handler(Looper.getMainLooper()).postDelayed({
                        activitySessionsBinding.loadingData.hide()
                    }, 500)
                    QrCodeDatabase(application).getSessions().deleteAll(arrSession[position].idSession)
                    arrSession.remove(arrSession[position])
                    adapterSession.notifyDataSetChanged()
                    checkQrcodeData()
                } else {
                    toast(resources.getString(R.string.added_failed))
                    activitySessionsBinding.loadingData.hide()
                }
            } else toast(getString(R.string.check_internet_connection))
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    suspend fun addAllDataToFirestore() {
        show = false
        launch {
            val arrayQrcode = ArrayList<QrCode>()
            val array = ArrayList<QrCode>()
            val docData: MutableMap<String, Any> = HashMap()
            arrayQrcode.addAll(QrCodeDatabase(application).getCodeDao().getAllCode())
          if (AppHelper.isNetworkAvailable(this@ActivitySessions)) {
                if (arrayQrcode.isNotEmpty()) {
                    arrApiStatus.add(ApiStatus(resources.getString(R.string.send_data),resources.getString(R.string.is_done),resources.getString(R.string.send)))
                    activitySessionsBinding.loadingData.show()
                    docData["QrCode"] = arrayQrcode
                    db!!.collection("Data")
                        .add(docData)
                        .addOnSuccessListener { documentReference ->
                            toast(resources.getString(R.string.success_added))
                            wtf("DocumentSnapshot written with ID: ${documentReference.id}")

                        }
                        .addOnFailureListener { e ->
                            wtf("Error adding document$e")
                            toast(e.toString())
                            arrApiStatus.add(ApiStatus(resources.getString(R.string.send_data),getString(R.string.is_failed),resources.getString(R.string.send)))
                        }
                    Handler(Looper.getMainLooper()).postDelayed({
                        activitySessionsBinding.loadingData.hide()

                    }, 500)
                    for (item in arrSession.indices) {
                        array.clear()
                        array.addAll(
                            QrCodeDatabase(application).getCodeDao()
                                .getCodes(arrSession[item].idSession)
                        )
                        if (array.isNotEmpty()) {
                            QrCodeDatabase(application).getSessions()
                                .deleteAll(arrSession[item].idSession)
                        }
                    }
                    arrSession.clear()
                    arrSession.addAll(QrCodeDatabase(application).getSessions().getSessions())
                    adapterSession.notifyDataSetChanged()
                    activitySessionsBinding.llSync.hide()
                    checkQrcodeData()
                } else {
                    toast(resources.getString(R.string.added_failed))
                    arrApiStatus.add(ApiStatus(resources.getString(R.string.send_data),resources.getString(R.string.no_data_app),resources.getString(R.string.send)))
                    activitySessionsBinding.loadingData.hide()
                }
            } else{
                toast(getString(R.string.check_internet_connection))
                arrApiStatus.add(ApiStatus(resources.getString(R.string.send_data),"\n"+getString(R.string.check_internet_connection),resources.getString(R.string.send)))
                arrApiStatus.add(ApiStatus(resources.getString(R.string.get_warehouse),"\n"+getString(R.string.check_internet_connection),resources.getString(R.string.received)))
          }
        }
    }

    private fun createDialSync(message: String, position: Int) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton(resources.getString(R.string.yes))
            { dialog, _ ->
                launch {
                    addDataToFirestore(position)
                }
                dialog.cancel()
            }
            .setNegativeButton(resources.getString(R.string.no))
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
            .setPositiveButton(resources.getString(R.string.yes))
            { dialog, _ ->
               launch {
                   apiRequest()
                }
                dialog.cancel()
            }
            .setNegativeButton(resources.getString(R.string.no))
            { dialog, _ ->
                dialog.cancel()

            }
        val alert = builder.create()
        alert.show()
    }

    private fun createDialogLogout() {
        val builder = AlertDialog.Builder(this)
            .setCancelable(true)
            .setMessage(resources.getString(R.string.logout_message))
            .setPositiveButton(resources.getString(R.string.yes))
            { dialog, _ ->
                mGoogleSignInClient.signOut().addOnCompleteListener {
                    MyApplication.isLogin = false
                    val intent = Intent(this, ActivityLogin::class.java)
                    startActivity(intent)
                    finish()
                }
                dialog.cancel()
            }
            .setNegativeButton(resources.getString(R.string.no))
            { dialog, _ ->
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()
        alert.setCanceledOnTouchOutside(false)
    }

    private fun createDialogLanguage() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        popupLanguageBinding = PopupLanguageBinding.inflate(layoutInflater)
        builder.setView(popupLanguageBinding.root)
        if (MyApplication.languageCode == "en")
            popupLanguageBinding.rbEnglish.isChecked = true
        else popupLanguageBinding.rbArabic.isChecked = true

        popupLanguageBinding.rbEnglish.setOnCheckedChangeListener(this)
        popupLanguageBinding.rbArabic.setOnCheckedChangeListener(this)
        popupLanguageBinding.rbEnglish.typeface = AppHelper.getTypeFace(this)
        popupLanguageBinding.rbArabic.typeface = AppHelper.getTypeFace(this)

        languageAlertDialog = builder.create()
        languageAlertDialog.show()
        languageAlertDialog.setCanceledOnTouchOutside(false)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when {
            buttonView!!.id == R.id.rbArabic -> {
                if (isChecked) {
                    AppHelper.changeLanguage(this, "ar")
                    startActivity(Intent(this@ActivitySessions, ActivitySessions::class.java))
                    finish()
                }
            }
            buttonView.id == R.id.rbEnglish -> {
                if (isChecked) {
                    AppHelper.changeLanguage(this, "en")
                    startActivity(Intent(this@ActivitySessions, ActivitySessions::class.java))
                    finish()
                }
            }
        }
    }

    private suspend fun apiRequest() {
        val syncData = async {
            sendData()
        }
        syncData.join()
        syncData.await()

        val readData = async {
            getData()
        }
        readData.join()
        readData.await()

        showDialogSync()
    }

    private suspend fun sendData() {
        addAllDataToFirestore()
    }

    private suspend fun getData() {
        getWarehouse()
    }

    private suspend fun getWarehouse() {
        try {
            val responseWarehouse =
                RetrofitClient.client?.create(RetrofitInterface::class.java)!!.getWarehouses()
            if (responseWarehouse.isSuccessful && responseWarehouse.body()!!.warehouse!!.size > 0) {
                arrApiStatus.add(ApiStatus(resources.getString(R.string.get_warehouse),resources.getString(R.string.is_done),resources.getString(R.string.received)))
                QrCodeDatabase(application).getWarehouse().insertWarehouse(responseWarehouse.body()!!.warehouse!!)
                if (show!!)
                AppHelper.createDialogError(this,responseWarehouse.body()!!.message.toString(),resources.getString(R.string.get_warehouse),true)
            }
            else{
                arrApiStatus.add(ApiStatus(resources.getString(R.string.get_warehouse),resources.getString(R.string.is_failed),resources.getString(R.string.received)))
                if (show!!)
                AppHelper.createDialogError(this,responseWarehouse.body()!!.errorMessage.toString(),resources.getString(R.string.get_warehouse),false)
            }

        }catch (t: Throwable) {
            if (show!!)
            AppHelper.createDialogError(this,t.toString(),resources.getString(R.string.get_warehouse),false)
        }

    }

    private fun createDialogDelete(position: Int) {
        val builder = AlertDialog.Builder(this)
            .setMessage(resources.getString(R.string.delete_message_session))
            .setCancelable(true)
            .setPositiveButton(resources.getString(R.string.yes))
            { dialog, _ ->
                deleteSession(position)
                dialog.cancel()
            }
            .setNegativeButton(resources.getString(R.string.no))
            { dialog, _ ->
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteSession(position: Int) {
        launch {
            QrCodeDatabase(application).getSessions().deleteAll(arrSession[position].idSession)
            arrSession.remove(arrSession[position])
            adapterSession.notifyDataSetChanged()
            checkQrcodeData()
        }
    }

    private fun checkQrcodeData() {
        launch {
            arrayQrcode.clear()
            arrayQrcode.addAll(QrCodeDatabase(application).getCodeDao().getAllCode())
            if (arrayQrcode.isEmpty())
                activitySessionsBinding.llSync.hide()
            else activitySessionsBinding.llSync.show()
            if (arrSession.isEmpty())
                activitySessionsBinding.tvNoData.show()
            else activitySessionsBinding.tvNoData.hide()
        }
    }

    @SuppressLint("SetTextI18n")
    fun showDialogSync() {
        val itemDialogSyncBinding = ItemDialogSyncBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        itemDialogSyncBinding.dialogMsg.gravity = Gravity.START
        var message = ""
        for (item in 0 until arrApiStatus.size) {
            message +=  arrApiStatus[item].apiName + "  " + arrApiStatus[item].type + arrApiStatus[item].status +"\n"
        }
        itemDialogSyncBinding.dialogMsg.text = message
        itemDialogSyncBinding.dialogMsg.setTextColor(ContextCompat.getColor(this, R.color.gray_bg))
        builder.setView(itemDialogSyncBinding.root)
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
        val d = builder.create()
        d.setCancelable(false)
        d.show()
    }

    fun checkCameraPermissions(view: View) {
        if (ContextCompat.checkSelfPermission(
                this@ActivitySessions,
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
                toast(resources.getString(R.string.camera_error))
            }
        }
    }

    override fun onScanned(barcode: Barcode?) {
        val value = barcode.let { it!!.displayValue }
        barcodeReader.playBeep()
        barcodeReader.pauseScanning()
        this.runOnUiThread {
            activitySessionsBinding.rlBarcode.hide()
            if (MyApplication.enableInsert && !MyApplication.enableNewLine) {
                insertScanAuto(QrCode(value, 0, 1, MyApplication.sessionId), this)
                activitySessionsBinding.llSync.show()
            } else if (MyApplication.enableInsert && MyApplication.enableNewLine) {
                insertNewLineAuto(QrCode(value, 0, 1, MyApplication.sessionId), this)
                activitySessionsBinding.llSync.show()
            } else {
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
        toast(resources.getString(R.string.camera_error))
    }

    override fun onBackPressed() {
        if (activitySessionsBinding.rlBarcode.visibility == View.VISIBLE) {
            barcodeReader.pauseScanning()
            activitySessionsBinding.rlBarcode.hide()
        } else {
            super.onBackPressed()
        }
    }

    fun back(v: View) {
        onBackPressed()
    }


}

