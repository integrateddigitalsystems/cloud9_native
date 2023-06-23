package com.ids.cloud9.controller.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ItemProductsBinding
import com.ids.cloud9.model.ItemSpinner
import com.ids.cloud9.model.ProductListItem
import com.ids.cloud9.utils.AppHelper
import com.ids.cloud9.utils.setCheckText
import com.ids.cloud9.utils.setTintImage
import kotlin.collections.ArrayList

class AdapterProducts(
    var items: ArrayList<ProductListItem>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener
) : RecyclerView.Adapter<AdapterProducts.VhItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItem {
        val binding = ItemProductsBinding.inflate(con.layoutInflater, parent, false)
        return VhItem(binding, clickListener)
    }
    override fun onBindViewHolder(holder: VhItem, position: Int) {
        holder.bind(items[position])
    }
    override fun getItemCount(): Int {
        return items.size
    }
    fun setUpStatusReasonSpinner(pos: Int, binding: ItemProductsBinding) {
        val arrSpin: ArrayList<ItemSpinner> = arrayListOf()
        arrSpin.clear()
        val reps = items.get(pos).reports
        for (item in reps)
            arrSpin.add(ItemSpinner(item.id, item.name, false, true))
        arrSpin.get(0).selected = true
        val adapterSpin =
            AdapterSpinner(con, R.layout.spinner_text_item, arrSpin)
        binding.spReports.adapter = adapterSpin
        adapterSpin.setDropDownViewResource(R.layout.spinner_new)
        binding.spReports.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    binding.tvReport.text = arrSpin.get(position).name
                    for (itm in arrSpin)
                        itm.selected = false
                    arrSpin.get(position).selected = true

                    if (items.get(pos).reports.isNotEmpty()){
                        for (i in items.get(pos).reports)
                            i.selected =false
                    }
                    items.get(pos).reports.get(position).selected = true
                    adapterSpin.notifyDataSetChanged()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
    }
    inner class VhItem(
        val binding: ItemProductsBinding, private var clickListener: RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root
    ), View.OnClickListener {
        fun bind(item: ProductListItem) {
            binding.tvProductName.setCheckText(item.customProductName)
            binding.tvDesc.setCheckText(item.customProductDescription)
            try {
                if (item.unitId != null && MyApplication.units.size > 0) {
                    val unit = MyApplication.units.find {
                        if (item.id != null) {
                            it.id == item.unitId
                        } else {
                            false
                        }
                    }
                    if (unit != null)
                        binding.tvUnit.text = unit.name
                    else {
                        binding.tvUnit.text =
                            if (!item.product.unit.name.isNullOrEmpty()) item.product.unit.name else ""
                    }
                } else binding.tvUnit.text =
                    if (!item.product.unit.name.isNullOrEmpty()) item.product.unit.name else ""
            } catch (ex: Exception) {
                binding.tvUnit.text = ""
            }
            var SN = ""
            if (item.serialNumbers.size > 0) {
               for(it in item.serialNumbers.indices){
                   if(it == item.serialNumbers.size-1){
                       SN = SN+item.serialNumbers.get(it)
                   }else{
                       SN = SN+item.serialNumbers.get(it)+"-"
                   }
               }
            } else {
                SN = "N/A"
            }
            binding.tvSN.text = SN
            binding.tvQuantity.text = item.quantity.toString()
            if (items.get(layoutPosition).reports.size > 0) {
                setUpStatusReasonSpinner(layoutPosition, binding)
                binding.ivReport.setTintImage(
                    R.color.black
                )
            }else {
                binding.tvReport.text = con.getString(R.string.no_data)
                AppHelper.setTextColor(con, binding.tvReport, R.color.gray_border_item)
            }
        }
        init {
            binding.root.setOnClickListener(this)
            binding.ivReport.setOnClickListener(this)
            binding.llJobReport.setOnClickListener(this)
            binding.btEditProduct.setOnClickListener(this)
            binding.btClose.setOnClickListener(this)
            binding.btRecords.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }
}