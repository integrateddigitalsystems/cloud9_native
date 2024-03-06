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
import com.ids.cloud9native.databinding.ItemRelatedVisitBinding
import com.ids.cloud9native.databinding.ItemVisitBinding
import com.ids.cloud9native.databinding.ItemVisitDateBinding
import com.ids.cloud9native.model.ResponseRelatedVisitsItem
import com.ids.cloud9native.model.Visit
import com.ids.cloud9native.utils.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.ArrayList


class AdapterRelatedVisits(context: Activity, private val mList: ArrayList<ResponseRelatedVisitsItem>, var click:RVOnItemClickListener) :
    RecyclerView.Adapter<AdapterRelatedVisits.VhItem>() {
    private val mContext: Activity
    init {
        mContext = context
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): VhItem {
        val binding = ItemRelatedVisitBinding.inflate(mContext.layoutInflater, viewGroup, false)
       return VhItem(binding,click)
    }
    override fun onBindViewHolder(holder: VhItem, position: Int) {
        holder.bind(mList[position])
    }
    inner class VhItem(
        val binding: ItemRelatedVisitBinding,
        var clicker:RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root
    ), View.OnClickListener {
        @SuppressLint("SetTextI18n")
        fun bind(child: ResponseRelatedVisitsItem) {
            binding.tvVisitName.text = child.title + "\n" + child.number

            when (child.reasonId) {
                AppHelper.getReasonID(AppConstants.REASON_ARRIVED) -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.arrived_bg)
                    AppHelper.setTextColor(mContext,binding.tvStatus,R.color.arrived_text)
                    binding.tvStatus.text =AppHelper.getReasonName(AppConstants.REASON_ARRIVED)
                }
                AppHelper.getReasonID(AppConstants.REASON_COMPLETED) -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.completed_bg)
                    AppHelper.setTextColor(mContext,binding.tvStatus,R.color.comp_text)
                    binding.tvStatus.text = AppHelper.getReasonName(AppConstants.REASON_COMPLETED)
                }
                AppHelper.getReasonID(AppConstants.REASON_SCHEDULED) -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.scheduled_bg)
                    AppHelper.setTextColor(mContext,binding.tvStatus,R.color.scheduled_text)
                    binding.tvStatus.text = AppHelper.getReasonName(AppConstants.REASON_SCHEDULED)
                }
                AppHelper.getReasonID(AppConstants.REASON_ON_THE_WAY) -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.on_the_way_bg)
                    AppHelper.setTextColor(mContext,binding.tvStatus,R.color.otw_text)
                    binding.tvStatus.text =AppHelper.getReasonName(AppConstants.REASON_ON_THE_WAY)
                }
                AppHelper.getReasonID(AppConstants.REASON_PENDING) -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.pending_bg)
                    AppHelper.setTextColor(mContext,binding.tvStatus,R.color.pending_text)
                    binding.tvStatus.text = AppHelper.getReasonName(AppConstants.REASON_PENDING)
                }
                else -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.on_the_way_bg)
                    AppHelper.setTextColor(mContext,binding.tvStatus,R.color.otw_text)
                    binding.tvStatus.text =AppHelper.getReasonName(AppConstants.REASON_ON_THE_WAY)
                }
            }

            val dateMil = SimpleDateFormat(
                "yyyy-MM-dd'T'hh:mm:ss",
                Locale.ENGLISH
            ).parse(child.visitDate)!!.time

            val day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date(dateMil))
            val date = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH).format(Date(dateMil))

            binding.tvDay.text =day
            binding.tvDate.text =date

            if (child.visitResources.isNotEmpty()){
                child.visitResources.forEachIndexed { index, categories ->
                    binding.tvResource.text = binding.tvResource.text.toString() + child.visitResources[index].resource.fullName + if(index!= child.visitResources.size-1) " , " else ""
                }
            }

        }
        init {
            binding.root.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            clicker.onItemClicked(p0!!, layoutPosition)
        }
    }
    override fun getItemCount(): Int {
        return mList.size
    }

}