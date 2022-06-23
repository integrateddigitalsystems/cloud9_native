package Base


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.ids.librascan.R
import com.ids.librascan.controller.Adapters.OnInsertUpdate
import com.ids.librascan.controller.Adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.librascan.controller.Adapters.UnitsSpinnerAdapter
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivityMainBinding
import com.ids.librascan.databinding.ItemDropDownBinding
import com.ids.librascan.databinding.PopupBarcodeBinding
import com.ids.librascan.db.QrCode
import com.ids.librascan.db.QrCodeDatabase
import com.ids.librascan.db.Unit
import com.ids.librascan.utils.AppHelper
import com.ids.librascan.utils.AppHelper.Companion.getRemoteString
import info.bideens.barcode.BarcodeReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import utils.LocaleUtils
import utils.hide
import utils.toast
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

open class ActivityCompactBase : AppCompatActivity(),CoroutineScope {
    private lateinit var barcodeAlertDialog: androidx.appcompat.app.AlertDialog
    private lateinit var job: Job
    lateinit var popupBarcodeBinding: PopupBarcodeBinding
    private var spinnerUnits: ArrayList<Unit> = arrayListOf()
    lateinit var spinnerAdapter: UnitsSpinnerAdapter
    var quantity = 1
    var selectedUnit = Unit()
    lateinit var onInsertUpdate: OnInsertUpdate

