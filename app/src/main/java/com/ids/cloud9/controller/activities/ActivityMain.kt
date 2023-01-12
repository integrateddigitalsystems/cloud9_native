package com.ids.cloud9.controller.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.ids.cloud9.Adapters.AdapterVisitDate
import com.ids.cloud9.Adapters.AdapterVisits
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ActivityMainBinding
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import com.shrikanthravi.customnavigationdrawer2.data.MenuItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


class ActivityMain: Activity() , RVOnItemClickListener{

    var simp = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
    var binding : ActivityMainBinding?=null
    var array : ArrayList<VisitDates> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        listeners()
        setUpSliding()
        getVisits()

    }



    fun setUpData(list: VisitList){
        for(item in list){
            var date = simp.parse(item.visitDate)
            var itm = array.find { it.dateMill == date.time }
            if(itm!=null){
                array[array.indexOf(itm)].visits!!.add(item)
            }else{
                array.add(VisitDates(date.time,VisitList()))
                array.get(array.size - 1 ).visits!!.add(item)
            }

        }



        var adapter = AdapterVisitDate(array,this,this)
        binding!!.llHomeMain.rvVisits.layoutManager = LinearLayoutManager(this)
        binding!!.llHomeMain.rvVisits.adapter = adapter

        Log.wtf("Visitors",Gson().toJson(array))
        binding!!.llHomeMain.loading.hide()
    }


    fun getVisits(){
        binding!!.llHomeMain.loading.show()
        RetrofitClientAuth.client?.create(RetrofitInterface::class.java)
            ?.getVisits(
               -1,
                MyApplication.userItem!!.applicationUserId!!.toInt()
            )?.enqueue(object : Callback<VisitList> {
                override fun onResponse(call: Call<VisitList>, response: Response<VisitList>) {
                   setUpData(response.body()!!)

                }
                override fun onFailure(call: Call<VisitList>, throwable: Throwable) {

                }
            })
    }


    fun listeners(){
        binding!!.llHomeMain.ivDrawer.setOnClickListener {
            var  shake =  AnimationUtils.loadAnimation(this, R.anim.corner)
            binding!!.navView.startAnimation(shake)
            binding!!.drawerLayout.openDrawer(GravityCompat.START)
        }


        binding!!.drawerMenu.btClose.setOnClickListener {
            var  shake =  AnimationUtils.loadAnimation(this, R.anim.close_corner)
            binding!!.navView.startAnimation(shake)
            Handler(Looper.getMainLooper()).postDelayed({
                binding!!.drawerLayout.closeDrawers()
            }, 300)

        }

        binding!!.llHomeMain.btDatePrevious.setOnClickListener {
           binding!!.llHomeMain.btDatePrevious.setBackgroundResource(R.drawable.rounded_darker_left)
            binding!!.llHomeMain.btDateNext.setBackgroundResource(R.drawable.rounded_dark_right)
        }

        binding!!.llHomeMain.btDateNext.setOnClickListener {
            binding!!.llHomeMain.btDateNext.setBackgroundResource(R.drawable.rounded_darker_right)
            binding!!.llHomeMain.btDatePrevious.setBackgroundResource(R.drawable.rounded_dark_left)
        }
    }



    fun setUpSliding(){
        /*var menuItems : ArrayList<MenuItem> ?= arrayListOf()
        menuItems!!.add(MenuItem("News", com.ids.cloud9.R.drawable.background))
        menuItems.add(MenuItem("Feed", com.ids.cloud9.R.drawable.background))
        menuItems.add(MenuItem("Messages",com.ids.cloud9.R.drawable.background))
        menuItems.add(MenuItem("Music",com.ids.cloud9.R.drawable.background))

        //then add them to navigation drawer


        //then add them to navigation drawer
        binding!!.navigationDrawer.setMenuItemList(menuItems)
        binding!!.navigationDrawer.
                setAppbarTitleTypeface(AppHelper.getTypeFace(this))


        binding!!.navigationDrawer.setOnMenuItemClickListener {

        }*/

    }

    override fun onItemClicked(view: View, position: Int) {

    }
}