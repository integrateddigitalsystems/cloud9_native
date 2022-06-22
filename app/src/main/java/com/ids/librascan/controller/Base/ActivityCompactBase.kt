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
import com.ids.librascan.controller.Adapters.UnitsSpinnerAdapter
import com.ids.librascan.controller.MyApplication
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
import kotlin.coroutines.CoroutineContext

open class ActivityCompactBase : AppCompatActivity(),CoroutineScope {
    private var spinnerUnits: ArrayList<Unit> = arrayListOf()
    private lateinit var spinnerAdapter: UnitsSpinnerAdapter
    private lateinit var barcodeAlertDialog: androidx.appcompat.app.AlertDialog
    private lateinit var job: Job
    init {
        AppHelper.setLocal(this)
        LocaleUtils.updateConfig(this)
        addUnit()
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppHelper.setLocal(this)
        job =Job()

    }

    private fun addUnit(){
        if (MyApplication.isFirst){
            launch {
                QrCodeDatabase(application).getUnit().insertUnit(Unit("Kg"))
                QrCodeDatabase(application).getUnit().insertUnit(Unit("box"))
                QrCodeDatabase(application).getUnit().insertUnit(Unit("item"))
            }
            MyApplication.isFirst = false
        }
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

    fun showAddBarcodeAlertDialog(c: Activity) {
        val spinnerUnits: ArrayList<Unit> = arrayListOf()
        var spinnerAdapter: UnitsSpinnerAdapter
        var barcodeAlertDialog: androidx.appcompat.app.AlertDialog
        var quantity = 1
        var selectedUnit = Unit()
        spinnerUnits.clear()
        val builder = AlertDialog.Builder(c)
        val popupBarcodeBinding = PopupBarcodeBinding.inflate(LayoutInflater.from(c))
        builder.setView(popupBarcodeBinding.root)
        val alert = builder.create()
        alert.show()

        /*launch {
            spinnerUnits.addAll(QrCodeDatabase(application).getUnit().getUnitData())
            popupBarcodeBinding.spUnits.adapter = spinnerAdapter
        }*/
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
                AppHelper.closeKeyboard(Activity())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // checkCameraPermissions()
            //barcodeAlertDialog.cancel()
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
            val builder = androidx.appcompat.app.AlertDialog.Builder(Activity())
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(AppHelper.getRemoteString("yes", Activity()))
                { dialog, _ ->
                    //val itemDropDownBinding = ItemDropDownBinding.inflate(layoutInflater)
                    popupBarcodeBinding.tvInsert.hide()
                    popupBarcodeBinding.tvCode.isFocusable = false
                    //  itemDropDownBinding.tvItem.isFocusable = true
                    // popupBarcodeBinding.spUnits.adapter = spinnerAdapter
                    popupBarcodeBinding.tvInsertClose.setText(
                        AppHelper.getRemoteString(
                            "update",
                            Activity()
                        )
                    )
                    dialog.cancel()
                }
                .setNegativeButton(AppHelper.getRemoteString("no", Activity()))
                { dialog, _ ->
                    dialog.cancel()

                }
            val alert = builder.create()
            alert.show()
        }

        //popupBarcodeBinding.tvCode.setText(barcode)
        fun insertCode (){
            /*launch {
                QrCodeDatabase(c.application).getCodeDao().insertCode(QrCode(popupBarcodeBinding.tvCode.text.toString().trim(),selectedUnit.id,quantity.toString().trim()))
                popupBarcodeBinding.tvCode.setText("")
                popupBarcodeBinding.etQty.setText("1")
                spinnerUnits.clear()
                spinnerUnits.addAll(QrCodeDatabase(c.application).getUnit().getUnitData())
               // popupBarcodeBinding.spUnits.adapter = spinnerAdapter
            }*/
        }

        popupBarcodeBinding.tvInsertClose.setOnClickListener {
            if (popupBarcodeBinding.tvCode.text.toString() != ""  && selectedUnit.toString() != " " && quantity != 0) {
                 launch {
                    if(popupBarcodeBinding.tvInsertClose.text == (getRemoteString("insert_and_close",Activity()))){
                        val qrCodeQuery = QrCodeDatabase(c.application).getCodeDao().getCode(popupBarcodeBinding.tvCode.text.toString().trim(),selectedUnit.id)
                        if (qrCodeQuery !=null ) {
                            createDialog(AppHelper.getRemoteString("item_exist",Activity()) + "\n" + "\n" + AppHelper.getRemoteString("item_update",c))
                            popupBarcodeBinding.tvCode.setText(qrCodeQuery.code)
                            popupBarcodeBinding.etQty.setText(qrCodeQuery.quantity)
                        } else {
                            insertCode()
                           // toast(AppHelper.getRemoteString("item_save",Activity()))
                            alert.cancel()
                        }
                    }
                    else{
                        insertCode()
                       // toast(AppHelper.getRemoteString("message_update",c))
                        alert.cancel()
                    }
                }

            }
            else{
                when {
                    popupBarcodeBinding.tvCode.text.toString() == "" -> AppHelper.createDialogPositive(
                        c,
                        AppHelper.getRemoteString("error_filled_barcode", Activity())
                    )
                    selectedUnit.toString() == " "-> AppHelper.createDialogPositive(
                        c,
                        AppHelper.getRemoteString("error_filled_unit", Activity())
                    )
                    quantity == 0 -> AppHelper.createDialogPositive(
                        c,
                        AppHelper.getRemoteString("error_filled_qty", Activity())
                    )
                }
            }
            AppHelper.getRemoteString("error_filled_qty", Activity())
        }
        popupBarcodeBinding.tvInsert.setOnClickListener {
            if (popupBarcodeBinding.tvCode.text.toString() != ""  && selectedUnit.toString() != " " && quantity != 0) {

                launch {
                    if(popupBarcodeBinding.tvInsertClose.text == (AppHelper.getRemoteString(
                            "insert_and_close",
                            Activity()
                        ))){
                        val qrCodeQuery = QrCodeDatabase(c.application).getCodeDao().getCode(popupBarcodeBinding.tvCode.text.toString().trim(),selectedUnit.id)
                        if (qrCodeQuery !=null ) {
                            createDialog(AppHelper.getRemoteString("item_exist", Activity()) + "\n" + "\n" + AppHelper.getRemoteString("item_update",c))
                            popupBarcodeBinding.tvCode.setText(qrCodeQuery.code)
                            popupBarcodeBinding.etQty.setText(qrCodeQuery.quantity)
                        } else {
                            insertCode()
                            //  toast(AppHelper.getRemoteString("item_save",Activity()))
                        }
                    }
                    else{
                        insertCode()
                        // toast(AppHelper.getRemoteString("message_update",Activity()))
                        alert.cancel()
                    }
                }

            }
            else{
                when {
                    popupBarcodeBinding.tvCode.text.toString() == "" -> AppHelper.createDialogPositive(
                        c,
                        AppHelper.getRemoteString("error_filled_barcode", Activity())
                    )
                    selectedUnit.toString() == " "-> AppHelper.createDialogPositive(
                        c,
                        AppHelper.getRemoteString("error_filled_unit", Activity())
                    )
                    quantity == 0 -> AppHelper.createDialogPositive(
                        c,
                        AppHelper.getRemoteString("error_filled_qty", Activity())
                    )
                }
            }
        }

        popupBarcodeBinding.btClose.setOnClickListener {
            alert.hide()
        }
        AppHelper.overrideFonts(c, popupBarcodeBinding.llPagerItem)

        try {
            alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        alert.show()

    }

    fun showAddBarcodeAlertDialog(barcode:String,c:Activity) {
        var quantity = 1
        var selectedUnit = Unit()
        spinnerUnits.clear()

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val popupBarcodeBinding = PopupBarcodeBinding.inflate(layoutInflater)

        launch {
            spinnerUnits.addAll(QrCodeDatabase(application).getUnit().getUnitData())
//            popupBarcodeBinding.spUnits.adapter = spinnerAdapter
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
                AppHelper.closeKeyboard(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
           // checkCameraPermissions()
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
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes))
                { dialog, _ ->
                    val itemDropDownBinding = ItemDropDownBinding.inflate(layoutInflater)
                    popupBarcodeBinding.tvInsert.hide()
                    popupBarcodeBinding.tvCode.isFocusable = false
                    itemDropDownBinding.tvItem.isFocusable = true
//                    popupBarcodeBinding.spUnits.adapter = spinnerAdapter
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
//                popupBarcodeBinding.spUnits.adapter = spinnerAdapter
            }
        }

        popupBarcodeBinding.tvInsertClose.setOnClickListener {
            if (popupBarcodeBinding.tvCode.text.toString() != ""  && selectedUnit.toString() != " " && quantity != 0) {
                launch {
                    if(popupBarcodeBinding.tvInsertClose.text == (getString(R.string.insert_and_close))){
                        val qrCodeQuery = QrCodeDatabase(application).getCodeDao().getCode(popupBarcodeBinding.tvCode.text.toString().trim(),selectedUnit.id)
                        if (qrCodeQuery !=null ) {
                            createDialog(AppHelper.getRemoteString("item_exist",c) + "\n" + "\n" + AppHelper.getRemoteString("item_update",c))
                            popupBarcodeBinding.tvCode.setText(qrCodeQuery.code)
                            popupBarcodeBinding.etQty.setText(qrCodeQuery.quantity)
                        } else {
                            insertCode()
                            toast(AppHelper.getRemoteString("item_save",c))
                            barcodeAlertDialog.cancel()
                        }
                    }
                    else{
                        insertCode()
                        toast(AppHelper.getRemoteString("message_update",c))
                        barcodeAlertDialog.cancel()
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
        popupBarcodeBinding.tvInsert.setOnClickListener {
            if (popupBarcodeBinding.tvCode.text.toString() != ""  && selectedUnit.toString() != " " && quantity != 0) {
                launch {
                    if(popupBarcodeBinding.tvInsertClose.text == (getString(R.string.insert_and_close))){
                        val qrCodeQuery = QrCodeDatabase(application).getCodeDao().getCode(popupBarcodeBinding.tvCode.text.toString().trim(),selectedUnit.id)
                        if (qrCodeQuery !=null ) {
                            createDialog(AppHelper.getRemoteString("item_exist",c) + "\n" + "\n" + AppHelper.getRemoteString("item_update",c))
                            popupBarcodeBinding.tvCode.setText(qrCodeQuery.code)
                            popupBarcodeBinding.etQty.setText(qrCodeQuery.quantity)
                        } else {
                            insertCode()
                            toast(AppHelper.getRemoteString("item_save",c))
                        }
                    }
                    else{
                        insertCode()
                        toast(AppHelper.getRemoteString("message_update",c))
                        barcodeAlertDialog.cancel()
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
        barcodeAlertDialog.show()

    }

}
