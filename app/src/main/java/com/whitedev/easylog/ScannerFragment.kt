package com.whitedev.easylog

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import com.google.zxing.Result
import com.whitedev.easylog.event.EventCamera
import com.whitedev.easylog.event.EventRestartCamera
import com.whitedev.easylog.utils.Constants.Companion.BASE_TIMER
import kotlinx.android.synthetic.main.fragment_scanner.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ScannerFragment : Fragment(), ZXingScannerView.ResultHandler {
    
    private var mScannerView: ZXingScannerView? = null
    
    private var timing: Long = BASE_TIMER.toLong()
    
    companion object {
        
        fun newInstance(): ScannerFragment {
            return ScannerFragment()
        }
        
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        mScannerView = scanner_s
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //val inflater = activity!!.menuInflater
        inflater.inflate(R.menu.menu_action_bar_scan, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.itemId) {
            R.id.action_timer -> {
                openTimerSetting()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onPause() {
        mScannerView!!.stopCamera()
        EventBus.getDefault().unregister(this)
        super.onPause()
    }
    
    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
        prepareScanner()
    }
    
    override fun handleResult(rawResult: Result) {
        val qrCode = (rawResult.text.substring(rawResult.text.lastIndexOf("/") + 1)).trim()
        EventBus.getDefault().post(EventCamera(qrCode))
    }
    
    private fun openTimerSetting() {
        val dialogBuilder = AlertDialog.Builder(context!!)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog_seekbar, null)
        dialogBuilder.setView(dialogView)
        
        val tvTiming = dialogView.findViewById(R.id.tv_timing) as TextView
        val seek = dialogView.findViewById(R.id.seekbar) as SeekBar
        
        seek.progress = timing.toInt()
        
        tvTiming.text = timing.toString()
        
        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvTiming.text = (progress).toString()
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            
        })
        
        dialogBuilder.setTitle(getString(R.string.modify_scan_timing_title))
        dialogBuilder.setPositiveButton(getString(R.string.validate)) { _, _ ->
            timing = tvTiming.text.toString().toLong()
            
        }
        dialogBuilder.setNegativeButton(getString(R.string.cancel)) { _, _ ->
        
        }
        val b = dialogBuilder.create()
        b.show()
    }
    
    private fun prepareScanner() {
        
        mScannerView?.let {
            
            it.startCamera()
            it.setResultHandler(this)
            
            if (activity?.packageManager!!.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                var isFlashOn = false
                
                val flashButton: FloatingActionButton = fab_flash_s
                flashButton.visibility = View.VISIBLE
                
                flashButton.setOnClickListener {
                    mScannerView!!.flash = !isFlashOn
                    isFlashOn = !isFlashOn
                    
                    if (isFlashOn)
                        fab_flash_s.setImageResource(R.drawable.ic_flash_off_black_24dp)
                    else
                        fab_flash_s.setImageResource(R.drawable.ic_flash_on_black_24dp)
                }
            }
        }
    }
    
    @Subscribe
    fun restartCamera(event: EventRestartCamera) {
        Handler().postDelayed({ mScannerView!!.resumeCameraPreview(this) }, timing)
    }
    
}