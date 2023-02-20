package com.ids.cloud9.controller.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.ids.cloud9.controller.adapters.AdapterDialog
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.AdapterEdit
import com.ids.cloud9.controller.adapters.AdapterSpinner
import com.ids.cloud9.controller.adapters.AdapterText
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.custom.AppCompactBase
import com.ids.cloud9.databinding.ActivityAddProductBinding
import com.ids.cloud9.databinding.ActivityVisitDetailsBinding
import com.ids.cloud9.databinding.ReasonDialogBinding
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar

class ActivityAddProduct : AppCompactBase() , RVOnItemClickListener {

    var binding: ActivityAddProductBinding? = null
    var prodId : Int ?=0
    var unitId : Int ?=0
    var mainPos  = -1
    var adapterSpin : AdapterSpinner ?=null
    var arraySpin : ArrayList<ItemSpinner> = arrayListOf()
    var adapterSer : AdapterEdit ?=null
    var arraySer : ArrayList<SerialItem> = arrayListOf()
    var array: ArrayList<ProductAllListItem> = arrayListOf()
    var createProduct : CreateProduct ?=null
    var simp : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    var tempArray : ArrayList<ProductAllListItem> = arrayListOf()
    var arrSpinner : ArrayList<ItemSpinner> = arrayListOf()
    var alertDialog : AlertDialog ?=null
    var adapter : AdapterText?=null
    var adapterDialog :AdapterDialog ?=null
    var arr : ArrayList<ItemSpinner> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
        listeners()
    }


    fun init() {
        binding!!.llTool.ivDrawer.hide()
        binding!!.llTool.layoutFragment.show()
        binding!!.llTool.tvTitleTool.text = MyApplication.selectedVisit!!.title
        getUnits()
        getProductNames()
        createProduct = CreateProduct()
        if(MyApplication.selectedProduct!=null){
            binding!!.tvUnitText.text = MyApplication.units.find {
                it.id == MyApplication.selectedProduct!!.unitId
            }!!.name
            binding!!.tvProduct.text = MyApplication.selectedProduct!!.product.name
            binding!!.etQuantity.text = MyApplication.selectedProduct!!.quantity!!.toString().toEditable()
            for(i in 1..MyApplication.selectedProduct!!.quantity){
                arraySer.add(SerialItem(""))
            }
            for(item in MyApplication.selectedProduct!!.serialNumbers!!.indices)
                arraySer.get(item).serial = MyApplication.selectedProduct!!.serialNumbers!!.get(item)
            if(MyApplication.selectedProduct!!.product.unit.visitProducts.size>0)
                unitId = MyApplication.selectedProduct!!.product.unit.visitProducts.get(0).unitId
            prodId = MyApplication.selectedProduct!!.productId
            binding!!.btAddProduct.text = getString(R.string.update)
        }else{
            arraySer.add(SerialItem(""))
        }
        binding!!.rvSerialised.layoutManager = LinearLayoutManager(this)
        adapterSer = AdapterEdit(arraySer,this,this)
        binding!!.rvSerialised.adapter = adapterSer
    }

    fun setUpStatusReasonSpinner() {
        arraySpin.clear()
        arraySpin
        adapterSpin =
            AdapterSpinner(this, R.layout.spinner_text_item, arrSpinner, 0, true)
        binding!!.spUnit.adapter = adapterSpin
        adapterSpin!!.setDropDownViewResource(R.layout.spinner_new)
        binding!!.spUnit.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    var pos = arrSpinner.indexOf(
                        arrSpinner.find {
                            it.selected!!
                        }
                    )
                        binding!!.tvUnitText.text = arrSpinner.get(position).name
                        for (itm in arrSpinner)
                            itm.selected = false
                        arrSpinner.get(position).selected = true
                        unitId = arrSpinner.get(position).id
                        adapterSpin!!.notifyDataSetChanged()
                    }


                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

        if(MyApplication.selectedProduct!=null) {
            var pos = arrSpinner.indexOf(
                arrSpinner.find {
                    it.id == MyApplication.selectedProduct!!.unitId
                }
            )
            binding!!.spUnit.setSelection(
                pos
            )
            unitId = arrSpinner.get(pos).id

        }else{
            binding!!.spUnit.setSelection(0)
            unitId = arrSpinner.get(0).id
        }



    }


    fun setUpProduct(a : ArrayList<ProductAllListItem>){
        arr.clear()
        for(item in a) {
            if(item.selected!=null)
                arr.add(ItemSpinner(item.id, item.name, item.selected))
            else
                arr.add(ItemSpinner(item.id, item.name, false))
        }
        binding!!.rvNames.layoutManager = LinearLayoutManager(this)
        adapter = AdapterText(arr,this,this )
        binding!!.rvNames.adapter = adapter

        binding!!.llLoading.hide()
    }

    fun setUpUnitData(units : UnitList){
        arrSpinner.clear()
        for(item in units) {
            if(item.selected!=null)
                arrSpinner.add(ItemSpinner(item.id, item.name, item.selected))
            else
                arrSpinner.add(ItemSpinner(item.id, item.name, false))
        }
        setUpStatusReasonSpinner()
    }

    fun getUnits(){
        binding!!.llLoading.show()
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getUnits(AppConstants.PRODUCTION_LOOKUP_CODE)
            .enqueue(object : Callback<UnitList> {
                override fun onResponse(
                    call: Call<UnitList>,
                    response: Response<UnitList>
                ) {
                   setUpUnitData(response.body()!!)
                }

                override fun onFailure(call: Call<UnitList>, t: Throwable) {
                    binding!!.llLoading.hide()
                }

            })

    }

    fun getProductNames() {
        binding!!.llLoading.show()
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getAllProducts()
            .enqueue(object : Callback<ProductAllList> {
                override fun onResponse(
                    call: Call<ProductAllList>,
                    response: Response<ProductAllList>
                ) {
                    array.clear()
                    array.addAll(response.body()!!)
                    setUpProduct(array)
                }

                override fun onFailure(call: Call<ProductAllList>, t: Throwable) {
                    binding!!.llLoading.hide()
                }

            })

    }

    fun addProduct(){
        binding!!.llLoading.show()
        var cal = Calendar.getInstance()
        var arr:ArrayList<String> = arrayListOf()
        for(item in arraySer)
            arr.add(item.serial!!)
        createProduct = CreateProduct(
            simp.format(cal.time),
            if(!array.get(mainPos).description.isNullOrEmpty()) array.get(mainPos).description  else "" ,
            array.get(mainPos).name,
            0,
            0,
            false,
            simp.format(cal.time),
            "",
            prodId ,
            binding!!.etQuantity.text.toString().toInt(),
            if(arr.size >0)
                0
            else
              arr.get(0).toInt()  ,
            arr,
            unitId,
            MyApplication.selectedVisit!!.id,
            MyApplication.selectedVisit!!.number
        )

        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java)
            .createMobile(
                createProduct!!
            ).enqueue(object : Callback<ResponseMessage>{
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if(response.body()!!.success!!.equals("true")){
                        AppHelper.createDialogAgain(this@ActivityAddProduct,response.body()!!.message!!){
                            finish()
                        }
                    }else{
                        AppHelper.createDialogPositive(this@ActivityAddProduct,response.body()!!.message!!)
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                   binding!!.llLoading.hide()
                }

            })
    }

    fun editProduct(){
        binding!!.llLoading.show()
        var cal = Calendar.getInstance()
        var arr:ArrayList<String> = arrayListOf()
        for(item in arraySer)
            arr.add(item.serial!!)
        createProduct = CreateProduct(
            simp.format(cal.time),
            MyApplication.selectedProduct!!.customProductDescription ,
            array.find { it.id == prodId }!!.name,
            0,
            MyApplication.selectedProduct!!.id,
            false,
            simp.format(cal.time),
            "",
            prodId ,
            binding!!.etQuantity.text.toString().toInt(),
            if(arr.size >0)
                0
            else
                arr.get(0).toInt()  ,
            arr,
            unitId,
            MyApplication.selectedVisit!!.id,
            MyApplication.selectedVisit!!.number
        )


        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java)
            .updateProduct(
                createProduct!!
            ).enqueue(object : Callback<ResponseMessage>{
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if(response.body()!!.success!!.equals("true")){
                        AppHelper.createDialogAgain(this@ActivityAddProduct,response.body()!!.message!!){
                            finish()
                        }
                    }else{
                        AppHelper.createDialogPositive(this@ActivityAddProduct,response.body()!!.message!!)
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    binding!!.llLoading.hide()
                }

            })
    }

    fun listeners() {

        binding!!.rlProductName.setOnClickListener {
            if(binding!!.llNameSelect.visibility == View.GONE) {
                binding!!.llNameSelect.show()
                binding!!.rlProductName.setBackgroundResource(R.drawable.rounded_top_white_gray_border)
            }else{
                binding!!.llNameSelect.hide()
                binding!!.rlProductName.setBackgroundResource(R.drawable.rounded_white_gray_border)
            }
        }

        binding!!.etQuantity.doOnTextChanged { text, start, before, count ->
            arraySer.clear()
            var ct = 1
            if(text!=null && text.length >0) {
                while (ct <= text.toString().toInt()) {
                    arraySer.add(SerialItem(""))
                    adapterSer!!.notifyDataSetChanged()
                    ct++
                }
            }else{
                arraySer.add(SerialItem(""))
                adapterSer!!.notifyDataSetChanged()
            }
        }

        binding!!.etFilterName.doOnTextChanged { text, start, before, count ->

            var items = array.filter {
                it.name.contains(text!!)
            }
            var arrayList : ArrayList<ProductAllListItem> = arrayListOf()
            arrayList.addAll(items)
            setUpProduct(arrayList)
        }


        binding!!.btAddProduct.setOnClickListener {
            if(prodId!=0 && unitId!=0 && !binding!!.etQuantity.text.toString().isNullOrEmpty() && arraySer.size > 0) {
                if(MyApplication.selectedProduct==null)
                    addProduct()
                else
                    editProduct()
            }else{
                AppHelper.createDialogPositive(this,getString(R.string.fill_details))
            }
        }

        binding!!.llTool.btBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding!!.llTool.ivCalendar.setOnClickListener {
            finishAffinity()
            startActivity(
                Intent(
                    this,
                    ActivityMain::class.java
                )
            )
        }

        binding!!.rlAddProdcut.setOnClickListener {
            binding!!.llNameSelect.hide()
        }

        binding!!.llProductDelete.setOnClickListener {
            AppHelper.createYesNoDialog(this,getString(R.string.you_wanna_delete),0){
                deleteProduct()
            }
        }
    }

    fun deleteProduct(){
        binding!!.llLoading.show()
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).deleteProduct(
            MyApplication.selectedProduct!!.id
        )?.enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(
                call: Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                if(response.body()!!.success.equals("true")){
                    binding!!.llLoading.hide()
                    finish()
                    toast(response.body()!!.message!!)
                }else{
                    binding!!.llLoading.hide()
                }



            }

            override fun onFailure(call: Call<ResponseMessage>, throwable: Throwable) {

                binding!!.llLoading.hide()
            }
        })
    }

    override fun onItemClicked(view: View, position: Int) {

        if (view.id == R.id.llItemText) {
            var old = arr.get(position).selected
            try {
                arr.find {
                    it.selected!!
                }!!.selected = false
                array.find {
                    it.selected!!
                }!!.selected = false
            } catch (ex: Exception) {

            }

            if (!old!!)
                arr.get(position).selected = true
            else
                arr.get(position).selected = false
            array.find {
                it.id == arr.get(position).id
            }!!.selected = arr.get(position).selected
            prodId = arr.get(position).id
            mainPos = array.indexOf(array.find {
                it.id == arr.get(position).id
            })

            binding!!.tvProduct.text = arr.get(position).name
            binding!!.llNameSelect.hide()




            adapter!!.notifyDataSetChanged()
        }
    }
}