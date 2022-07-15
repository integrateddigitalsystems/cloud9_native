package com.ids.librascan.controller.Activities

import Base.ActivityCompactBase
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.ids.librascan.apis.RetrofitClient
import com.ids.librascan.apis.RetrofitInterface
import com.ids.librascan.controller.Adapters.UnitsSpinnerAdapter
import com.ids.librascan.controller.Adapters.WarehouseSpinnerAdapter
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivitySessionsBinding
import com.ids.librascan.databinding.PopupSessionBinding
import com.ids.librascan.db.QrCodeDatabase
import com.ids.librascan.db.Unit
import com.ids.librascan.db.Warehouse
import com.ids.librascan.model.ResponseGetWareHouse
import com.ids.librascan.model.ResponseLogin
import com.ids.librascan.model.WareHouse
import com.ids.librascan.utils.AppHelper
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.hide
import utils.show
import utils.toast
import utils.wtf
import java.util.*

class ActivitySessions : ActivityCompactBase() {
    lateinit var activitySessionsBinding: ActivitySessionsBinding
    lateinit var popupSessionBinding: PopupSessionBinding
    private lateinit var sessionAlertDialog: androidx.appcompat.app.AlertDialog
    private var spinnerWarehouse: ArrayList<Warehouse> = arrayListOf()
    lateinit var warehouseSpinnerAdapter: WarehouseSpinnerAdapter
    var selectedWarehouse = Warehouse()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySessionsBinding = ActivitySessionsBinding.inflate(layoutInflater)
        setContentView(activitySessionsBinding.root)
        init()
    }
    fun init(){
        AppHelper.setAllTexts(activitySessionsBinding.rootSessions, this)
        popupSessionBinding = PopupSessionBinding.inflate(layoutInflater)
        activitySessionsBinding.llAddSession.setOnClickListener {
            showAddSessionAlertDialog()
        }

    }
    fun back(v: View) {
        onBackPressed()
    }


    private fun showAddSessionAlertDialog() {
        spinnerWarehouse.clear()
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)

        AppHelper.setAllTexts(popupSessionBinding.llPagerSession, this)

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

        popupSessionBinding.btClose.setOnClickListener {
            sessionAlertDialog.cancel()
        }
        popupSessionBinding.tvDate.setOnClickListener {
         //   datePickerDialog()
        }
        builder.setView(popupSessionBinding.root)
        sessionAlertDialog = builder.create()
        try {
            sessionAlertDialog.show()
            sessionAlertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (e: Exception) {

        }

    }

    @SuppressLint("SetTextI18n")
    fun datePickerDialog(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month  = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            getApplicationContext()?.let {
                DatePickerDialog(it, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    popupSessionBinding.tvDate.text = "" +dayOfMonth + "/" + (month + 1)+ "/" +year
                },year,month,day)
            }
        datePickerDialog!!.show()

    }

    fun showWarehouse(view: View){
        popupSessionBinding.spWarehouse.performClick()
    }

}