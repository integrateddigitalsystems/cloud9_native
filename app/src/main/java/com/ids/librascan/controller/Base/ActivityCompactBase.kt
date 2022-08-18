package Base


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.ViewPumpAppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isNotEmpty
import androidx.core.view.updateLayoutParams
import com.ids.librascan.R
import com.ids.librascan.apis.WifiService
import com.ids.librascan.controller.Adapters.OnInsertUpdate.OnInsertUpdate
import com.ids.librascan.controller.Adapters.SessionsSpinnerAdapter
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivityQrDataBinding
import com.ids.librascan.databinding.ActivitySessionsBinding
import com.ids.librascan.databinding.PopupBarcodeBinding
import com.ids.librascan.db.*
import com.ids.librascan.db.Unit
import com.ids.librascan.utils.AppHelper
import dev.b3nedikt.restring.Restring
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import utils.*
import java.util.*
import kotlin.coroutines.CoroutineContext


@SuppressLint("ObsoleteSdkInt")
open class ActivityCompactBase : AppCompatActivity(),CoroutineScope {
    lateinit var barcodeAlertDialog: androidx.appcompat.app.AlertDialog
    private lateinit var job: Job
    lateinit var popupBarcodeBinding: PopupBarcodeBinding
    private var spinnerSessions: ArrayList<Sessions> = arrayListOf()
    private lateinit var sessionsSpinnerAdapter: SessionsSpinnerAdapter
    lateinit var activitySessionsBinding: ActivitySessionsBinding
    lateinit var activityQrDataBinding: ActivityQrDataBinding
    var quantity = 1
    private var selectedUnit = Unit()
    var selectedSession = Sessions()
    private lateinit var onInsertUpdate: OnInsertUpdate
    private var closeDialog : Boolean = false
    private val appCompatDelegate: AppCompatDelegate by lazy {
        ViewPumpAppCompatDelegate(
            baseDelegate = super.getDelegate(),
            baseContext = this,
            wrapContext = Restring::wrapContext
        )
    }
    override fun getDelegate(): AppCompatDelegate {
        return appCompatDelegate
    }
    init {
        AppHelper.setLocal()
        if (!WifiService.instance.isOnline() && MyApplication.localizeArray==null && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1){
            LocaleUtils.updateConfig(this)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        AppHelper.handleCrashes(this)
        super.onCreate(savedInstanceState)
        AppHelper.setLocal()
        job =Job()
        try{
        if (MyApplication.languageCode == AppConstants.LANG_ENGLISH) {
            AppHelper.setLocalStrings(this, "en", Locale("en"))
        } else if (MyApplication.languageCode == AppConstants.LANG_ARABIC) {
            AppHelper.setLocalStrings(this, "ar", Locale("ar"))
        }}catch (e:Exception){}

    }
    override fun attachBaseContext(newBase: Context) {
        var newBase = newBase
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val config = newBase.resources.configuration
            config.setLocale(Locale.getDefault())
            newBase = newBase.createConfigurationContext(config)
        }
        super.attachBaseContext(newBase)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    fun showAddBarcodeAlertDialog(isUpdate : Boolean,qrCode: QrCode,onInsUpdate: OnInsertUpdate,onScan : Boolean,sessions: SessionQrcode) {
        spinnerSessions.clear()
        onInsertUpdate = onInsUpdate
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        popupBarcodeBinding = PopupBarcodeBinding.inflate(layoutInflater)
        sessionsSpinnerAdapter = SessionsSpinnerAdapter(this, spinnerSessions)

        launch {
            spinnerSessions.addAll(QrCodeDatabase(application).getSessions().getAllSessions())
            popupBarcodeBinding.spSession.adapter = sessionsSpinnerAdapter
            popupBarcodeBinding.spSession.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedSession = parent?.getItemAtPosition(position) as Sessions

                    }
                }
            if (onScan) {
                popupBarcodeBinding.ivSession.hide()
                popupBarcodeBinding.spSession.isEnabled = false
                if (MyApplication.isScan) {
                    popupBarcodeBinding.spSession.setSelection(spinnerSessions.indexOf(
                        spinnerSessions.find {
                            it.idSession == MyApplication.sessionId
                        }
                    ))
                } else {
                    popupBarcodeBinding.spSession.setSelection(spinnerSessions.indexOf(
                        spinnerSessions.find {
                            it.idSession == sessions.idSession
                        }
                    ))
                }
            }
            listeners()
            popupBarcodeBinding.tvInsertClose.setOnClickListener {
                closeDialog = true
                insert(qrCode)
            }
            popupBarcodeBinding.tvInsert.setOnClickListener {
                closeDialog = false
                insert(qrCode)
            }

            isUpdateChecked(isUpdate, qrCode)
        }
        builder.setView(popupBarcodeBinding.root)
        barcodeAlertDialog = builder.create()
        barcodeAlertDialog.setCanceledOnTouchOutside(false)
        try {
            barcodeAlertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (MyApplication.languageCode == AppConstants.LANG_ARABIC){
            popupBarcodeBinding.tvIncrement.background = ContextCompat.getDrawable(this, R.drawable.rounded_left_gray)
            popupBarcodeBinding.tvDecrement.background = ContextCompat.getDrawable(this, R.drawable.rounded_right_gray)
        }
    }

