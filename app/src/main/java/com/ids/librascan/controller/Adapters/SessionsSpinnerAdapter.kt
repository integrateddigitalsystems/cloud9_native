package com.ids.librascan.controller.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.ids.librascan.R
import com.ids.librascan.db.Sessions
import com.ids.librascan.db.Unit

class SessionsSpinnerAdapter (val mContext: Context, private var items:MutableList<Sessions>): ArrayAdapter<Sessions>(mContext, 0, items){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getItem(position: Int): Sessions {
        return items[position]
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_drop_down, parent, false)

        val sector = items[position].sessionName
        val tvItem = view.findViewById<TextView>(R.id.tvItem)

        tvItem.text = sector



       // Actions.overrideFonts(context, llItem)

        return view
    }
}