    init {
        AppHelper.setLocal(this)
        LocaleUtils.updateConfig(this)
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppHelper.setLocal(this)
        job =Job()
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

    fun showAddBarcodeAlertDialog(c:Activity,isUpdate : Boolean,qrCode: QrCode,onInsUpdate: OnInsertUpdate) {
        onInsertUpdate=onInsUpdate
        spinnerUnits.clear()
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        popupBarcodeBinding = PopupBarcodeBinding.inflate(layoutInflater)
        AppHelper.setAllTexts(popupBarcodeBinding.llPagerItem, this)
        spinnerAdapter = UnitsSpinnerAdapter(this, spinnerUnits)

        getUnit()

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

        popupBarcodeBinding.tvCode.setText(qrCode.code.ifEmpty { "" })

        popupBarcodeBinding.tvInsertClose.setOnClickListener {
            insertAndClose(c)
        }

        popupBarcodeBinding.tvInsert.setOnClickListener {
          insert(c)
        }

        popupBarcodeBinding.btClose.setOnClickListener {
            barcodeAlertDialog.cancel()
        }
        AppHelper.overrideFonts(c, popupBarcodeBinding.llPagerItem)
        builder.setView(popupBarcodeBinding.root)
        barcodeAlertDialog = builder.create()
        try {
            barcodeAlertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (isUpdate){
            popupBarcodeBinding.tvInsertClose.setText(getRemoteString("update",this))
            popupBarcodeBinding.tvInsert.hide()
            popupBarcodeBinding.tvTitle.setText(getRemoteString("message_update",this))
            popupBarcodeBinding.ivScan.hide()
            barcodeAlertDialog.show()
            popupBarcodeBinding.spUnits.isEnabled = false
            popupBarcodeBinding.tvCode.isFocusable = false
            popupBarcodeBinding.tvCode.setText(qrCode.code)
            popupBarcodeBinding.ivBarcode.isClickable = false
            popupBarcodeBinding.etQty.setText(qrCode.quantity.toString())
            selectedUnit.id = qrCode.unitId
            popupBarcodeBinding.spUnits.adapter = spinnerAdapter


        }
        else{
            barcodeAlertDialog.show()
        }

    }

    fun createDialog(message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton(getRemoteString("yes",this))
            { dialog, _ ->
                popupBarcodeBinding.tvInsert.hide()
                popupBarcodeBinding.tvCode.isFocusable = false
                popupBarcodeBinding.spUnits.isFocusable = false
                popupBarcodeBinding.spUnits.isEnabled = false
                popupBarcodeBinding.tvInsertClose.setText(AppHelper.getRemoteString("update",this))
                dialog.cancel()
            }
            .setNegativeButton(getRemoteString("no",this))
            { dialog, _ ->
                dialog.cancel()

            }
        val alert = builder.create()
        alert.show()
    }

    fun insertCode (){
        launch {
            QrCodeDatabase(application).getCodeDao().insertCode(QrCode(popupBarcodeBinding.tvCode.text.toString().trim(),selectedUnit.id,quantity))
            popupBarcodeBinding.tvCode.setText("")
            popupBarcodeBinding.etQty.setText("1")
            spinnerUnits.clear()
            spinnerUnits.addAll(QrCodeDatabase(application).getUnit().getUnitData())
            popupBarcodeBinding.spUnits.adapter = spinnerAdapter
        }
    }

    fun getUnit(){
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
    }
    fun insertAndClose(c:Activity){
        if (popupBarcodeBinding.tvCode.text.toString() != ""  && selectedUnit.toString() != " " && quantity != 0) {
            launch {
                if (popupBarcodeBinding.tvInsertClose.text == (getRemoteString("insert_and_close",c))) {
                        val qrCodeQuery = QrCodeDatabase(application).getCodeDao().getCode(
                            popupBarcodeBinding.tvCode.text.toString().trim(),
                            selectedUnit.id
                        )
                        if (qrCodeQuery != null) {
                            createDialog(
                                AppHelper.getRemoteString(
                                    "item_exist",
                                    c
                                ) + "\n" + "\n" + AppHelper.getRemoteString("item_update", c)
                            )
                            popupBarcodeBinding.tvCode.setText(qrCodeQuery.code)
                            popupBarcodeBinding.etQty.setText(qrCodeQuery.quantity.toString())
                        } else {
                            insertCode()
                            toast(AppHelper.getRemoteString("item_save", c))
                            barcodeAlertDialog.cancel()
                        }
                    }

                else{
                    insertCode()
                    toast(AppHelper.getRemoteString("message_update",c))
                    barcodeAlertDialog.cancel()
                    onInsertUpdate.onInsertUpdate(true)
                }
            }

        }
        else{
            when {
                popupBarcodeBinding.tvCode.text.toString() == "" -> AppHelper.createDialogPositive(
                    c,
                    AppHelper.getRemoteString("error_filled_barcode",this)
                )
                selectedUnit.toString() == " "-> AppHelper.createDialogPositive(
                    c,
                    AppHelper.getRemoteString("error_filled_unit",this)
                )
                quantity == 0 -> AppHelper.createDialogPositive(
                    c,
                    AppHelper.getRemoteString("error_filled_qty",this)
                )
            }
        }
    }

    fun insert(c:Activity){
        if (popupBarcodeBinding.tvCode.text.toString() != ""  && selectedUnit.toString() != " " && quantity != 0) {
            launch {
                if(popupBarcodeBinding.tvInsertClose.text == (getRemoteString("insert_and_close",c))) {
                    val qrCodeQuery = QrCodeDatabase(application).getCodeDao().getCode(popupBarcodeBinding.tvCode.text.toString().trim(),selectedUnit.id)
                    if (qrCodeQuery !=null ) {
                        createDialog(AppHelper.getRemoteString("item_exist",c) + "\n" + "\n" + AppHelper.getRemoteString("item_update",c))
                        popupBarcodeBinding.tvCode.setText(qrCodeQuery.code)
                        popupBarcodeBinding.etQty.setText(qrCodeQuery.quantity.toString())
                    } else {
                        insertCode()
                        toast(AppHelper.getRemoteString("item_save",c))
                    }
                }
                else{
                    insertCode()
                    toast(AppHelper.getRemoteString("message_update",c))
                    barcodeAlertDialog.cancel()
                    onInsertUpdate.onInsertUpdate(true)
                }
            }

        }
        else{
            when {
                popupBarcodeBinding.tvCode.text.toString() == "" -> AppHelper.createDialogPositive(
                    c,
                    AppHelper.getRemoteString("error_filled_barcode",this)
                )
                selectedUnit.toString() == " "-> AppHelper.createDialogPositive(
                    c,
                    AppHelper.getRemoteString("error_filled_unit",this)
                )
                quantity == 0 -> AppHelper.createDialogPositive(
                    c,
                    AppHelper.getRemoteString("error_filled_qty",this)
                )
            }
        }
    }




}
