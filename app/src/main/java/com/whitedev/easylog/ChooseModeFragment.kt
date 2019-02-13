package com.whitedev.easylog

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.whitedev.easylog.event.EventShowDouchetteButton
import com.whitedev.easylog.utils.Constants
import com.whitedev.easylog.utils.Constants.Companion.FAILURE
import com.whitedev.easylog.utils.Utils
import kotlinx.android.synthetic.main.fragment_choose_mode.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class ChooseModeFragment : Fragment() {
    
    private lateinit var mListener: ModeActivityInterface
    
    companion object {
        fun newInstance(): ChooseModeFragment {
            return ChooseModeFragment()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mListener = activity as ModeActivityInterface
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement MyInterface ")
        }
    }
    
    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
        
        if (isBarcodeNotSent())
            tv_send_data.visibility = View.VISIBLE
        else
            tv_send_data.visibility = View.GONE
    }
    
    private fun isBarcodeNotSent(): Boolean {
        for (barcode in mListener.getBarcodes) {
            if (barcode.sentMsg == FAILURE) {
                return true
            }
        }
        return false
    }
    
    override fun onPause() {
        EventBus.getDefault().unregister(this)
        super.onPause()
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_choose_mode, container, false)
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_action_bar_choose, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.itemId) {
            R.id.action_scan_settings -> {
                openSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun openSettings() {
        val dialogBuilder = AlertDialog.Builder(context!!)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog_settings, null)
        dialogBuilder.setView(dialogView)
        
        val llBluetooth = dialogView.findViewById(R.id.ll_bt) as LinearLayout
        val tvDeviceId = dialogView.findViewById(R.id.tv_uuid) as TextView
        val tvBluetoothId = dialogView.findViewById(R.id.tv_bt) as TextView
        
        tvDeviceId.text = Utils.getDeviceId(context!!)
        
        this.context?.let {
            if (Utils.getBluetoothMac(it) != null) {
                llBluetooth.visibility = View.VISIBLE
                tvBluetoothId.text = Utils.getBluetoothMac(it)
            } else {
                llBluetooth.visibility = View.GONE
            }
        }
        
        val b = dialogBuilder.create()
        b.show()
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupButtons()
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_CAMERA_ACCESS -> {
                redirectCameraFragment()
                return
            }
        }
    }
    
    private fun redirectCameraFragment() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.root_layout, ScannerFragment.newInstance(), "CameraFragment")
            ?.addToBackStack("CameraFragment")
            ?.commit()
    }
    
    private fun redirectDouchetteFragment() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.root_layout, DouchetteFragment.newInstance(), "DouchetteFragment")
            ?.addToBackStack("DouchetteFragment")
            ?.commit()
    }
    
    private fun setupButtons() {
        tv_scan.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.CAMERA),
                        Constants.REQUEST_CAMERA_ACCESS
                    )
                }
            } else {
                redirectCameraFragment()
            }
        }
        
        tv_douchette.setOnClickListener {
            redirectDouchetteFragment()
        }
    }
    
    @Subscribe
    fun handleBarcode(event: EventShowDouchetteButton) {
        tv_douchette.isEnabled = event.isEnable
    }
    
}