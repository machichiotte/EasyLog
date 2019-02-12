package com.whitedev.easylog

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whitedev.easylog.pojo.Barcode
import com.whitedev.easylog.utils.Constants.Companion.ENVOYE
import com.whitedev.easylog.utils.Constants.Companion.ERROR_EXPIRED_TOKEN
import com.whitedev.easylog.utils.Constants.Companion.ERROR_MISSING_TRACKING
import com.whitedev.easylog.utils.Constants.Companion.ERROR_MISSING_ZONE
import com.whitedev.easylog.utils.Constants.Companion.ERROR_SAME_ZONE
import com.whitedev.easylog.utils.Constants.Companion.FAILURE
import com.whitedev.easylog.utils.Constants.Companion.ZONE
import kotlinx.android.synthetic.main.item_barcode.view.*

class BarcodeAdapter(val items: ArrayList<Barcode>, val context: Context?) : RecyclerView.Adapter<ViewHolder>() {
    
    override fun getItemCount(): Int {
        return items.size
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_barcode, parent, false))
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvBarcode.text = items[position].barcodeValue + " : "
        holder.tvSent.text = items[position].sentMsg
        
        when (holder.tvSent.text) {
            ENVOYE -> {
                context?.let {
                    holder.tvBarcode.setTextColor(ContextCompat.getColor(it, R.color.green))
                    holder.tvSent.setTextColor(ContextCompat.getColor(it, R.color.green))
                    
                }
            }
            ZONE -> {
            }
            
            ERROR_SAME_ZONE,
            ERROR_MISSING_ZONE,
            ERROR_MISSING_TRACKING,
            ERROR_EXPIRED_TOKEN -> {
                context?.let {
                    holder.tvBarcode.setTextColor(ContextCompat.getColor(it, R.color.red))
                    holder.tvSent.setTextColor(ContextCompat.getColor(it, R.color.red))
                }
            }
            FAILURE -> {
            }
        }
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvBarcode = view.tv_barcode
    val tvSent = view.tv_sent_msg
}