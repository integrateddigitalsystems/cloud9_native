package com.ids.cloud9.Adapters

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
import com.ids.cloud9.model.ItemSpinner
import com.ids.cloud9.model.ProductsItem
import com.ids.cloud9.utils.AppHelper
import com.ids.cloud9.utils.setCheckText
import java.text.SimpleDateFormat
import java.util.ArrayList

class AdapterReccomendations(
    var items : ArrayList<ActivitiesListItem>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener
) : RecyclerView.Adapter<AdapterReccomendations.VhItem>() {
    var simpOrg = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    var simpTo = SimpleDateFormat("dd/MM/yyyy")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterReccomendations.VhItem {
        val binding = ItemReccomendationsBinding.inflate(con.layoutInflater,parent,false)
        return VhItem(binding, clickListener)
    }

    override fun onBindViewHolder(holder: AdapterReccomendations.VhItem, position: Int) {
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


        fun bind(item: ActivitiesListItem) {


            binding.tvAssignedTo.setCheckText(item.assignedTo)
            binding.tvDesc.setCheckText(item.description)
            binding.tvSubject.setCheckText(item.subject)
            binding.tvDueDate.setCheckText(simpTo.format(simpOrg.parse(item.dueDate)))
            binding.tvReason.setCheckText(item.reason)





        }


        init {
            binding.root.setOnClickListener(this)



        }

        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }

}