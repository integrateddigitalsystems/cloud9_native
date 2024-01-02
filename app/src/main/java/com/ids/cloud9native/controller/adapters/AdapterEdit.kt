package com.ids.cloud9native.controller.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9native.databinding.ItemSerialisedBinding
import com.ids.cloud9native.model.SerialItem
import com.ids.cloud9native.utils.toEditable
import java.util.*

class AdapterEdit(
    var items: ArrayList<SerialItem>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener
) : RecyclerView.Adapter<AdapterEdit.VhItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItem {
        val binding = ItemSerialisedBinding.inflate(con.layoutInflater, parent, false)
        return VhItem(binding, clickListener)
    }
    override fun onBindViewHolder(holder: VhItem, position: Int) {
        holder.bind(items[position])
    }
    override fun getItemCount(): Int {
        return items.size
    }
    inner class VhItem(
        val binding: ItemSerialisedBinding,
        private var clickListener: RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root
    ), View.OnClickListener {
        fun bind(item: SerialItem) {
            binding.etSerialNumber.doOnTextChanged { text, start, before, count ->
             items.get(layoutPosition).serial = text.toString()
            }
            binding.tvSerialised.text = con.getString(R.string.serial_number) + " "+(layoutPosition+1)+":"
            binding.etSerialNumber.text = item.serial!!.toEditable()
        }
        init {
            binding.root.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }
}