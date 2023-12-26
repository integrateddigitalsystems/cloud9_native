package com.ids.cloud9.controller.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ItemReccomendationsBinding
import com.ids.cloud9.model.FilteredActivityListItem
import java.text.SimpleDateFormat
import java.util.*

class AdapterFilteredReccomendations(
    var items : ArrayList<FilteredActivityListItem>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener
) : RecyclerView.Adapter<AdapterFilteredReccomendations.VhItem>() {
    var simpOrg = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    var simpTo = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItem {
        val binding = ItemReccomendationsBinding.inflate(con.layoutInflater,parent,false)
        return VhItem(binding, clickListener)
    }
    override fun onBindViewHolder(holder: VhItem, position: Int) {
        holder.bind(items[position])
    }
    override fun getItemCount(): Int {
        return items.size
    }
    inner class VhItem(
        val binding: ItemReccomendationsBinding,
        private var clickListener: RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root
    ), View.OnClickListener {
        fun bind(item: FilteredActivityListItem) {
            if(!item.assignedTo.isNullOrEmpty()){
                binding.tvAssignedTo.text = item.assignedTo
            }else{
                binding.tvAssignedTo.text = ""
            }
            if(!item.dueDate.isNullOrEmpty()){
                binding.tvDueDate.text = item.dueDate
            }else{
                binding.tvDueDate.text = ""
            }
            if(!item.subject.isNullOrEmpty()){
                binding.tvSubject.text = item.subject
            }else{
                binding.tvSubject.text = ""
            }
            if(!item.description.isNullOrEmpty()){
                binding.tvDesc.text = item.description!!
            }else{
                binding.tvDesc.text = ""
            }
            if(!item.statusReason.isNullOrEmpty()){
                binding.tvReason.text = item.statusReason!!
            }else{
                binding.tvReason.text = ""
            }
  }
        init {
            binding.root.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }
}