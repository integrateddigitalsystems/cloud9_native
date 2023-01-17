package com.ids.cloud9.Adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ItemVisitDateBinding
import com.ids.cloud9.model.VisitDates
import java.text.SimpleDateFormat
import java.util.*

class AdapterVisitDate(
    var items : ArrayList<VisitDates>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener
) : RecyclerView.Adapter<AdapterVisitDate.VhItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterVisitDate.VhItem {
        val binding = ItemVisitDateBinding.inflate(con.layoutInflater,parent,false)
        return VhItem(binding, clickListener)
    }

    override fun onBindViewHolder(holder: AdapterVisitDate.VhItem, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class VhItem(
        val binding: ItemVisitDateBinding,
        private var clickListener: RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root

    ), View.OnClickListener {


        fun bind(item: VisitDates) {

            var cal = Calendar.getInstance()
            cal.time.time = item.dateMill!!

            binding.tvDay.text = SimpleDateFormat("EEEE").format(Date(item.dateMill!!))
            binding.tvDate.text = SimpleDateFormat("MMMM dd, yyyy").format(Date(item.dateMill!!))

            var adapter = AdapterVisits(item.visits!!,con,clickListener)
           // binding.rvVisits.layoutManager = LinearLayoutManager(con)
           // binding.rvVisits.adapter = adapter


           // binding.rvVisits.addItemDecoration(HeaderDecoration(adapter as HeaderDecoration.StickyHeaderInterface?))


        }


        init {
            binding.root.setOnClickListener(this)



        }

        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }

}