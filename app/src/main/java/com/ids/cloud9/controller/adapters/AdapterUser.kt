package com.ids.cloud9.controller.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9.R
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ItemReasonDialogBinding
import com.ids.cloud9.databinding.ItemTextBinding
import com.ids.cloud9.databinding.ItemUserBinding
import com.ids.cloud9.databinding.ItemVisitDateBinding
import com.ids.cloud9.model.ApplicationUserListItem
import com.ids.cloud9.model.ItemSpinner
import com.ids.cloud9.model.VisitDates
import com.ids.cloud9.utils.AppHelper
import com.ids.cloud9.utils.hide
import com.ids.cloud9.utils.show
import java.text.SimpleDateFormat
import java.util.*

class AdapterUser(
    var items: ArrayList<ApplicationUserListItem>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener
) : RecyclerView.Adapter<AdapterUser.VhItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItem {
        val binding = ItemUserBinding.inflate(con.layoutInflater, parent, false)
        return VhItem(binding, clickListener)
    }

    override fun onBindViewHolder(holder: VhItem, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class VhItem(
        val binding: com.ids.cloud9.databinding.ItemUserBinding,
        private var clickListener: RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root

    ), View.OnClickListener {


        fun bind(item: ApplicationUserListItem) {


            binding.tvFilter.text = item.firstName + " "+item.lastName




        }


        init {
            binding.btRemoveMore.setOnClickListener(this)


        }

        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }

}