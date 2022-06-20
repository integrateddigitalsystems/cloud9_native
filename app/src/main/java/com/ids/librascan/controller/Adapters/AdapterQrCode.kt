package com.ids.librascan.controller.Adapters


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ids.librascan.R
import com.ids.librascan.controller.Adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.librascan.db.QrCode
import java.util.*
import kotlin.collections.ArrayList

class AdapterQrCode( private val items:ArrayList<QrCode> ,private val itemClickListener: RVOnItemClickListener) : RecyclerView.Adapter<AdapterQrCode.MyViewHolder>(),
    Filterable {
    var FilterList = ArrayList<QrCode>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_qr_data, parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.idQr.text = items[position].id.toString()
        holder.title.text = items[position].title
        holder.quantity.text = items[position].quantity
        holder.unitSelected.text = items[position].unitId


    }


    override fun getItemCount(): Int {
        return items.size
    }


    inner class MyViewHolder

    constructor (itemView :View) : RecyclerView.ViewHolder(itemView),View.OnClickListener{

        val idQr : TextView = itemView.findViewById(R.id.tvIdQrCode)
        val title : TextView = itemView.findViewById(R.id.tvTitleQrCode)
        val quantity : TextView = itemView.findViewById(R.id.tvQuantityCode)
        val unitSelected : TextView = itemView.findViewById(R.id.tvUnitCode)
        val iVDelete : ImageView = itemView.findViewById(R.id.iVDelete)

        init {
            itemView.setOnClickListener(this)
            iVDelete.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            itemClickListener.onItemClicked(v, layoutPosition)
        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    FilterList = items
                } else {
                    val resultList = ArrayList<QrCode>()
                    for (row in items) {
                        resultList.add(row)

                    }
                    FilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = FilterList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                FilterList = results?.values as ArrayList<QrCode>
                notifyDataSetChanged()
            }

        }
    }
}