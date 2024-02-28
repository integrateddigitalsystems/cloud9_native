package com.ids.cloud9native.controller.Fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.adapters.AdapterProducts
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.controller.activities.ActivityAddProduct
import com.ids.cloud9native.controller.activities.ActivityRecords
import com.ids.cloud9native.controller.activities.ActivityReportDetails
import com.ids.cloud9native.controller.adapters.AdapterDialog
import com.ids.cloud9native.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9native.databinding.LayoutProductsBinding
import com.ids.cloud9native.databinding.ReasonDialogBinding


import com.ids.cloud9native.model.*
import com.ids.cloud9native.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class FragmentProducts : Fragment(), RVOnItemClickListener {

    var binding: LayoutProductsBinding? = null
    var ctProd = 0
    var arrSpin = arrayListOf<ItemSpinner>()
    var adapterProd: AdapterProducts? = null
    var alertDialog: AlertDialog? = null
    var adapterDialog: AdapterDialog? = null
    var arrayProd: ArrayList<ProductListItem> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutProductsBinding.inflate(inflater, container, false)
        return binding!!.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    fun setUpDataVisit(position: Int) {
        arrSpin.clear()
        for (item in arrayProd.get(position).reports) {
            arrSpin.add(ItemSpinner(item.id, item.name, item.selected, true, false, position, null))
        }
        val builder = AlertDialog.Builder(requireActivity())
        val popupItemMultipleBinding = ReasonDialogBinding.inflate(layoutInflater)
        popupItemMultipleBinding.rvReasonStatus.layoutManager =
            LinearLayoutManager(requireActivity())
        adapterDialog = AdapterDialog(arrSpin, requireActivity(), this, true)
        popupItemMultipleBinding.rvReasonStatus.adapter = adapterDialog
        builder.setView(popupItemMultipleBinding.root)
        alertDialog = builder.create()
        alertDialog!!.setCanceledOnTouchOutside(true)
        safeCall {
            alertDialog!!.show()
            alertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
    fun init() {
      /*  getProducts()*/

        if (MyApplication.selectedVisit!!.reasonId ==  AppHelper.getReasonID(AppConstants.REASON_PENDING) || MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_COMPLETED) || MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_ON_THE_WAY) || MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_SCHEDULED)) {
            binding!!.llButton.setBackgroundResource(R.color.disabled_primary)
            binding!!.btAddProducts.isEnabled = false
        }
        binding!!.btAddProducts.setOnClickListener {
            MyApplication.selectedProduct = null
            startActivity(
                Intent(
                    requireActivity(),
                    ActivityAddProduct::class.java

                )
            )
        }
        binding!!.srProducts.setOnRefreshListener{
            getProducts()
        }
    }
    fun getProducts() {
        binding!!.llLoading.show()
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java).getProducts(
            MyApplication.selectedVisit!!.id!!
        ).enqueue(object : Callback<ProductList> {
            override fun onResponse(call: Call<ProductList>, response: Response<ProductList>) {
                if (response.isSuccessful){
                    arrayProd.clear()
                    arrayProd.addAll(response.body()!!)
                    setUpProducts()
                }
                else binding!!.llLoading.hide()

            }
            override fun onFailure(call: Call<ProductList>, throwable: Throwable) {
                binding!!.llLoading.hide()
            }
        })
    }
    fun setUpProductsPage() {
        safeCall {
        if (arrayProd.size > 0) {
            binding!!.tvNoData.hide()
            binding!!.rvProducts.show()
            binding!!.rvProducts.layoutManager = LinearLayoutManager(requireContext())
            adapterProd = AdapterProducts(arrayProd, requireActivity(), this)
            binding!!.rvProducts.adapter = adapterProd
        } else {
            binding!!.tvNoData.show()
            binding!!.rvProducts.hide()
        }
        binding!!.llLoading.hide()
        }
    }
    fun setUpProducts() {
        ctProd = 0
        if (arrayProd.size > 0) {
            for (i in arrayProd.indices) {
                arrayProd.get(i).reports = arrayListOf()
                getReports(i)
            }
        } else {
            setUpProductsPage()
            binding!!.llLoading.hide()
        }
        binding!!.srProducts.isRefreshing = false
    }
    fun getReports(position: Int) {
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java).getFormByVisitProductId(
            arrayProd.get(position).id!!
        ).enqueue(object : Callback<Forms>{
            override fun onResponse(
                call: Call<Forms>, response: Response<Forms>
            ) {
                if (response.isSuccessful){
                    if (response.body()!!.size > 0) {
                        arrayProd.get(position).reports.clear()
                        arrayProd.get(position).reports.addAll(response.body()!!)
                    }

                    ctProd++
                    if (ctProd == arrayProd.size) {
                        setUpProductsPage()
                    }
                }
                else  binding!!.llLoading.hide()

            }
            override fun onFailure(call: Call<Forms>, t: Throwable) {
                binding!!.llLoading.hide()
            }
        })
    }
    override fun onResume() {
        super.onResume()
        MyApplication.selectedProduct = null
        getProducts()
    }
    fun deleteProduct(pos: Int) {
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java).deleteProduct(
            arrayProd.get(pos).id!!
        ).enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(
                call: Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                createDialog( response.body()!!.message!!)

                if (response.body()!!.success.equals("true"))
                    getProducts()
            }

            override fun onFailure(call: Call<ResponseMessage>, throwable: Throwable) {
                binding!!.llLoading.hide()
            }
        })
    }
    override fun onItemClicked(view: View, position: Int) {
        if (view.id == R.id.btEditProduct) {
            if (MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_ARRIVED)) {
                MyApplication.selectedProduct = arrayProd.get(position)
                startActivity(
                    Intent(
                        requireActivity(),
                        ActivityAddProduct::class.java
                    )
                )
            }
        } else if (view.id == R.id.btClose) {
            if (MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_ARRIVED)) {
                requireContext().createActionDialog(
                    getString(R.string.you_wanna_delete),
                    0
                ){
                    deleteProduct(position)
                }
            }
        }else if(view.id == R.id.btRecords){
            MyApplication.selectedProduct = arrayProd.get(position)
            startActivity(Intent(requireContext(),ActivityRecords::class.java))
        }
        else if (view.id == R.id.llJobReport) {
            if (arrayProd.get(position).reports.size > 0)
                setUpDataVisit(position)
        } else if (view.id == R.id.llItemReason) {
            alertDialog!!.dismiss()
            arrSpin.get(position).selected = true
            for (item in arrayProd.get(arrSpin.get(position).type!!).reports)
                item.selected = false
            arrayProd.get(arrSpin.get(position).type!!).reports.get(position).selected = true
            adapterDialog!!.notifyItemChanged(position)
            adapterProd!!.notifyItemChanged(position)
        } else if (view.id == R.id.ivReport) {
            if (MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_ARRIVED)) {
                MyApplication.selectedProduct = arrayProd.get(position)
                    val ct = arrayProd.get(position).reports.count {
                        it.selected
                    }
                    if (ct > 0) {
                        var url:String?=null
                        var id:Int?=null
                        safeCall {
                            url =  arrayProd.get(position).reports.find {
                                it.selected
                            }!!.url
                        }
                        safeCall {
                                id =  arrayProd.get(position).reports.find {
                                    it.selected
                                }!!.id

                            }

                        if (url==null || id==null) {
                            createDialog(getString(R.string.empty_report))
                        }
                        else{
                            startActivity(Intent(requireContext(), ActivityReportDetails::class.java).putExtra("RepId",
                                id
                            ).putExtra("url",  url)
                            )
                        }


                    }


            }
        }
    }
}