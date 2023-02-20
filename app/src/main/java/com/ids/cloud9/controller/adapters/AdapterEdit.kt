package com.ids.cloud9.controller.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9.R
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ItemReasonDialogBinding
import com.ids.cloud9.databinding.ItemSerialisedBinding
import com.ids.cloud9.databinding.ItemTextBinding
import com.ids.cloud9.databinding.ItemVisitDateBinding
import com.ids.cloud9.model.ItemSpinner
import com.ids.cloud9.model.SerialItem
import com.ids.cloud9.model.VisitDates
import com.ids.cloud9.utils.AppHelper
import com.ids.cloud9.utils.hide
import com.ids.cloud9.utils.show
import com.ids.cloud9.utils.toEditable
import java.text.SimpleDateFormat
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
        val binding: com.ids.cloud9.databinding.ItemSerialisedBinding,
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