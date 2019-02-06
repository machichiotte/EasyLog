package com.whitedev.easylog

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
import com.whitedev.easylog.utils.Utils
import kotlinx.android.synthetic.main.fragment_choose_mode.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ChooseModeFragment : Fragment() {
    
    companion object {
        fun newInstance(): ChooseModeFragment {
            return ChooseModeFragment()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        return inflater.inflate(R.layout.fragment_choose_mode, container, false)
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //val inflater = activity!!.menuInflater
        inflater.inflate(R.menu.menu_action_bar_choose, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        when (item.itemId) {
            R.id.action_scan_settings -> {
                openSettings()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
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
        
        tvDeviceId.text = Utils.getBtDeviceId(context!!)
        
        if (android.provider.Settings.Secure.getString(this.context!!.contentResolver, "bluetooth_address") != null) {
            llBluetooth.visibility = View.VISIBLE
            tvBluetoothId.text =
                    android.provider.Settings.Secure.getString(this.context!!.contentResolver, "bluetooth_address")
        } else {
            llBluetooth.visibility = View.GONE
        }
        
        val b = dialogBuilder.create()
        b.show()
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupButtons()
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
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.root_layout, ScannerFragment.newInstance(), "ScannerFragment")
                    ?.addToBackStack("ScannerFragment")
                    ?.commit()
            }
        }
        
        tv_douchette.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.root_layout, DouchetteFragment.newInstance(), "DouchetteFragment")
                ?.addToBackStack("DouchetteFragment")
                ?.commit()
        }
    }
    
    @Subscribe
    fun handleBarcode(event: EventShowDouchetteButton) {
        tv_douchette.isEnabled = event.isEnable
    }
    
}