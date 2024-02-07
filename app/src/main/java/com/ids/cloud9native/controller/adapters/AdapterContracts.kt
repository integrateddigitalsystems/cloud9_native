package com.ids.cloud9native.controller.adapters


import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9native.databinding.ItemReccomendationsBinding
import com.ids.cloud9native.databinding.ItemRelatedVisitBinding
import com.ids.cloud9native.databinding.ItemVisitBinding
import com.ids.cloud9native.databinding.ItemVisitDateBinding
import com.ids.cloud9native.model.FilteredActivityListItem
import com.ids.cloud9native.model.Visit
import com.ids.cloud9native.utils.*
import kotlin.collections.ArrayList


class AdapterContracts(context: Activity, private val items: ArrayList<Visit>, var click:RVOnItemClickListener) :
    RecyclerView.Adapter<AdapterContracts.VhItem>()  {
    private val mContext: Activity
    init {
        mContext = context
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): VhItem {
        val binding = ItemRelatedVisitBinding.inflate(mContext.layoutInflater, viewGroup, false)
        return VhItem(binding,click)

    }
    override fun onBindViewHolder(holder: AdapterContracts.VhItem, position: Int) {
        holder.bind(items[position])
    }
    override fun getItemCount(): Int {
        return items.size
    }

    inner class VhItem(
        val binding: ItemRelatedVisitBinding,
        private var clickListener: RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root
    ), View.OnClickListener {
        fun bind(item: Visit) {
            binding.tvVisitName.text = item.title + "\n" + item.number
            binding.tvCompany.text = item.company!!.companyName
            binding.tvStatus.setBackgroundResource(item.showData!!.backg!!)
            AppHelper.setTextColor(mContext,binding.tvStatus,item.showData!!.textColor!!)
            binding.tvStatus.text = item.showData!!.statusReason
            binding.tvDuration.text = item.showData!!.durationFromTo
        }
        init {
            binding.root.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }


}