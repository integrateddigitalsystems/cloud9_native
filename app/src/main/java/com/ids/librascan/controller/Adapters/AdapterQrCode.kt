package com.ids.librascan.controller.Adapters


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.ids.librascan.R
import com.ids.librascan.controller.Adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.librascan.databinding.ActivityLoginBinding
import com.ids.librascan.databinding.ItemQrDataBinding
import com.ids.librascan.db.QrCode
import java.util.*
import kotlin.collections.ArrayList

class AdapterQrCode(private var items:ArrayList<QrCode>, private val itemClickListener: RVOnItemClickListener) : RecyclerView.Adapter<AdapterQrCode.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_qr_data, parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.title.text = items[position].code
        holder.quantity.text = items[position].quantity.toString()

        if (items[position].unitId == 1 )
           holder.unitSelected.text = "Kg"
        if (items[position].unitId == 2)
            holder.unitSelected.text = "box"
        if (items[position].unitId == 3)
            holder.unitSelected.text = "item"

        holder.swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right,holder.swipeLeft)

    }
    override fun getItemCount(): Int {
        return items.size
    }



    inner class MyViewHolder

    constructor (itemView :View) : RecyclerView.ViewHolder(itemView),View.OnClickListener{

        val title : TextView = itemView.findViewById(R.id.tvTitleQrCode)
        val quantity : TextView = itemView.findViewById(R.id.tvQuantityCode)
        val unitSelected : TextView = itemView.findViewById(R.id.tvUnitCode)
        val swipeLayout : SwipeLayout = itemView.findViewById(R.id.swipe)
        val swipeLeft : LinearLayout = itemView.findViewById(R.id.bottom_wrapper)
        val tVDelete : TextView = itemView.findViewById(R.id.tVDelete)
        val tVUpdate : TextView = itemView.findViewById(R.id.tVUpdate)

        init {
            itemView.setOnClickListener(this)
            tVDelete.setOnClickListener (this)
            tVUpdate.setOnClickListener (this)
        }

        override fun onClick(v: View) {
            itemClickListener.onItemClicked(v, layoutPosition)
        }

    }

    fun filterList(filteredNames: ArrayList<QrCode>) {
        this.items = filteredNames
        notifyDataSetChanged()
    }

}