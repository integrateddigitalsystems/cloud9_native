package com.ids.cloud9.controller.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9.R
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ItemProductsBinding
import com.ids.cloud9.databinding.ItemReasonDialogBinding
import com.ids.cloud9.databinding.ItemReccomendationsBinding
import com.ids.cloud9.model.ActivitiesListItem
import com.ids.cloud9.model.FilteredActivityListItem
import com.ids.cloud9.model.ItemSpinner
import com.ids.cloud9.model.ProductsItem
import com.ids.cloud9.utils.AppHelper
import com.ids.cloud9.utils.setCheckText
import java.text.SimpleDateFormat
import java.util.ArrayList

class AdapterFilteredReccomendations(
    var items : ArrayList<FilteredActivityListItem>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener
) : RecyclerView.Adapter<AdapterFilteredReccomendations.VhItem>() {
    var simpOrg = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    var simpTo = SimpleDateFormat("dd/MM/yyyy")
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


            binding.tvAssignedTo.setCheckText(item.assignedTo)
            binding.tvDesc.setCheckText(item.description)
            binding.tvSubject.setCheckText(item.subject)
            binding.tvDueDate.setCheckText(simpTo.format(simpOrg.parse(item.dueDate)))
            if(!item.statusReason.isNullOrEmpty())
                binding.tvReason.setCheckText(item.statusReason!!)





        }


        init {
            binding.root.setOnClickListener(this)



        }

        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }

}