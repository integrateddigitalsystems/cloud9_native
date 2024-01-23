package com.ids.cloud9native.controller.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView;
import androidx.viewpager.widget.ViewPager
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.controller.adapters.AdapterPagerFiles
import com.ids.cloud9native.custom.AppCompactBase
import com.ids.cloud9native.databinding.ActivityMediaFullscreenBinding
import com.ids.cloud9native.model.Videos
import com.ids.cloud9native.utils.*
import java.util.*

class ActivityFullScreen : AppCompactBase() , IFragmentImages, ViewPager.OnPageChangeListener , Player.Listener {
    private var arrayVideos: ArrayList<Videos> = ArrayList()
    var binding : ActivityMediaFullscreenBinding?=null
    var pos=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaFullscreenBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }
    @SuppressLint("SetTextI18n")
    private fun init() {
        binding!!.llTool.ivDrawer.hide()
        binding!!.llTool.layoutFragment.show()
        binding!!.llTool.tvTitleTool.text = MyApplication.selectedVisit!!.title + "\n" + MyApplication.selectedVisit!!.number
        binding!!.llTool.btBack.setOnClickListener {
           onBackPressedDispatcher.onBackPressed()
            try {
                pauseAll()
            } catch (e: java.lang.Exception) {
                wtf(e.toString())
            }
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
        pos = intent.getIntExtra("ID_MEDIA",0)
        setMediaPager()
    }


    private fun setMediaPager(){
        arrayVideos.clear()
        var ct = 1
        for(item in MyApplication.arrayVid){
            arrayVideos.add(Videos(ct,item.name,item.type))
            arrayVideos.get(ct-1).isLocal = item.isLocal
            ct++
        }

        val adapterVideos = AdapterPagerFiles(arrayVideos, this,this,this)
        binding!!.vpMediaFull.adapter = adapterVideos
        binding!!.tbMedia.setupWithViewPager(binding!!.vpMediaFull)
        binding!!.vpMediaFull.addOnPageChangeListener(this)
        if(arrayVideos.size>1){
            binding!!.tbMedia.visibility = View.VISIBLE
        }else{
            binding!!.tbMedia.visibility = View.GONE
        }
        binding!!.vpMediaFull.currentItem = pos
    }
    override fun onPageClicked(v: View) {
    }
    override fun onPageScrollStateChanged(state: Int) {
    }
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        for(i in arrayVideos.indices){
            if(arrayVideos.get(i).player!=null) {
                if (i != position)
                    arrayVideos.get(i).player!!.pause()
                else{
                    arrayVideos.get(i).player!!.play()
                }

            }else{
               /* if(arrayVideos.get(i).zoomer!=null)
                    arrayVideos.get(i).zoomer!!.currentZoom*/
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MyApplication.activityResumed()
    }

    override fun onPageSelected(position: Int) {
       safeCall {  pauseAll()}
    }
    private fun pauseAll() {
        for (i in arrayVideos.indices) {
            if (arrayVideos.get(i).type==2) {
                (binding!!.vpMediaFull.getChildAt(i)
                    .findViewById(R.id.epView) as PlayerView).player!!.playWhenReady =
                    false
                (binding!!.vpMediaFull.getChildAt(i).findViewById(R.id.epView) as PlayerView).player!!
                    .playbackState
            }
        }
    }
    override fun onPause() {
        safeCall {
            pauseAll()
        }
        MyApplication.activityPaused()
        super.onPause()
    }
    override fun onStop() {
        safeCall {
            pauseAll()
        }
        super.onStop()
    }
    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

    }
}