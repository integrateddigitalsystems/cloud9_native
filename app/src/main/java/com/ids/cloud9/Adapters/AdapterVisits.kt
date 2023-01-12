package com.ids.cloud9.Adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9.R
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ItemVisitBinding
import com.ids.cloud9.model.VisitListItem
import com.ids.cloud9.utils.hide
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AdapterVisits(
    var items : ArrayList<VisitListItem>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener
) : RecyclerView.Adapter<AdapterVisits.VhItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterVisits.VhItem {
        val binding = ItemVisitBinding.inflate(con.layoutInflater,parent,false)
        return VhItem(binding, clickListener)
    }

    override fun onBindViewHolder(holder: AdapterVisits.VhItem, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class VhItem(
        val binding: ItemVisitBinding,
        private var clickListener: RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root

    ), View.OnClickListener {


        fun bind(item: VisitListItem) {

            binding.tvVisitName.text = item.title
            binding.tvCompany.text = item.company.companyName
            if(item.company.active)
                binding.tvStatus.text = "active"
            else
                binding.tvStatus.text = "pending"

            var calFrom = Calendar.getInstance()
            var calTo = Calendar.getInstance()

           // calFrom.time.time = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssss").parse(item.fromTime).time
           // calTo.time.time = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssss").parse(item.toTime).time

            var millFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss").parse(item.fromTime).time
            var millTo = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss").parse(item.toTime).time

            var diff = millTo - millFrom

            var mins = diff / 60000


            var hours = mins/60
            var min = mins%60


            binding.tvDuration.text = SimpleDateFormat("hh:mm aa").format(Date(millFrom)) +"-\n"+SimpleDateFormat("hh:mm aa").format(Date(millTo))+"\n("+hours+"hrs"+min+"mins"+")"


            if(layoutPosition==items.size-1)
                binding.llUnderLine.setBackgroundResource(R.color.transparent)
        }


        init {
            binding.root.setOnClickListener(this)



        }

        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }

}