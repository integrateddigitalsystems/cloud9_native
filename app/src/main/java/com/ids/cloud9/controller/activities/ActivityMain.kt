package com.ids.cloud9.controller.activities

import HeaderDecoration
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.ids.cloud9.controller.adapters.StickyAdapter
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ActivityMainBinding
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ActivityMain: AppCompatActivity() , RVOnItemClickListener{

    var simp = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
    var half = SimpleDateFormat("MMM dd")
    var secondHalf = SimpleDateFormat("MMM dd yyyy")
    var secondNoMonthHalf = SimpleDateFormat("dd, yyyy")
    var binding : ActivityMainBinding?=null
    var fromDefault = Calendar.getInstance()
    var toDefault = Calendar.getInstance()
    var prevHeaders : ArrayList<Int> = arrayListOf()
    var editingFrom = Calendar.getInstance()
    var editingTo = Calendar.getInstance()
    var tempArray : ArrayList<VisitListItem> = arrayListOf()
    var mainArray : ArrayList<VisitListItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        listeners()
        setUpSliding()
        getVisits()
        setUpDate()
        setUpDrawer()





    }


    fun editDate(isNext : Boolean ){
        if(isNext){
            var oldEdge = editingTo.time
            editingFrom.time = oldEdge
            editingTo.time = oldEdge
            editingFrom.add(Calendar.DAY_OF_MONTH,1)
            editingTo.add(Calendar.DAY_OF_MONTH,7)

        }else{
            var oldEdge = editingFrom.time
            editingFrom.time = oldEdge
            editingTo.time = oldEdge
            editingFrom.add(Calendar.DAY_OF_MONTH,-7)
            editingTo.add(Calendar.DAY_OF_MONTH,-1)
        }

        editingFrom.set(Calendar.HOUR,0)
        editingFrom.set(Calendar.HOUR_OF_DAY,0)
        editingFrom.set(Calendar.MINUTE,0)
        editingFrom.set(Calendar.SECOND,0)
        editingFrom.set(Calendar.MILLISECOND,0)

        editingTo.set(Calendar.HOUR,0)
        editingTo.set(Calendar.HOUR_OF_DAY,0)
        editingTo.set(Calendar.MINUTE,0)
        editingTo.set(Calendar.SECOND,0)
        editingTo.set(Calendar.MILLISECOND,0)

        var curr = Calendar.getInstance()
        curr.set(Calendar.HOUR,0)
        curr.set(Calendar.HOUR_OF_DAY,0)
        curr.set(Calendar.MINUTE,0)
        curr.set(Calendar.SECOND,0)
        curr.set(Calendar.MILLISECOND,0)

        if(curr.time.time >= editingFrom.time.time && curr.time.time <= editingTo.time.time){
            binding!!.llHomeMain.tvToday.setBackgroundResource(R.drawable.rounded_selected)
        }else{
            binding!!.llHomeMain.tvToday.setBackgroundResource(R.drawable.rounded_dark_blue)
        }


        var vl = VisitList()
        binding!!.llHomeMain.loading.show()
        filterDate(mainArray)
        if(editingFrom.get(Calendar.MONTH) != editingTo.get(Calendar.MONTH))
            binding!!.llHomeMain.tvDate.text = half.format(editingFrom.time) + " - "+secondHalf.format(editingTo.time)
        else
            binding!!.llHomeMain.tvDate.text = half.format(editingFrom.time) + " - "+secondNoMonthHalf.format(editingTo.time)
    }


    fun setUpDate(){
        var from = Calendar.getInstance()
        var to = Calendar.getInstance()

        from.add(Calendar.DAY_OF_MONTH,-2)
        to.add(Calendar.DAY_OF_MONTH,3)

        fromDefault = from
        toDefault = to
        editingFrom = from
        editingTo = to

        editingFrom.set(Calendar.HOUR,0)
        editingFrom.set(Calendar.HOUR_OF_DAY,0)
        editingFrom.set(Calendar.MINUTE,0)
        editingFrom.set(Calendar.SECOND,0)
        editingFrom.set(Calendar.MILLISECOND,0)

        editingTo.set(Calendar.HOUR,0)
        editingTo.set(Calendar.HOUR_OF_DAY,0)
        editingTo.set(Calendar.MINUTE,0)
        editingTo.set(Calendar.SECOND,0)
        editingTo.set(Calendar.MILLISECOND,0)

        filterDate(mainArray)

        if(editingFrom.get(Calendar.MONTH) != editingTo.get(Calendar.MONTH))
            binding!!.llHomeMain.tvDate.text = half.format(editingFrom.time) + " - "+secondHalf.format(editingTo.time)
        else
            binding!!.llHomeMain.tvDate.text = half.format(editingFrom.time) + " - "+secondNoMonthHalf.format(editingTo.time)
    }




    fun filterDate(arrayList: ArrayList<VisitListItem>){

        tempArray.clear()

        tempArray.addAll(arrayList.filter {

            simp.parse(it.visitDate).time >= editingFrom.time.time && simp.parse(it.visitDate).time <= editingTo.time.time
        }
        )
        Log.wtf("TAG_ARRAY",tempArray.size.toString())
        var vl = VisitList()
        vl.addAll(tempArray)
        setUpData(vl)
    }

    fun setUpDrawer(){
       // binding!!.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    fun setUpData(list: VisitList){
        if(list.size >0) {
            list.sortBy {
                simp.parse(it.visitDate).time
            }
            tempArray.clear()
            var headers = 0
            for (item in list) {
                var date = simp.parse(item.visitDate)
                Log.wtf("TAG_VISIT", item.title + " ---DATE: " + date.time)
                var itm = tempArray.find { it.dateMill == date.time }
                if (itm != null) {
                    var nextHead = tempArray.find {
                        it.headerOrder == (itm.headerOrder + 1)
                    }
                    if (nextHead == null)
                        tempArray.add(item)
                    else {
                        var indx = tempArray.indexOf(nextHead)
                        tempArray.add(indx, item)
                    }
                } else {
                    var visHeader = VisitListItem()
                    visHeader.isHeader = true
                    visHeader.visitDate = item.visitDate
                    visHeader.dateMill = date.time
                    visHeader.headerOrder = headers
                    headers++
                    tempArray.add(visHeader)
                    tempArray.add(item)



                }

            }
        }else{
            tempArray.clear()
        }


        var x : ArrayList<VisitListItem> = arrayListOf()
        x.addAll(tempArray)

        tempArray.clear()
        tempArray.addAll(x.filter {
            it.reasonId != AppConstants.SCHEDULED_REASON_ID
        })







        if(tempArray.size > 0) {

            var adapter = StickyAdapter(this, tempArray,this)
            binding!!.llHomeMain.rvVisits.layoutManager = LinearLayoutManager(this)
            binding!!.llHomeMain.rvVisits.adapter = adapter
            binding!!.llHomeMain.rvVisits.addItemDecoration(
                HeaderDecoration(
                    binding!!.llHomeMain.rvVisits,
                    adapter
                )
            );

            Log.wtf("Visitors", Gson().toJson(tempArray))
            binding!!.llHomeMain.loading.hide()
            binding!!.llHomeMain.rvVisits.show()
            binding!!.llHomeMain.tvNoVisits.hide()
        }else{
            binding!!.llHomeMain.loading.hide()
            binding!!.llHomeMain.rvVisits.hide()
            binding!!.llHomeMain.tvNoVisits.show()

        }
    }


    fun getVisits(){
        binding!!.llHomeMain.loading.show()
        RetrofitClientAuth.client?.create(RetrofitInterface::class.java)
            ?.getVisits(
               -1,
                MyApplication.userItem!!.applicationUserId!!.toInt()
            )?.enqueue(object : Callback<VisitList> {
                override fun onResponse(call: Call<VisitList>, response: Response<VisitList>) {
                    mainArray.clear()
                    mainArray.addAll(response.body()!!)
                    filterDate(response.body()!!)

                }
                override fun onFailure(call: Call<VisitList>, throwable: Throwable) {

                }
            })
    }


    fun listeners(){
        binding!!.drawerMenu.tvWelcome.text = binding!!.drawerMenu.tvWelcome.text.toString() + MyApplication.userItem!!.firstName + " "+MyApplication.userItem!!.lastName
        binding!!.llHomeMain.ivDrawer.setOnClickListener {
            var  shake =  AnimationUtils.loadAnimation(this, R.anim.corner)
            binding!!.navView.startAnimation(shake)
            binding!!.drawerLayout.openDrawer(GravityCompat.START)
        }


        binding!!.drawerMenu.llLogout.setOnClickListener {
            AppHelper.createYesNoDialog(this,getString(R.string.sure_logout),0){
                finishAffinity()
                MyApplication.loggedIn = false
                startActivity(Intent(this,ActivityLogin::class.java))
            }
        }

        binding!!.drawerMenu.tvAllTasks.setOnClickListener {
            startActivity(Intent(
                this,
                ActivityAllTasks::class.java
            ))
        }

        binding!!.drawerMenu.tvHome.setOnClickListener {
            finishAffinity()
            startActivity(Intent(this,ActivityMain::class.java))
        }

        binding!!.drawerMenu.btClose.setOnClickListener {
            var  shake =  AnimationUtils.loadAnimation(this, R.anim.close_corner)
            binding!!.navView.startAnimation(shake)
            Handler(Looper.getMainLooper()).postDelayed({
                binding!!.drawerLayout.closeDrawers()
            }, 300)

        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AppHelper.createYesNoDialog(this@ActivityMain,getString(R.string.are_you_sure_exit),0){
                    finish()
                }

            }
        })

        binding!!.llHomeMain.btDatePrevious.setOnClickListener {
            binding!!.llHomeMain.btDatePrevious.setBackgroundResource(R.drawable.rounded_darker_left)
            binding!!.llHomeMain.btDateNext.setBackgroundResource(R.drawable.rounded_dark_right)
            editDate(false)
        }

        binding!!.llHomeMain.btDateNext.setOnClickListener {
            binding!!.llHomeMain.btDateNext.setBackgroundResource(R.drawable.rounded_darker_right)
            binding!!.llHomeMain.btDatePrevious.setBackgroundResource(R.drawable.rounded_dark_left)
            editDate(true)
        }

        binding!!.llHomeMain.tvToday.setOnClickListener {
            setUpDate()
            binding!!.llHomeMain.tvToday.setBackgroundResource(R.drawable.rounded_selected)
            binding!!.llHomeMain.btDateNext.setBackgroundResource(R.drawable.rounded_dark_right)
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
        MyApplication.selectedVisit = tempArray.get(position)
        startActivity(Intent(this,ActivtyVisitDetails::class.java))
    }
}