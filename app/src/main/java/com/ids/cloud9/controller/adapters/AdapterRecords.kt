package com.ids.cloud9.controller.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ItemReccomendationsBinding
import com.ids.cloud9.databinding.ItemRecordBinding
import com.ids.cloud9.model.ActivitiesListItem
import com.ids.cloud9.model.RecordListsItem
import com.ids.cloud9.utils.setCheckText
import java.text.SimpleDateFormat
import java.util.ArrayList

class AdapterRecords(
    var items : ArrayList<RecordListsItem>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener
) : RecyclerView.Adapter<AdapterRecords.VhItem>() {
    var simpOrg = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    var simpTo = SimpleDateFormat("dd/MM/yyyy")
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
            binding.tvSubject.text = item.name
            binding.tvDesc.text = item.code
            binding.tvDate.text = simpTo.format(simpOrg.parse(item.creationDate))
        }
        init {
            binding.root.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }
}