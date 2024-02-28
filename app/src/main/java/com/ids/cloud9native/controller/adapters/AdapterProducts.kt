package com.ids.cloud9native.controller.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9native.databinding.ItemProductsBinding
import com.ids.cloud9native.model.ItemSpinner
import com.ids.cloud9native.model.ProductListItem
import com.ids.cloud9native.utils.AppConstants
import com.ids.cloud9native.utils.AppHelper
import com.ids.cloud9native.utils.hide
import com.ids.cloud9native.utils.setTintImage
import com.ids.cloud9native.utils.show
import com.ids.cloud9native.utils.toHTML
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
            if(item.product.name!!.contains(AppConstants.OTHER_PRODUCT_NAME)) {
                if(!item.customProductName.isNullOrEmpty()){
                    binding.tvProductName.text = item.customProductName
                }else{
                    binding.tvProductName.text = ""
                }
                if(!item.customProductDescription.isNullOrEmpty()){
                    binding.tvDesc.toHTML(item.customProductDescription!!)
                }else{
                    binding.tvDesc.text = ""
                }
            }else{
                if(!item.product.name.isNullOrEmpty()){
                    binding.tvProductName.text = item.product.name
                }else{
                    binding.tvProductName.hide()
                    binding.tvProductName.text = ""
                }
                if(!item.product.description.isNullOrEmpty()){
                    binding.tvDesc.toHTML(item.product.description!!)
                }else{
                    binding.tvDesc.hide()
                    binding.tvDesc.text = ""
                }
            }
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
                            if (!item.product.unit!!.name.isNullOrEmpty()) item.product.unit.name else ""
                    }
                } else binding.tvUnit.text =
                    if (!item.product.unit!!.name.isNullOrEmpty()) item.product.unit.name else ""
            } catch (ex: Exception) {
                binding.tvUnit.text = ""
            }
            var SN = ""
            if (item.serialNumbers!!.size > 0) {
                binding.rvSerialNumbers.show()
                binding.tvNoData.hide()
                val adapter = AdapterSerialNumber(item.serialNumbers,con,clickListener,
                    item.productId!!, MyApplication.selectedVisit!!.contractId!!,item.product.name
                )
                binding.rvSerialNumbers.layoutManager = LinearLayoutManager(con)
                binding.rvSerialNumbers.adapter = adapter
//               for(it in item.serialNumbers.indices){
//                   if(it == item.serialNumbers.size-1){
//                       SN = SN+item.serialNumbers.get(it)
//                   }else{
//                       SN = SN+item.serialNumbers.get(it)+"\n"
//                   }
//               }
            } else {
                binding.rvSerialNumbers.hide()
                binding.tvNoData.show()
                SN = "N/A"
            }
           // binding.tvSN.text = SN
            binding.tvQuantity.text = item.quantity.toString()
            if (items.get(layoutPosition).reports.size > 0 && MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_ARRIVED)) {
                setUpStatusReasonSpinner(layoutPosition, binding)
                binding.ivReport.setTintImage(
                    R.color.black
                )
            }else {
                if(items.get(layoutPosition).reports.size == 0)
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