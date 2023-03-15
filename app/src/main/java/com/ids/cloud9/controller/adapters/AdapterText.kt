package com.ids.cloud9.controller.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9.R
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ItemTextBinding
import com.ids.cloud9.model.ItemSpinner
import com.ids.cloud9.utils.AppHelper
import java.util.*

class AdapterText(
    var items: ArrayList<ItemSpinner>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener
) : RecyclerView.Adapter<AdapterText.VhItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItem {
        val binding = ItemTextBinding.inflate(con.layoutInflater, parent, false)
        return VhItem(binding, clickListener)
    }
    override fun onBindViewHolder(holder: VhItem, position: Int) {
        holder.bind(items[position])
    }
    override fun getItemCount(): Int {
        return items.size
    }
    inner class VhItem(
        val binding: ItemTextBinding,
        private var clickListener: RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root
    ), View.OnClickListener {
        fun bind(item: ItemSpinner) {
            binding.tvReason.text = item.name
            if (item.selected != null && item.selected!!) {
                binding.llItemText.setBackgroundResource(R.color.colorPrimary)
                AppHelper.setTextColor(con, binding.tvReason, R.color.white)
            } else {
                binding.llItemText.setBackgroundResource(R.color.white)
                AppHelper.setTextColor(con, binding.tvReason, R.color.colorAccent)
            }
        }
        init {
            binding.llItemText.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }

}