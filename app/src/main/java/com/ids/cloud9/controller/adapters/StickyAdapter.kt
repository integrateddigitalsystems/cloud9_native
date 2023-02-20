package com.ids.cloud9.controller.adapters


import android.app.Activity
import android.content.Context
import android.provider.ContactsContract.Directory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9.R
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ItemVisitBinding
import com.ids.cloud9.databinding.ItemVisitDateBinding
import com.ids.cloud9.model.VisitDates
import com.ids.cloud9.model.VisitListItem
import com.ids.cloud9.model.testVisitItem
import com.ids.cloud9.utils.AppConstants
import com.ids.cloud9.utils.AppHelper
import com.ids.cloud9.utils.hide
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class StickyAdapter(context: Activity, private val mList: ArrayList<testVisitItem>,click:RVOnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), HeaderDecoration.StickyHeaderInterface {
    private val mContext: Activity
    private val VIEW_HEADER = 1
    private val VIEW_DETAIL = 0
    var click:RVOnItemClickListener = click
    init {
        mContext = context
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_HEADER) {
            val binding = ItemVisitDateBinding.inflate(mContext.layoutInflater, viewGroup, false)
            HeaderViewHolder(binding)
        } else {
            val binding = ItemVisitBinding.inflate(mContext.layoutInflater, viewGroup, false)
            DetailViewHolder(binding,click)
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(getItem(position))
        } else {
            (holder as DetailViewHolder).bind(getItem(position))
        }
    }
    inner class HeaderViewHolder(
        val binding: ItemVisitDateBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ), View.OnClickListener {
        fun bind(item: testVisitItem) {
            var cal = Calendar.getInstance()
            var dateMil = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(item.visitDate!!).time
            cal.time.time = dateMil
            binding.tvDay.text = SimpleDateFormat("EEEE").format(Date(dateMil))
            binding.tvDate.text = SimpleDateFormat("MMMM dd, yyyy").format(Date(dateMil))
        }
        init {
            binding.root.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
        }
    }
    inner class DetailViewHolder(
        val binding: ItemVisitBinding,
        var clicker:RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root
    ), View.OnClickListener {
        fun bind(child: testVisitItem) {
            binding.tvVisitName.text = child.title
            binding.tvCompany.text = child.company!!.companyName
            when (child.reasonId){
                AppConstants.COMPLETED_REASON_ID -> {
                    binding.tvStatus.setBackgroundResource(R.color.comp_bg)
                    AppHelper.setTextColor(mContext,binding.tvStatus,R.color.comp_text)
                    binding.tvStatus.text = AppConstants.COMPLETED_REASON
                }
                AppConstants.SCHEDULED_REASON_ID -> {
                    binding.tvStatus.setBackgroundResource(R.color.comp_bg)
                    AppHelper.setTextColor(mContext,binding.tvStatus,R.color.comp_text)
                    binding.tvStatus.text = AppConstants.SCHEDULED_REASON
                }
                AppConstants.ON_THE_WAY_REASON_ID -> {
                    binding.tvStatus.setBackgroundResource(R.color.on_the_way_bg)
                    AppHelper.setTextColor(mContext,binding.tvStatus,R.color.otw_text)
                    binding.tvStatus.text = AppConstants.ON_THE_WAY_REASON
                }
                AppConstants.ARRIVED_REASON_ID -> {
                    binding.tvStatus.setBackgroundResource(R.color.arrived_bg)
                    AppHelper.setTextColor(mContext,binding.tvStatus,R.color.arrived_text)
                    binding.tvStatus.text = AppConstants.ARRIVED_REASON
                }
                AppConstants.PENDING_REASON_ID -> {
                    binding.tvStatus.setBackgroundResource(R.color.pending_bg)
                    AppHelper.setTextColor(mContext,binding.tvStatus,R.color.pending_text)
                    binding.tvStatus.text = AppConstants.PENDING_REASON
                }
            }
            var calFrom = Calendar.getInstance()
            var calTo = Calendar.getInstance()
            var millFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss").parse(child.fromTime).time
            var millTo = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss").parse(child.toTime).time
            var diff = millTo - millFrom
            var mins = diff / 60000
            var hours = mins/60
            var min = mins%60
            binding.tvDuration.text = SimpleDateFormat("hh:mm aa").format(Date(millFrom)) +"-\n"+SimpleDateFormat("hh:mm aa").format(Date(millTo))+"\n("+if(hours>0) {hours.toString()+"hrs"+min+"mins"+")"}else {min.toString()+" mins"+")"}
            if(layoutPosition+1 < mList.size) {
                if (mList.get(layoutPosition + 1).isHeader!!)
                    binding.llUnderLine.hide()
            }else{
                binding.llUnderLine.hide()
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
        return mList?.size ?: 0
    }
    fun getItem(position: Int): testVisitItem {
        return mList!![position]
    }
    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var itemPosition = itemPosition
        var headerPosition = 0
        do {
            if (isHeader(itemPosition)) {
                headerPosition = itemPosition
                break
            }
            itemPosition -= 1
        } while (itemPosition >= 0)
        return headerPosition
    }
    override fun getHeaderLayout(headerPosition: Int): Int {
        return R.layout.item_visit_date
    }
    override fun bindHeaderData(header: View?, headerPosition: Int) {
        val child = mList!![headerPosition]
        val day: TextView = header!!.findViewById(R.id.tvDay)
        val date : TextView = header!!.findViewById(R.id.tvDate)
        var cal = Calendar.getInstance()
        var dateMil = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(child.visitDate!!).time
        cal.time.time = dateMil
        day.text = SimpleDateFormat("EEEE").format(Date(dateMil))
        date.text = SimpleDateFormat("MMMM dd, yyyy").format(Date(dateMil))
    }
    override fun isHeader(itemPosition: Int): Boolean {
        return mList!![itemPosition].isHeader!!
    }
    override fun getItemViewType(position: Int): Int {
        return if (mList!![position].isHeader!!)
            VIEW_HEADER else
                VIEW_DETAIL
    }
}