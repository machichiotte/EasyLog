package com.whitedev.easylog

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.whitedev.easylog.event.EventCamera
import com.whitedev.easylog.event.EventDouchette
import com.whitedev.easylog.pojo.Barcode
import com.whitedev.easylog.utils.Constants.Companion.NOT_CONFIRM
import kotlinx.android.synthetic.main.fragment_douchette.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class DouchetteFragment : Fragment() {
    
    private lateinit var barcodeList: ArrayList<Barcode>
    private var barcode: String = ""
    
    companion object {
        fun newInstance(): DouchetteFragment {
            return DouchetteFragment()
        }
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
    
    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }
    
    override fun onPause() {
        EventBus.getDefault().unregister(this)
        super.onPause()
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_douchette, container, false)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeList = ArrayList()
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareRecycler()
        populateAdapter()
    }
    
    private fun prepareRecycler() {
        val layout = LinearLayoutManager(context)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        rv_barcode.layoutManager = layout
    }
    
    private fun populateAdapter() {
        rv_barcode.adapter = BarcodeAdapter(barcodeList, context)
        rv_barcode.adapter.notifyDataSetChanged()
    }
    
    @Subscribe
    fun handleBarcode(event: EventDouchette) {
        
        barcodeList.filterIndexed { index, barcode ->
            if (barcode.barcodeValue == event.barcode && barcode.sentMsg == NOT_CONFIRM) {
                barcodeList.removeAt(index)
            }
            true
        }
        
        barcodeList.add(Barcode(event.barcode, event.isSent))
        
        populateAdapter()
        
        EventBus.getDefault().post(EventCamera(barcode))
        barcode = ""
    }
    
}