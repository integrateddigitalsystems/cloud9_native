package com.ids.cloud9.controller.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.AdapterFilteredReccomendations
import com.ids.cloud9.controller.adapters.AdapterRecords
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.custom.AppCompactBase
import com.ids.cloud9.databinding.LayoutReccomendationsBinding
import com.ids.cloud9.model.FilteredActivityList
import com.ids.cloud9.model.FilteredActivityListItem
import com.ids.cloud9.model.RecordLists
import com.ids.cloud9.model.RecordListsItem
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityRecords : AppCompactBase(), RVOnItemClickListener {

    var binding : LayoutReccomendationsBinding?=null
    var prodId : Int ?=0
    var records : ArrayList<RecordListsItem> = arrayListOf()
    var arrayReccomend : ArrayList<FilteredActivityListItem> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutReccomendationsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
        listeners()
    }
    override fun onResume() {
        super.onResume()
    }
    fun init(){
        getRecords()
    }
    fun getRecords(){
        binding!!.llLoading.show()
        RetrofitClientAuth.client!!
            .create(RetrofitInterface::class.java)
            .getProductRecords(
                MyApplication.selectedProduct!!.id
            ).enqueue(object : Callback<RecordLists>{
                override fun onResponse(call: Call<RecordLists>, response: Response<RecordLists>) {
                    records.clear()
                    records.addAll(response.body()!!)
                    setUpRecord()
                }

                override fun onFailure(call: Call<RecordLists>, t: Throwable) {
                    binding!!.llLoading.hide()
                }

            })
    }
    fun setUpRecord(){
        binding!!.llLoading.hide()
        if(records.size >0){
            var adapter = AdapterRecords(records,this,this)
            binding!!.rvReccomendations.layoutManager = LinearLayoutManager(this)
            binding!!.rvReccomendations.adapter = adapter
            binding!!.rvReccomendations.show()
            binding!!.tvNotasks.hide()
        }else{
            binding!!.rvReccomendations.hide()
            binding!!.tvNotasks.show()
        }
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

    }
}