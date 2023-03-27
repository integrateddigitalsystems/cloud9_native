package com.ids.cloud9.controller.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.ids.cloud9.R
import com.ids.cloud9.custom.TouchImageView
import com.ids.cloud9.model.Videos
import com.ids.cloud9.utils.IFragmentImages
import com.ids.cloud9.utils.safeCall
import com.ids.cloud9.utils.wtf
import java.util.ArrayList

class AdapterPagerFiles(
    private val files: ArrayList<Videos>,
    private val fragmentImages: IFragmentImages,
    private val context: Context,
    private val list : Player.Listener
) : PagerAdapter(), View.OnClickListener {
    var currentPosition: Long? = null
    override fun getCount(): Int {
        return files.size
    }
    override fun isViewFromObject(
        view: View,
        `object`: Any
    ): Boolean {
        return view === `object`
    }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var itemView = LayoutInflater.from(container.context)
            .inflate(R.layout.item_pager_files, container, false)


        val touch = itemView.findViewById<View>(R.id.ivImages) as TouchImageView
        itemView.findViewById<View>(R.id.ivImages).setOnClickListener(this)
        container.addView(itemView)
       (itemView.findViewById<View>(R.id.btFullScreen) as ImageView).visibility =
            View.INVISIBLE
        if (files[position].type == 0) {
            (itemView.findViewById<View>(R.id.epView) as StyledPlayerView).visibility =
                View.VISIBLE
            touch.visibility = View.GONE
            loadVideo(
                itemView.context,
                itemView.findViewById<View>(R.id.epView) as StyledPlayerView,
                files[position].url,
                position
            )

        } else {
            wtf("file : " + files[position].url)
            (itemView.findViewById<View>(R.id.epView) as StyledPlayerView).visibility = View.GONE
            touch.visibility = View.VISIBLE
            loadImage(
                itemView.context,
                touch,
                files[position].url,
                position
            )

        }
        return itemView
    }
    override fun onClick(v: View) {
        fragmentImages.onPageClicked(v)
    }
    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        try {
            currentPosition = 0L
            (container.getChildAt(position)
                .findViewById<View>(R.id.epView) as StyledPlayerView).player!!.playWhenReady =
                false
            (container.getChildAt(position)
                .findViewById<View>(R.id.epView) as StyledPlayerView).player!!
                .playbackState
        } catch (e: Exception) {
            wtf(e.toString())
        }
        safeCall {
            container.removeView(`object` as View)
            Glide.with(context).clear(`object`)
        }
    }
    private fun loadImage(
        context: Context,
        imageView: TouchImageView,
        url: String?,position:Int
    ) {
        val options: RequestOptions = RequestOptions()
            .fitCenter()
            .placeholder(R.color.gray_border_tab)
            .error(R.color.gray_border_tab)
        Glide.with(context).load(url).apply(options)
            .into(imageView)
        files.get(position).zoomer = imageView
    }
    private fun loadVideo(
        context: Context,
        exoPlayerView: StyledPlayerView,
        url: String?,
        position: Int
    ) {
        val player = ExoPlayer.Builder(context).build()
        exoPlayerView.player = player
        player.addListener(list)
        val mediaItem = MediaItem.fromUri(url!!)
        player.addMediaItem(mediaItem)
        player.prepare()
        files.get(position).player = player
    }

}