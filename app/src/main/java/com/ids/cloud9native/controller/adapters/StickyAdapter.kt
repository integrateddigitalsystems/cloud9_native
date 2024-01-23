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
import com.ids.cloud9native.databinding.ItemVisitBinding
import com.ids.cloud9native.databinding.ItemVisitDateBinding
import com.ids.cloud9native.model.Visit
import com.ids.cloud9native.utils.*
import kotlin.collections.ArrayList


class StickyAdapter(context: Activity, private val mList: ArrayList<Visit>, var click:RVOnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), HeaderDecoration.StickyHeaderInterface {
    private val mContext: Activity
    private val VIEW_HEADER = 1
    private val VIEW_DETAIL = 0
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
        fun bind(item: Visit) {
            binding.tvDay.text = item.showData!!.dayname
            binding.tvDate.text = item.showData!!.date
            if(item.showData!!.today!!){
                binding.llHeaderDate.setBackgroundResource(R.color.colorPrimaryDark)
                AppHelper.setTextColor(mContext,binding.tvDate,R.color.white)
                AppHelper.setTextColor(mContext,binding.tvDay,R.color.white)
            }
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
        @SuppressLint("SetTextI18n")
        fun bind(child: Visit) {
            binding.tvVisitName.text = child.title + "\n" + child.number
            binding.tvCompany.text = child.company!!.companyName
            binding.tvStatus.setBackgroundResource(child.showData!!.backg!!)
            AppHelper.setTextColor(mContext,binding.tvStatus,child.showData!!.textColor!!)
            binding.tvStatus.text = child.showData!!.statusReason
            binding.tvDuration.text = child.showData!!.durationFromTo
            safeCall {
                if(mList.get(layoutPosition+1).isHeader!!) binding.llUnderLine.hide() else binding.llUnderLine.show()
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
    fun getItem(position: Int): Visit {
        return mList[position]
    }
    override fun getHeaderPositionForItem(itePosition: Int): Int {
        var itemPosition = itePosition
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
        val child = mList[headerPosition]
        val day: TextView = header!!.findViewById(R.id.tvDay)
        val date : TextView = header.findViewById(R.id.tvDate)
        val headerLL : LinearLayout = header.findViewById(R.id.llHeaderDate)
        day.text = child.showData!!.dayname
        date.text = child.showData!!.date
        if(mList.get(headerPosition).showData!!.today!!){
            headerLL.setBackgroundResource(R.color.colorPrimaryDark)
            AppHelper.setTextColor(mContext,date,R.color.white)
            AppHelper.setTextColor(mContext,day,R.color.white)
        }

    }
    override fun isHeader(itemPosition: Int): Boolean {
        return mList[itemPosition].isHeader!!
    }
    override fun getItemViewType(position: Int): Int {
        return if (mList[position].isHeader!!)
            VIEW_HEADER else
                VIEW_DETAIL
    }
}