package com.whitedev.easylog

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whitedev.easylog.pojo.Barcode
import kotlinx.android.synthetic.main.item_barcode.view.*

class BarcodeAdapter(val items: ArrayList<Barcode>, val context: Context?) : RecyclerView.Adapter<ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_barcode, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvBarcode.text = items[position].barcodeValue
        holder.tvSent.text = items[position].sentMsg

        when (holder.tvSent.text) {
            "ENVOYE" -> {
                holder.tvBarcode.setTextColor(context!!.resources.getColor(R.color.green))
                holder.tvSent.setTextColor(context!!.resources.getColor(R.color.green))

            }
            "ZONE" -> {
            }
            "ERROR" -> {
                holder.tvBarcode.setTextColor(context!!.resources.getColor(R.color.red))
                holder.tvSent.setTextColor(context!!.resources.getColor(R.color.red))
            }
            "FAILURE" -> {
            }
        }
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvBarcode = view.tv_barcode
    val tvSent = view.tv_sent_msg
}