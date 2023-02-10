package com.ids.cloud9.controller.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ids.cloud9.R
import com.ids.cloud9.controller.adapters.AdapterProducts
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.activities.ActivityAddProduct
import com.ids.cloud9.controller.activities.ActivtyVisitDetails
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.LayoutProductsBinding


import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class FragmentProducts : Fragment() , RVOnItemClickListener {

    var binding : LayoutProductsBinding?=null
    var ctProd = 0
    var arrayProd: ArrayList<ProductListItem> = arrayListOf()
    var arrayReccomend: ArrayList<ActivitiesListItem> = arrayListOf()

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


    fun init(){
        getProducts()

        binding!!.btAddProducts.setOnClickListener {
            MyApplication.selectedProduct = null
            startActivity(
                Intent(
                    requireActivity(),
                    ActivityAddProduct::class.java

                )
            )
            //  overridePendingTransition(R.anim.cycling ,R.anim.cycling)
        }
    }

    fun getProducts() {
       binding!!.llLoading.show()
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getProducts(
            MyApplication.selectedVisit!!.id!!
        )?.enqueue(object : Callback<ProductList> {
            override fun onResponse(call: Call<ProductList>, response: Response<ProductList>) {
                arrayProd.clear()
                arrayProd.addAll(response.body()!!)
                setUpProducts()


            }

            override fun onFailure(call: Call<ProductList>, throwable: Throwable) {

                binding!!.llLoading.hide()
            }
        })
    }

    fun setUpProductsPage() {
        binding!!.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding!!.rvProducts.adapter = AdapterProducts(arrayProd, requireActivity(), this)
        binding!!.llLoading.hide()
    }

    fun setUpProducts() {
        ctProd = 0
        if (arrayProd.size > 0) {
            for (i in arrayProd.indices) {
                arrayProd.get(i).reports = arrayListOf()
                getReports(i)
            }
        } else {
           binding!!.llLoading.hide()
        }
    }

    fun getReports(position: Int) {

        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getReports(
            arrayProd.get(position).product.categoryId
        )?.enqueue(object : Callback<ArrayList<Report>> {
            override fun onResponse(
                call: Call<ArrayList<Report>>,
                response: Response<ArrayList<Report>>
            ) {
                if (response.body()!!.size > 0) {
                    arrayProd.get(position).reports.clear()
                    arrayProd.get(position).reports.addAll(response.body()!!)

                }

                ctProd++
                if (ctProd == arrayProd.size) {
                    setUpProductsPage()
                }

            }

            override fun onFailure(call: Call<ArrayList<Report>>, throwable: Throwable) {

               binding!!.llLoading.hide()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        getProducts()
    }

    fun deleteProduct(pos:Int){
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).deleteProduct(
            arrayProd.get(pos).id
        )?.enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(
                call: Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                AppHelper.createDialogPositive(requireActivity(),response.body()!!.message!!)

                if(response.body()!!.success.equals("true"))
                    getProducts()

            }

            override fun onFailure(call: Call<ResponseMessage>, throwable: Throwable) {

               binding!!.llLoading.hide()
            }
        })
    }

    override fun onItemClicked(view: View, position: Int) {
        if(view.id == R.id.btEditProduct){
            MyApplication.selectedProduct = arrayProd.get(position)
            startActivity(Intent(
                requireActivity(),
                ActivityAddProduct::class.java
            )
            )
        }else if(view.id == R.id.btClose){
            AppHelper.createYesNoDialog(requireActivity(),getString(R.string.you_wanna_delete),0){
                deleteProduct(position)
            }
        }
    }
}