package com.ids.librascan.controller.Adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.ids.librascan.controller.Adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ItemQrDataBinding
import com.ids.librascan.db.QrCode
import java.util.*

class AdapterQrCode(private var items:ArrayList<QrCode>, private val itemClickListener: RVOnItemClickListener) : RecyclerView.Adapter<AdapterQrCode.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemQrDataBinding = ItemQrDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemQrDataBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemQrDataBinding.tvTitleQrCode.text = items[position].code
        holder.itemQrDataBinding.tvQuantityCode.text = items[position].quantity.toString()

        if(MyApplication.languageCode =="en"){
            holder.itemQrDataBinding.swipe.showMode = SwipeLayout.ShowMode.LayDown
            holder.itemQrDataBinding.swipe.addDrag(SwipeLayout.DragEdge.Right,holder.itemQrDataBinding.llSwipe)
        }
        else{
            holder.itemQrDataBinding.swipe.showMode = SwipeLayout.ShowMode.LayDown
            holder.itemQrDataBinding.swipe.addDrag(SwipeLayout.DragEdge.Left,holder.itemQrDataBinding.llSwipe)
            holder.itemQrDataBinding.swipe.isRightSwipeEnabled = false
        }

    }
    override fun getItemCount(): Int {
        return items.size
    }

    inner class MyViewHolder( val itemQrDataBinding: ItemQrDataBinding) : RecyclerView.ViewHolder(itemQrDataBinding.root),View.OnClickListener{
        init {
            itemView.setOnClickListener(this)
            itemQrDataBinding.tVDelete.setOnClickListener (this)
            itemQrDataBinding.tVUpdate.setOnClickListener (this)
        }
        override fun onClick(v: View) {
            itemClickListener.onItemClicked(v, layoutPosition)
        }
    }

}