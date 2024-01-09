package com.ids.cloud9native.controller.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9native.databinding.ItemReasonDialogBinding
import com.ids.cloud9native.model.ItemSpinner
import com.ids.cloud9native.utils.AppHelper
import java.util.*

class AdapterDialog(
    var items: ArrayList<ItemSpinner>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener,
    var type: Boolean
) : RecyclerView.Adapter<AdapterDialog.VhItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItem {
        val binding = ItemReasonDialogBinding.inflate(con.layoutInflater, parent, false)
        return VhItem(binding, clickListener)
    }
    override fun onBindViewHolder(holder: VhItem, position: Int) {
        holder.bind(items[position])
    }
    override fun getItemCount(): Int {
        return items.size
    }
    inner class VhItem(
        val binding: ItemReasonDialogBinding,
        private var clickListener: RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root
    ), View.OnClickListener {
        fun bind(item: ItemSpinner) {
            binding.tvReason.text = item.name
            if (!item.selectable!!) {
                binding.llItemReason.setBackgroundResource(R.drawable.gray_tint_border)
                AppHelper.setTextColor(con, binding.tvReason, R.color.gray_border_item)
            } else {
                if (item.selected != null && item.selected!!) {
                    binding.llItemReason.setBackgroundResource(R.drawable.rounded_selected_primary)
                    AppHelper.setTextColor(con, binding.tvReason, R.color.colorPrimary)
                } else {
                    binding.llItemReason.setBackgroundResource(R.drawable.rounded_white_border)
                    AppHelper.setTextColor(con, binding.tvReason, R.color.colorAccent)
                }
            }
        }
        init {
            binding.llItemReason.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }
}