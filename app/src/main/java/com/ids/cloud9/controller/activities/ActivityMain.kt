package com.ids.cloud9.controller.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.core.view.GravityCompat
import com.ids.cloud9.R
import com.ids.cloud9.databinding.ActivityMainBinding
import com.ids.cloud9.utils.AppHelper
import com.ids.cloud9.utils.show
import com.shrikanthravi.customnavigationdrawer2.data.MenuItem


class ActivityMain: Activity() {

    var binding : ActivityMainBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        listeners()
        setUpSliding()

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
}