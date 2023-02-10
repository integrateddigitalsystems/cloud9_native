package com.ids.cloud9.controller.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ids.cloud9.R
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ItemProductsBinding
import com.ids.cloud9.databinding.ItemReasonDialogBinding
import com.ids.cloud9.model.ItemSpinner
import com.ids.cloud9.model.ProductList
import com.ids.cloud9.model.ProductListItem
import com.ids.cloud9.model.ProductsItem
import com.ids.cloud9.utils.AppHelper
import com.ids.cloud9.utils.setCheckText
import java.util.ArrayList

class AdapterProducts(
    var items : ArrayList<ProductListItem>,
    var con: Activity,
    private var clickListener: RVOnItemClickListener
) : RecyclerView.Adapter<AdapterProducts.VhItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItem {
        val binding = ItemProductsBinding.inflate(con.layoutInflater,parent,false)
        return VhItem(binding, clickListener)
    }

    override fun onBindViewHolder(holder: VhItem, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class VhItem(
        val binding: ItemProductsBinding,
        private var clickListener: RVOnItemClickListener
    ) : RecyclerView.ViewHolder(
        binding.root

    ), View.OnClickListener {


        fun bind(item: ProductListItem) {

            binding.tvProductName.setCheckText(item.product.name)
            if(item.product.unit!=null && !item.product.unit.name.isNullOrEmpty())
                binding.tvUnit.setCheckText(item.product.unit.name)
            else
                binding.tvUnit.text = ""
            binding.tvSN.text =if(item.serialNumbers!=null && item.serialNumbers!!.size > 0 ){
                item.serialNumbers!!.get(0)
            }else{
                "N/A"
            }
            binding.tvQuantity.text = item.quantity.toString()




        }


        init {
            binding.root.setOnClickListener(this)
            binding.llJobReport.setOnClickListener(this)
            binding.btEditProduct.setOnClickListener(this)
            binding.btClose.setOnClickListener(this)



        }

        override fun onClick(p0: View?) {
            clickListener.onItemClicked(p0!!, layoutPosition)
        }
    }

}