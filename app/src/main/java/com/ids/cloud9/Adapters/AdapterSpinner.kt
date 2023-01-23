package com.ids.cloud9.Adapters

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ids.cloud9.R
import com.ids.cloud9.model.ItemSpinner
import com.ids.cloud9.utils.AppHelper

class AdapterSpinner(
    context: Context, textViewResourceId: Int,
    private val values: ArrayList<ItemSpinner>,
    private val type:Int,
    val roundedSpin : Boolean?=false
) : ArrayAdapter<ItemSpinner>(context, textViewResourceId, values) {
    var con = context

    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): ItemSpinner {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent) as TextView
        label.text = values[position].name
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.resources.getDimension(R.dimen.normal_font))
        label.gravity= Gravity.START
        if (values[position].id==-1) label.setTextColor(ContextCompat.getColor(con, R.color.gray_app_bg))
        else label.setTextColor(ContextCompat.getColor(con, R.color.colorAccent))
        return label
    }

    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        label.text = values[position].name
        if (values[position].id==-1)
            label.setTextColor(ContextCompat.getColor(con, R.color.gray_border_item))
        else {


            if (values[position].selected!!) {
                label.setTextColor(AppHelper.getColor(con, R.color.white))
                if(position == values.size -1){
                    if(roundedSpin!!)
                        label.setBackgroundResource(R.drawable.rounded_bottom_primary)
                    else
                        label.setBackgroundColor(AppHelper.getColor(con, R.color.colorPrimary))
                }else {
                    label.setBackgroundColor(AppHelper.getColor(con, R.color.colorPrimary))
                }
            } else {
                label.setBackgroundColor(AppHelper.getColor(con, R.color.transparent))
                label.setTextColor(AppHelper.getColor(con, R.color.colorAccent))
            }
        }


        return label
    }
}