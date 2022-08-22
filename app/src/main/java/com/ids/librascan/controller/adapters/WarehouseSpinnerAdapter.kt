package com.ids.librascan.controller.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.ids.librascan.databinding.ItemDropDownBinding
import com.ids.librascan.db.Warehouse

class WarehouseSpinnerAdapter (val mContext: Context, private var items:MutableList<Warehouse>): ArrayAdapter<Warehouse>(mContext, 0, items){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getItem(position: Int): Warehouse {
        return items[position]
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {

        val itemDropDownBinding = ItemDropDownBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        val sector = items[position]
        itemDropDownBinding.tvItem.text = sector.toString()
        return itemDropDownBinding.root
    }
}
