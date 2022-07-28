package com.ids.librascan.controller.Adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.ids.librascan.controller.Adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ItemQrDataBinding
import com.ids.librascan.databinding.ItemSessionBinding
import com.ids.librascan.db.QrCode
import com.ids.librascan.db.SessionQrcode
import com.ids.librascan.db.Sessions
import kotlinx.coroutines.launch
import utils.hide
import utils.show
import java.util.*

class AdapterSession(private var items:ArrayList<SessionQrcode>, private val itemClickListener: RVOnItemClickListener) : RecyclerView.Adapter<AdapterSession.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemSessionBinding = ItemSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemSessionBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemSessionBinding.tvNameSession.text = items[position].sessionName
        holder.itemSessionBinding.tvWarehouses.text = items[position].warehouseName
        holder.itemSessionBinding.tvDate.text = items[position].date
        if(items[position].count>0){
            holder.itemSessionBinding.llSync.show()
        }
        else holder.itemSessionBinding.llSync.hide()

        if(MyApplication.languageCode =="en"){
            holder.itemSessionBinding.swipe.showMode = SwipeLayout.ShowMode.LayDown
            holder.itemSessionBinding.swipe.addDrag(SwipeLayout.DragEdge.Right,holder.itemSessionBinding.llSwipe)
        }
        else{
            holder.itemSessionBinding.swipe.showMode = SwipeLayout.ShowMode.LayDown
            holder.itemSessionBinding.swipe.addDrag(SwipeLayout.DragEdge.Left,holder.itemSessionBinding.llSwipe)
            holder.itemSessionBinding.swipe.isRightSwipeEnabled = false
        }

    }
    override fun getItemCount(): Int {
        return items.size
    }

    inner class MyViewHolder( val itemSessionBinding: ItemSessionBinding) : RecyclerView.ViewHolder(itemSessionBinding.root),View.OnClickListener{
        init {
            itemView.setOnClickListener(this)
            itemSessionBinding.llSync.setOnClickListener(this)
            itemSessionBinding.llScan.setOnClickListener(this)
            itemSessionBinding.tVDelete.setOnClickListener(this)
            itemSessionBinding.llItem.setOnClickListener(this)

        }
        override fun onClick(v: View) {
           itemClickListener.onItemClicked(v, layoutPosition)
        }
    }

}