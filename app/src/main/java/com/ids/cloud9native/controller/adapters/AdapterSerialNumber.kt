package com.ids.cloud9native.controller.adapters

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.controller.activities.ActivityRecords
import com.ids.cloud9native.controller.activities.ActivityVisitsRelated
import com.ids.cloud9native.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9native.databinding.ItemProductsBinding
import com.ids.cloud9native.databinding.ItemSerialNumberBinding
import com.ids.cloud9native.databinding.ItemSerialisedBinding
import com.ids.cloud9native.model.ItemSpinner
import com.ids.cloud9native.model.ProductListItem
import com.ids.cloud9native.utils.AppConstants
import com.ids.cloud9native.utils.AppHelper
import com.ids.cloud9native.utils.hide
import com.ids.cloud9native.utils.setTintImage
import com.ids.cloud9native.utils.show
import com.ids.cloud9native.utils.toHTML
import kotlin.collections.ArrayList

class AdapterSerialNumber(
    var items: ArrayList<String>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener,
    private var productId:Int,
    private var contractId:Int,
    private var productName:String,
) : RecyclerView.Adapter<AdapterSerialNumber.VhItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItem {
        val binding = ItemSerialNumberBinding.inflate(con.layoutInflater, parent, false)
        return VhItem(binding, clickListener)
    }
    override fun onBindViewHolder(holder: VhItem, position: Int) {
        holder.bind(items[position])
    }
    override fun getItemCount(): Int {
        return items.size
    }

    inner class VhItem(
        val binding: ItemSerialNumberBinding, private var clickListener: RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root
    ), View.OnClickListener {
        fun bind(item: String) {
                binding.tvNa.text = item

                binding.btVisitsRelated.setOnClickListener {
                    con.startActivity(Intent(con, ActivityVisitsRelated::class.java)
                        .putExtra("serialNumber",item)
                        .putExtra("contractId",contractId)
                        .putExtra("productId",productId)
                        .putExtra("productName",productName)
                    )
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