    private fun isUpdateChecked(isUpdate:Boolean, qrCode: QrCode){
        if (isUpdate){
            popupBarcodeBinding.tvInsertClose.text = resources.getString(R.string.update)
            popupBarcodeBinding.tvTitle.text = resources.getString(R.string.update_title)
            popupBarcodeBinding.tvInsert.hide()
            popupBarcodeBinding.ivScan.hide()
            barcodeAlertDialog.show()
            popupBarcodeBinding.viewUpdate.updateLayoutParams<LinearLayout.LayoutParams>{width=0}
            popupBarcodeBinding.spSession.isEnabled = false
            popupBarcodeBinding.tvCode.isFocusable = false
            popupBarcodeBinding.ivBarcode.isClickable = false
            popupBarcodeBinding.ivSession.hide()

            popupBarcodeBinding.tvCode.setText(qrCode.code)
            popupBarcodeBinding.etQty.setText(qrCode.quantity.toString())

            popupBarcodeBinding.spSession.setSelection(spinnerSessions.indexOf(
                spinnerSessions.find {
                    it.idSession == qrCode.sessionId
                }
            ))
            sessionsSpinnerAdapter.notifyDataSetChanged()
        }
        else{
            barcodeAlertDialog.show()
        }
    }

    fun insert(qrCode: QrCode){
        if (popupBarcodeBinding.tvCode.text.toString().trim() != ""  &&  popupBarcodeBinding.spSession.isNotEmpty() && quantity != 0) {
            launch {
                if (popupBarcodeBinding.tvInsertClose.text == resources.getString(R.string.insert_and_close)) {
                    val qrCode: QrCode = QrCodeDatabase(application).getCodeDao().getCode(popupBarcodeBinding.tvCode.text.toString().trim(),selectedSession.idSession)
                    if (qrCode!=null){
                        if(!MyApplication.enableInsert && !MyApplication.enableNewLine)
                            insertCode(qrCode)
                        else if (!MyApplication.enableInsert && MyApplication.enableNewLine)
                            insertCodeNew()
                    }
                    else insertCodeNew()
                    if (closeDialog) barcodeAlertDialog.cancel()
                    if (MyApplication.isScan){
                        activityQrDataBinding.ivScan.show()
                    }
                    else activitySessionsBinding.llSync.show()

                //Update QrCode
                }else updateQrcode(qrCode)

            }
        }
        else checkIsNotEmpty()
    }

    private fun checkIsNotEmpty(){
        when {
            popupBarcodeBinding.tvCode.text.toString().trim() == "" -> AppHelper.createDialogPositive(this@ActivityCompactBase,resources.getString(R.string.error_filled_barcode))
            quantity == 0 -> AppHelper.createDialogPositive(this@ActivityCompactBase, resources.getString(R.string.error_filled_qty))
        }
    }
    private fun listeners(){
        popupBarcodeBinding.ivBarcode.setOnClickListener {
            popupBarcodeBinding.btShowScan.performClick()
            barcodeAlertDialog.cancel()
        }

        popupBarcodeBinding.ivScan.setOnClickListener {
            popupBarcodeBinding.btShowScan.performClick()
            barcodeAlertDialog.cancel()
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
        popupBarcodeBinding.etQty.setOnFocusChangeListener {_, hasFocus ->
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

        popupBarcodeBinding.btClose.setOnClickListener {
            barcodeAlertDialog.cancel()
        }
    }

    private fun updateQrcode(qrCode: QrCode){
        launch {
            QrCodeDatabase(application).getCodeDao().updateCode(quantity,qrCode.idQrcode)
            popupBarcodeBinding.tvCode.setText("")
            popupBarcodeBinding.etQty.setText("1")
            onInsertUpdate.onInsertUpdate(true)
            toast(resources.getString(R.string.message_update))
            barcodeAlertDialog.cancel()
        }
    }

    fun showSession(view: View){
        popupBarcodeBinding.spSession.performClick()
    }

    private fun insertCode (qrCode: QrCode){
        launch {
            QrCodeDatabase(application).getCodeDao().updateCode(qrCode.quantity +quantity,qrCode.idQrcode)
            popupBarcodeBinding.tvCode.setText("")
            popupBarcodeBinding.etQty.setText("1")
            toast(resources.getString(R.string.item_save))
            onInsertUpdate.onInsertUpdate(true)
        }
    }
    private fun insertCodeNew (){
        launch {
            QrCodeDatabase(application).getCodeDao().insertCode(QrCode(popupBarcodeBinding.tvCode.text.toString().trim(),selectedUnit.id,quantity,selectedSession.idSession))
            popupBarcodeBinding.tvCode.setText("")
            popupBarcodeBinding.etQty.setText("1")
            toast(resources.getString(R.string.item_save))
            onInsertUpdate.onInsertUpdate(true)
        }
    }

    fun insertScanAuto(qrCode: QrCode,onInsUpdate: OnInsertUpdate) {
        onInsertUpdate=onInsUpdate
        launch {
            val qrCodeScan: QrCode =QrCodeDatabase(application).getCodeDao().getCode(qrCode.code, qrCode.sessionId)
            if (qrCodeScan != null) {
                QrCodeDatabase(application).getCodeDao().updateCode(qrCodeScan.quantity+1,qrCodeScan.idQrcode)
                onInsertUpdate.onInsertUpdate(true)
                toast(resources.getString(R.string.item_save))
            }
            else{
                QrCodeDatabase(application).getCodeDao().insertCode(QrCode(qrCode.code,0,qrCode.quantity,qrCode.sessionId))
                onInsertUpdate.onInsertUpdate(true)
                toast(resources.getString(R.string.item_save))

            }
        }
    }

    fun insertNewLineAuto(qrCode: QrCode,onInsUpdate: OnInsertUpdate){
        onInsertUpdate=onInsUpdate
        launch {
            QrCodeDatabase(application).getCodeDao().insertCode(qrCode)
            onInsertUpdate.onInsertUpdate(true)
            toast(resources.getString(R.string.item_save))
        }
    }
}
