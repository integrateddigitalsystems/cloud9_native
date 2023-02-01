package com.ids.cloud9.controller.adapters

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
    var items: ArrayList<ItemSpinner>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener,
    var type: Boolean
) : RecyclerView.Adapter<AdapterDialog.VhItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItem {
        val binding = ItemReasonDialogBinding.inflate(con.layoutInflater, parent, false)
        return VhItem(binding, clickListener)
    }

    override fun onBindViewHolder(holder: VhItem, position: Int) {
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





            if (!item.selectable!!) {

                binding.llItemReason.setBackgroundResource(R.drawable.gray_tint_border)

                AppHelper.setTextColor(con, binding.tvReason, R.color.gray_border_item)


            } else {

                if (item.selected != null && item.selected!!) {

                    binding.llItemReason.setBackgroundResource(R.drawable.rounded_selected_primary)

                    AppHelper.setTextColor(con, binding.tvReason, R.color.colorPrimary)

                } else {
                    binding.llItemReason.setBackgroundResource(R.drawable.rounded_white_border)

                    AppHelper.setTextColor(con, binding.tvReason, R.color.colorAccent)

                }
            }


        }


        init {
            binding.llItemReason.setOnClickListener(this)


        }

        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }

}