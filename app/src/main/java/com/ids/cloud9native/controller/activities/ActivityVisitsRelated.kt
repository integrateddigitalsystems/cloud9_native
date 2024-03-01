package com.ids.cloud9native.controller.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.controller.adapters.AdapterRecords
import com.ids.cloud9native.controller.adapters.AdapterRelatedVisits
import com.ids.cloud9native.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9native.custom.AppCompactBase
import com.ids.cloud9native.databinding.LayoutReccomendationsBinding
import com.ids.cloud9native.model.RecordLists
import com.ids.cloud9native.model.RecordListsItem
import com.ids.cloud9native.model.ResponseRelatedVisits
import com.ids.cloud9native.model.ResponseRelatedVisitsItem
import com.ids.cloud9native.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityVisitsRelated : AppCompactBase(), RVOnItemClickListener {

    var binding : LayoutReccomendationsBinding?=null
    var responseRelatedVisitsItem : ArrayList<ResponseRelatedVisitsItem> = arrayListOf()
    var serialNumber =""
    var contractId =0
    var productId =0
    var productName =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutReccomendationsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
        listeners()
    }
    fun init(){
        val extras = intent.extras
        safeCall {
            if (extras!=null){
                contractId = extras.getInt("contractId")
                productId = extras.getInt("productId")
                serialNumber = extras.getString("serialNumber","")
                productName = extras.getString("productName","")
            }
        }
        getVisitsByFilter()
        binding!!.srReccomendations.setOnRefreshListener{
            getVisitsByFilter()
        }
    }
    fun getVisitsByFilter(){
        binding!!.llLoading.show()
        RetrofitClientSpecificAuth.client!!
            .create(RetrofitInterface::class.java)
            .getVisitsByFilter(
                contractId,
                productId,
                serialNumber,
            ).enqueue(object : Callback<ResponseRelatedVisits>{
                override fun onResponse(call: Call<ResponseRelatedVisits>, response: Response<ResponseRelatedVisits>) {
                    if (response.isSuccessful){
                        binding!!.llLoading.hide()
                        responseRelatedVisitsItem.clear()
                        responseRelatedVisitsItem.addAll(response.body()!!)
                        setUpVititsRelated()
                    }
                    else {
                        createDialog(response.message())
                        binding!!.llLoading.hide()
                        binding!!.tvNotasks.show()
                    }
                }
                override fun onFailure(call: Call<ResponseRelatedVisits>, t: Throwable) {
                    binding!!.llLoading.hide()
                }

            })
    }
    fun setUpVititsRelated(){
        binding!!.llLoading.hide()
        val visitSelected =responseRelatedVisitsItem.find { it.id ==MyApplication.selectedVisit!!.id}
        if (visitSelected!=null)
            responseRelatedVisitsItem.remove(visitSelected)
        if(responseRelatedVisitsItem.size >0){
            val adapter = AdapterRelatedVisits(this,responseRelatedVisitsItem,this)
            binding!!.rvReccomendations.layoutManager = LinearLayoutManager(this)
            binding!!.rvReccomendations.adapter = adapter
            binding!!.rvReccomendations.show()
            binding!!.tvNotasks.hide()
        }else{
            binding!!.rvReccomendations.hide()
            binding!!.tvNotasks.show()
        }
        binding!!.srReccomendations.isRefreshing = false
    }
    fun listeners(){
        binding!!.layoutTool.show()
        binding!!.llTool.ivDrawer.hide()
        binding!!.llTool.layoutFragment.show()
        binding!!.llTool.tvTitleTool.text = productName
        binding!!.tvNotasks.text = getString(R.string.no_data)
        binding!!.layoutAllTasks.setBackgroundResource(R.color.gray_app_bg)
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
        binding!!.btAddReccomend.hide()
    }
    override fun onItemClicked(view: View, position: Int) {
        startActivity(Intent(this,ActivityRecords::class.java)
            .putExtra("name",productName)
            .putExtra("visitId",responseRelatedVisitsItem[position].id)
            .putExtra("productId",productId)
            .putExtra("serialNumber",serialNumber)
            .putExtra("isVisitProduct",true))


    }
}