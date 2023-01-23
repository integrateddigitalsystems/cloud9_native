package com.ids.cloud9.Adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9.R
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ItemReasonDialogBinding
import com.ids.cloud9.databinding.ItemVisitDateBinding
import com.ids.cloud9.model.ItemSpinner
import com.ids.cloud9.model.VisitDates
import com.ids.cloud9.utils.AppHelper
import com.ids.cloud9.utils.hide
import com.ids.cloud9.utils.show
import java.text.SimpleDateFormat
import java.util.*

class AdapterDialog(
    var items : ArrayList<ItemSpinner>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener
) : RecyclerView.Adapter<AdapterDialog.VhItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDialog.VhItem {
        val binding = ItemReasonDialogBinding.inflate(con.layoutInflater,parent,false)
        return VhItem(binding, clickListener)
    }

    override fun onBindViewHolder(holder: AdapterDialog.VhItem, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class VhItem(
        val binding: com.ids.cloud9.databinding.ItemReasonDialogBinding,
        private var clickListener: RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root

    ), View.OnClickListener {


        fun bind(item: ItemSpinner) {



            binding.tvReason.text = item.name




            if(!item.selectable!!){
                if(layoutPosition == 0){
                    binding.llItemReason.setBackgroundResource(R.drawable.rounded_top_gray)
                }else if(layoutPosition == items.size-1){
                    binding.llItemReason.setBackgroundResource(R.drawable.rounded_bottom_gray)
                }else {
                    binding.llItemReason.setBackgroundResource(R.color.gray_border_tab)
                }

            }
            if(item.selected!!){
               if(layoutPosition == 0){
                   binding.llItemReason.setBackgroundResource(R.drawable.rounded_top_primary)
               }else if(layoutPosition == items.size-1){
                   binding.llItemReason.setBackgroundResource(R.drawable.rounded_bottom_primary)
               }else {
                   binding.llItemReason.setBackgroundResource(R.color.colorPrimary)
               }

                AppHelper.setTextColor(con, binding.tvReason, R.color.white)
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