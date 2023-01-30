package com.ids.cloud9.Adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ItemMediaBinding
import com.ids.cloud9.model.ItemSpinner
import com.ids.cloud9.utils.AppHelper
import com.ids.cloud9.utils.hide
import com.ids.cloud9.utils.show
import java.util.*

class AdapterMedia(
    var items : ArrayList<ItemSpinner>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener
) : RecyclerView.Adapter<AdapterMedia.VhItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMedia.VhItem {
        val binding = ItemMediaBinding.inflate(con.layoutInflater,parent,false)
        return VhItem(binding, clickListener)
    }

    override fun onBindViewHolder(holder: AdapterMedia.VhItem, position: Int) {
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


            if(item.type == 1) {
                binding.ivMedia.show()
                binding.vvMedia.hide()
                AppHelper.setImage(con, binding.ivMedia, item.name!!, item.isLocal!!)
            } else{
                binding.ivMedia.hide()
                binding.vvMedia.show()
                AppHelper.loadVideo(binding.vvMedia,item.name!!)
            }
              //  AppHelper.setImage(con,binding.vvMedia,item.name!!,item.isLocal!!)
            

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