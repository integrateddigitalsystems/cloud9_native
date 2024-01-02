package com.ids.cloud9native.controller.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.controller.adapters.AdapterRecords
import com.ids.cloud9native.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9native.custom.AppCompactBase
import com.ids.cloud9native.databinding.LayoutReccomendationsBinding
import com.ids.cloud9native.model.RecordLists
import com.ids.cloud9native.model.RecordListsItem
import com.ids.cloud9native.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityRecords : AppCompactBase(), RVOnItemClickListener {

    var binding : LayoutReccomendationsBinding?=null
    var records : ArrayList<RecordListsItem> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutReccomendationsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
        listeners()
    }
    override fun onResume() {
        super.onResume()
        getRecords()
        MyApplication.activityResumed()
    }

    override fun onPause() {
        super.onPause()
        MyApplication.activityPaused()
    }
    fun init(){

        binding!!.srReccomendations.setOnRefreshListener{
            getRecords()
        }
    }
    fun getRecords(){
        binding!!.llLoading.show()
        RetrofitClientSpecificAuth.client!!
            .create(RetrofitInterface::class.java)
            .getProductRecords(
                MyApplication.selectedProduct!!.id!!
            ).enqueue(object : Callback<RecordLists>{
                override fun onResponse(call: Call<RecordLists>, response: Response<RecordLists>) {
                    if (response.isSuccessful){
                        records.clear()
                        records.addAll(response.body()!!)
                        setUpRecord()
                    }
                    else  binding!!.llLoading.hide()
                }
                override fun onFailure(call: Call<RecordLists>, t: Throwable) {
                    binding!!.llLoading.hide()
                }

            })
    }
    fun setUpRecord(){
        binding!!.llLoading.hide()
        if(records.size >0){
            val adapter = AdapterRecords(records,this,this)
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
        binding!!.llTool.tvTitleTool.text = MyApplication.selectedProduct!!.product.name
        binding!!.tvNotasks.text = getString(R.string.no_records)
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
            startActivity(Intent(this, ActivityReportDetails::class.java)
                .putExtra("RepId", records.get(position).formId)
                .putExtra("url",  records.get(position).url))
    }
}