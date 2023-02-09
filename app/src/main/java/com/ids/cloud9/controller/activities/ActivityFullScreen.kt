package com.ids.cloud9.controller.activities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.AdapterPagerFiles
import com.ids.cloud9.custom.AppCompactBase
import com.ids.cloud9.databinding.ActivityLoginBinding
import com.ids.cloud9.databinding.ActivityMediaFullscreenBinding
import com.ids.cloud9.model.Videos
import com.ids.cloud9.utils.IFragmentImages
import com.ids.cloud9.utils.hide
import com.ids.cloud9.utils.show
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

    private fun init() {

        binding!!.llTool.ivDrawer.hide()
        binding!!.llTool.layoutFragment.show()
        binding!!.llTool.tvTitleTool.text = MyApplication.selectedVisit!!.title
        binding!!.llTool.btBack.setOnClickListener {
           onBackPressedDispatcher.onBackPressed()
            try {
                pauseAll()
            } catch (e: java.lang.Exception) {
                Log.wtf("pause_exception", e.toString())
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
        //  arrayVideos.add(Videos(1,"http://staging.in-point.org/images/about-videos/inpoint-3-withSE-and-Sub.mp4",2))
        // arrayVideos.add(Videos(2,"http://staging.in-point.org/images/about-videos/Inpoint-second-video-with-SE-and-Sub.mp4",2))
        //  arrayVideos.add(Videos(3,"http://staging.in-point.org/images/about-videos/Inpoint-With-Sub-And-SE-Final.mp4",2))
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
        // vpLessonsMedia.currentItem = intent.getIntExtra(AppConstants.IMAGE_POSITION, 0)

    }

    override fun onPageClicked(v: View) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        Log.wtf("TAG_ABOUT_1","$position")
        Log.wtf("TAG_ABOUT_2","$positionOffset")
        for(i in arrayVideos.indices){
            if(arrayVideos.get(i).player!=null) {
                if (i == position) {
                    // arrayVideos.get(i).player!!.play()
                } else {
                    arrayVideos.get(i).player!!.pause()
                }
            }
        }
        //Toast.makeText(this,"PAGE NUMBER $position",Toast.LENGTH_SHORT).show()
    }

    override fun onPageSelected(position: Int) {
        try{pauseAll()}catch (e:java.lang.Exception){}
    }

    private fun pauseAll() {
        for (i in arrayVideos.indices) {
            if (arrayVideos.get(i).type==2) {
                (binding!!.vpMediaFull.getChildAt(i)
                    .findViewById(R.id.epView) as StyledPlayerView).player!!.playWhenReady =
                    false
                (binding!!.vpMediaFull.getChildAt(i).findViewById(R.id.epView) as PlayerView).player!!
                    .playbackState
            }
        }
    }

    override fun onPause() {
        try {
            pauseAll()
        } catch (e: Exception) {
        }
        super.onPause()
    }

    override fun onStop() {
        try {
            pauseAll()
        } catch (e: java.lang.Exception) {
        }
        super.onStop()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

    }
}