package com.ids.cloud9native.controller.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.Player
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9native.databinding.ItemMediaBinding
import com.ids.cloud9native.model.ItemSpinner
import com.ids.cloud9native.utils.AppHelper
import com.ids.cloud9native.utils.hide
import com.ids.cloud9native.utils.show

import java.util.*

class AdapterMedia(
    var items : ArrayList<ItemSpinner>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener
) : RecyclerView.Adapter<AdapterMedia.VhItem>() {
    var list : Player.Listener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItem {
        val binding = ItemMediaBinding.inflate(con.layoutInflater,parent,false)
        return VhItem(binding, clickListener)
    }
    override fun onBindViewHolder(holder: VhItem, position: Int) {
        holder.bind(items[position])
    }
    override fun getItemCount(): Int {
        return items.size
    }
    inner class VhItem(
        val binding: ItemMediaBinding,
        private var clickListener: RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root
    ), View.OnClickListener {
        fun bind(item: ItemSpinner) {
            if(item.type == 1){
                binding.ivVideoDefault.hide()
                binding.ivMedia.show()
                binding.vvMedia.hide()
                binding.llMainMediaLayout.setBackgroundResource(R.color.transparent)
                AppHelper.setImage(con,binding.ivMedia,item.name!!,item.isLocal!!,150,300)
            }else{
                binding.ivMedia.hide()
                binding.ivVideoDefault.show()
            }
        }
        init {
            binding.root.setOnClickListener(this)
            binding.btClose.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }
}