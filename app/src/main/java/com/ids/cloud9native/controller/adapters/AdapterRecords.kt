package com.ids.cloud9native.controller.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9native.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9native.databinding.ItemRecordBinding
import com.ids.cloud9native.model.RecordListsItem
import java.text.SimpleDateFormat
import java.util.*

class AdapterRecords(
    var items : ArrayList<RecordListsItem>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener
) : RecyclerView.Adapter<AdapterRecords.VhItem>() {
    var simpOrg = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    var simpTo = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItem {
        val binding = ItemRecordBinding.inflate(con.layoutInflater,parent,false)
        return VhItem(binding, clickListener)
    }
    override fun onBindViewHolder(holder: VhItem, position: Int) {
        holder.bind(items[position])
    }
    override fun getItemCount(): Int {
        return items.size
    }
    inner class VhItem(
        val binding: ItemRecordBinding,
        private var clickListener: RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root

    ), View.OnClickListener {
        fun bind(item: RecordListsItem) {
            binding.tvSubject.text = item.formName
            binding.tvDate.text = simpTo.format(simpOrg.parse(item.creationDate!!)!!)
        }
        init {
            binding.root.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }
}