package com.ids.cloud9native.controller.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9native.databinding.ItemSerialisedBinding
import com.ids.cloud9native.databinding.ItemTextBinding
import com.ids.cloud9native.databinding.ItemTextSerialBinding
import com.ids.cloud9native.model.ItemSpinner
import com.ids.cloud9native.model.SerialItem
import com.ids.cloud9native.utils.toEditable
import java.util.*

class AdapterSerial(
    var items: ArrayList<ItemSpinner>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener,
) : RecyclerView.Adapter<AdapterSerial.VhItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItem {
        val binding = ItemTextSerialBinding.inflate(con.layoutInflater, parent, false)
        return VhItem(binding, clickListener)
    }
    override fun onBindViewHolder(holder: VhItem, position: Int) {
        holder.bind(items[position])
    }
    override fun getItemCount(): Int {
        return items.size
    }
    inner class VhItem(
        val binding: ItemTextSerialBinding,
        private var clickListener: RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root
    ), View.OnClickListener {
        fun bind(item: ItemSpinner) {
          binding.tvName.text = item.name
        }
        init {
            binding.root.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }
}