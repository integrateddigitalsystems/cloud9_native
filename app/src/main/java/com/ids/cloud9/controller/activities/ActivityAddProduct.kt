package com.ids.cloud9.controller.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.AdapterEdit
import com.ids.cloud9.controller.adapters.AdapterSpinner
import com.ids.cloud9.controller.adapters.AdapterText
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.custom.AppCompactBase
import com.ids.cloud9.databinding.ActivityAddProductBinding
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ActivityAddProduct : AppCompactBase() , RVOnItemClickListener {

    var binding: ActivityAddProductBinding? = null
    var prodId : Int ?=0
    var unitId : Int ?=0
    var mainPos  = -1
    var selectedPos = 0
    var adapterSpin : AdapterSpinner ?=null
    var arraySpin : ArrayList<ItemSpinner> = arrayListOf()
    var adapterSer : AdapterEdit ?=null
    var arraySer : ArrayList<SerialItem> = arrayListOf()
    var array: ArrayList<ProductAllListItem> = arrayListOf()
    var createProduct : CreateProduct ?=null
    var simp : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    var arrSpinner : ArrayList<ItemSpinner> = arrayListOf()
    var adapter : AdapterText?=null
    var arr : ArrayList<ItemSpinner> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
        listeners()
    }


    override fun onResume() {
        super.onResume()
        MyApplication.activityResumed()
    }

    override fun onPause() {
        super.onPause()
        MyApplication.activityPaused()
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
            binding!!.etQuantity.text = MyApplication.selectedProduct!!.quantity.toString().toEditable()
            for(i in 1..MyApplication.selectedProduct!!.quantity!!){
                arraySer.add(SerialItem(""))
            }
            for(item in MyApplication.selectedProduct!!.serialNumbers!!.indices)
                arraySer.get(item).serial = MyApplication.selectedProduct!!.serialNumbers!!.get(item)
            if(MyApplication.selectedProduct!!.product.unit!!.visitProducts.size>0)
                unitId = MyApplication.selectedProduct!!.product.unit!!.visitProducts.get(0).unitId
            prodId = MyApplication.selectedProduct!!.productId
            binding!!.btAddProduct.text = getString(R.string.update)
            if(MyApplication.selectedProduct!!.product.name!!.contains(AppConstants.OTHER_PRODUCT_NAME)){
                binding!!.etCustomProductName.text = MyApplication.selectedProduct!!.customProductName!!.toEditable()
                binding!!.etCustomProductDesc.text = MyApplication.selectedProduct!!.customProductDescription!!.toEditable()
                binding!!.llCustom.show()
            }
            binding!!.rlProductName.setBackgroundResource(R.drawable.disabled_rounded)
            binding!!.rlProductName.isEnabled = false
        }else{
            binding!!.rlProductName.isEnabled = true
            binding!!.llProductDelete.hide()
            arraySer.add(SerialItem(""))
        }
        binding!!.rvSerialised.layoutManager = LinearLayoutManager(this)
        adapterSer = AdapterEdit(arraySer,this,this)
        binding!!.rvSerialised.adapter = adapterSer
    }

    fun setUpStatusReasonSpinner() {
        arraySpin.clear()
        arrSpinner.add(0,ItemSpinner(0,getString(R.string._select_)))
        adapterSpin =
            AdapterSpinner(this, R.layout.spinner_text_item, arrSpinner)
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
            val pos = arrSpinner.indexOf(
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
        if(MyApplication.selectedProduct!=null) {
            for (item in arr.indices) {
                if (arr.get(item).id == MyApplication.selectedProduct!!.product.id) {
                    selectedPos = item
                }
            }
        }
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
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java).getUnits(AppConstants.PRODUCTION_LOOKUP_CODE)
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
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java).getAllProducts()
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
        val cal = Calendar.getInstance()
        val array:ArrayList<String> = arrayListOf()
        for(item in arraySer)
            array.add(item.serial!!)
        createProduct = CreateProduct(
            simp.format(cal.time),
           "" ,
            "",
            0,
            0,
            false,
            simp.format(cal.time),
            "",
            prodId ,
            binding!!.etQuantity.text.toString().toInt(),
            if(array.size >0)
                0
            else
                array.get(0).toInt()  ,
            array,
            unitId,
            MyApplication.selectedVisit!!.id,
            MyApplication.selectedVisit!!.number
        )

        if(this.arr.get(selectedPos).name!!.contains(AppConstants.OTHER_PRODUCT_NAME)){
            createProduct!!.customProductName = binding!!.etCustomProductName.text.toString()
            createProduct!!.customProductDescription = binding!!.etCustomProductDesc.text.toString()
        }else{
            createProduct!!.customProductName = this.arr.get(selectedPos).name
        }
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java)
            .createMobile(
                createProduct!!
            ).enqueue(object : Callback<ResponseMessage>{
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if(response.body()!!.success!!.equals("true")){
                        binding!!.llLoading.hide()
                        createRetryDialog(response.body()!!.message!!){
                            finish()
                        }
                    }else{
                        binding!!.llLoading.hide()
                        createDialog(response.body()!!.message!!)
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                   binding!!.llLoading.hide()
                }

            })
    }

    fun editProduct(){
        binding!!.llLoading.show()
        val cal = Calendar.getInstance()
        val array:ArrayList<String> = arrayListOf()
        for(item in arraySer)
            array.add(item.serial!!)
        createProduct = CreateProduct(
            simp.format(cal.time),
           "" ,
           "",
            0,
            MyApplication.selectedProduct!!.id,
            false,
            simp.format(cal.time),
            "",
            prodId ,
            binding!!.etQuantity.text.toString().toInt(),
            if(array.size >0)
                0
            else
                array.get(0).toInt()  ,
            array,
            unitId,
            MyApplication.selectedVisit!!.id,
            MyApplication.selectedVisit!!.number
        )

        if(this.arr.get(selectedPos).name!!.contains(AppConstants.OTHER_PRODUCT_NAME)){
            createProduct!!.customProductName = binding!!.etCustomProductName.text.toString()
            createProduct!!.customProductDescription = binding!!.etCustomProductDesc.text.toString()
        }


        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java)
            .updateProduct(
                createProduct!!
            ).enqueue(object : Callback<ResponseMessage>{
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if(response.body()!!.success!!.equals("true")){
                        createRetryDialog(
                            response.body()!!.message!!){
                            finish()
                        }
                    }else{
                        createDialog(response.body()!!.message!!)
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
        binding!!.rlAddProdcut.setOnClickListener {
            if(binding!!.llNameSelect.visibility == View.VISIBLE){
                binding!!.llNameSelect.hide()
            }
        }
        binding!!.etQuantity.doOnTextChanged { text, start, before, count ->
            arraySer.clear()
            var ct = 1
            if(text!=null && text.length >0) {
                while (ct <= text.toString().toInt()) {
                    arraySer.add(SerialItem(""))
                    ct++
                }
                adapterSer!!.notifyDataSetChanged()
            }else{
                arraySer.add(SerialItem(""))
                adapterSer!!.notifyDataSetChanged()
            }
        }

        binding!!.etFilterName.doOnTextChanged { text, start, before, count ->

            val items = array.filter {
                it.name!!.contains(text!!,true)
            }
            val arrayList : ArrayList<ProductAllListItem> = arrayListOf()
            arrayList.addAll(items)
            setUpProduct(arrayList)
        }


        binding!!.btAddProduct.setOnClickListener {
            var serialised_filled = true
            for(item in arraySer)
                if(item.serial.isNullOrEmpty())
                    serialised_filled = false

            if(prodId!=0 && unitId!=0 && !binding!!.etQuantity.text.toString().isEmpty() && binding!!.etQuantity.text!!.toString().toInt() >0 && serialised_filled && binding!!.tvProduct.text.toString().trim() != getString(R.string.product) && !(arr.get(selectedPos).name!!.contains(AppConstants.OTHER_PRODUCT_NAME) && (binding!!.etCustomProductDesc.text.isNullOrEmpty() || binding!!.etCustomProductName.text.isNullOrEmpty()))) {
                if(MyApplication.selectedProduct==null)
                    addProduct()
                else
                    editProduct()
            }else{
                if(binding!!.etQuantity.text!!.toString().toInt()>0)
                    createDialog(getString(R.string.fill_details))
                else{
                    createDialog(getString(R.string.quantity_more_0))
                }
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
            createActionDialog(
                getString(R.string.you_wanna_delete),
                0
            ){
                deleteProduct()
            }
        }
    }

    fun deleteProduct(){
        binding!!.llLoading.show()
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java).deleteProduct(
            MyApplication.selectedProduct!!.id!!
        ).enqueue(object : Callback<ResponseMessage> {
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
            val old = arr.get(position).selected
            safeCall {
                arr.find {
                    it.selected!!
                }!!.selected = false
                array.find {
                    it.selected!!
                }!!.selected = false
            }

            arr.get(position).selected = !old!!
            array.find {
                it.id == arr.get(position).id
            }!!.selected = arr.get(position).selected
            prodId = arr.get(position).id
            mainPos = array.indexOf(array.find {
                it.id == arr.get(position).id
            })
            if (arr[position].selected!!)
            binding!!.tvProduct.text = arr.get(position).name
            else  binding!!.tvProduct.text = getString(R.string.product)
            binding!!.llNameSelect.hide()

            if(arr.get(position).name!!.contains(AppConstants.OTHER_PRODUCT_NAME)){
                binding!!.llCustom.show()
                selectedPos = position
            }else{
                binding!!.llCustom.hide()
                selectedPos = position
            }

            adapter!!.notifyDataSetChanged()
        }
    }
}