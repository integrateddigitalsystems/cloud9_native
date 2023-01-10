package com.ids.cloud9.controller.activities

import android.app.Activity
import android.os.Bundle
import androidx.core.view.GravityCompat
import com.ids.cloud9.databinding.ActivityMainBinding
import com.ids.cloud9.utils.AppHelper
import com.shrikanthravi.customnavigationdrawer2.data.MenuItem


class ActivityMain: Activity() {

    var binding : ActivityMainBinding ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        listeners()
        setUpSliding()

    }


    fun listeners(){
        binding!!.llHomeMain.ivDrawer.setOnClickListener {
            binding!!.drawerLayout.openDrawer(GravityCompat.START)
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
}