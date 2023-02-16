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

class ActivityRecords : AppCompactBase(), RVOnItemClickListener {

    var binding : LayoutReccomendationsBinding?=null
    var arrayReccomend : ArrayList<FilteredActivityListItem> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutReccomendationsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        // getReccomendations()
        listeners()

    }

    override fun onResume() {
        super.onResume()
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