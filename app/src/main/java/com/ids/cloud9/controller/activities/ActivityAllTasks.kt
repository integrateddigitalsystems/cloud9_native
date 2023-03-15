package com.ids.cloud9.controller.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.AdapterFilteredReccomendations
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.custom.AppCompactBase
import com.ids.cloud9.databinding.LayoutReccomendationsBinding
import com.ids.cloud9.model.FilteredActivityList
import com.ids.cloud9.model.FilteredActivityListItem
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityAllTasks : AppCompactBase(),RVOnItemClickListener {

    var binding : LayoutReccomendationsBinding?=null
    var arrayReccomend : ArrayList<FilteredActivityListItem> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutReccomendationsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        listeners()
    }
    override fun onResume() {
        super.onResume()
        getReccomendations()
    }
    fun listeners(){
        binding!!.layoutTool.show()
        binding!!.llTool.ivDrawer.hide()
        binding!!.llTool.layoutFragment.show()
        binding!!.llTool.tvTitleTool.text = getString(R.string.all_tasks)
        binding!!.layoutAllTasks.setBackgroundResource(R.color.gray_app_bg)
        binding!!.llTool.btBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding!!.srReccomendations.setOnRefreshListener{
            getReccomendations()
        }
        binding!!.llTool.ivCalendar.setOnClickListener {
            finishAffinity()
            startActivity(Intent(
                this,
                ActivityMain::class.java
            ))
        }
        binding!!.btAddReccomend.hide()
    }
    private fun getReccomendations() {
        binding!!.llLoading.show()
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getReccomendations(
            MyApplication.userItem!!.applicationUserId!!.toInt()
        ).enqueue(object : Callback<FilteredActivityList> {
            override fun onResponse(
                call: Call<FilteredActivityList>,
                response: Response<FilteredActivityList>
            ) {
                arrayReccomend.clear()
                arrayReccomend.addAll(response.body()!!)
                setDataReccomend()
            }
            override fun onFailure(call: Call<FilteredActivityList>, t: Throwable) {
                binding!!.llLoading.hide()
            }
        })
    }
    fun setDataReccomend(){
        if(arrayReccomend.size > 0) {
            binding!!.tvNotasks.hide()
            binding!!.rvReccomendations.show()
            binding!!.rvReccomendations.layoutManager = LinearLayoutManager(this)
            binding!!.rvReccomendations.adapter = AdapterFilteredReccomendations(arrayReccomend, this, this)
        }else{
            binding!!.tvNotasks.show()
            binding!!.rvReccomendations.hide()
        }
        binding!!.llLoading.hide()
        binding!!.srReccomendations.isRefreshing = false
    }
    override fun onItemClicked(view: View, position: Int) {
        val ct = MyApplication.allVisits.count {
            it.title.equals(arrayReccomend[position].entity) && it.reasonId==AppConstants.ARRIVED_REASON_ID
        }
        if(ct>0) {
            MyApplication.selectedReccomend = arrayReccomend[position]
            startActivity(
                Intent(
                    this,
                    ActivityAddReccomendations::class.java
                )
            )
        }else{
            createDialog( getString(R.string.visit_arrived_completed))
        }
    }
}