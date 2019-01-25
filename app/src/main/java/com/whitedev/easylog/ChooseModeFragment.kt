package com.whitedev.easylog

import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whitedev.easylog.event.EventShowDouchetteButton
import com.whitedev.easylog.utils.Constants
import kotlinx.android.synthetic.main.fragment_choose_mode.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ChooseModeFragment : Fragment() {

    companion object {

        fun newInstance(): ChooseModeFragment {
            return ChooseModeFragment()
        }

    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
        val bad: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()


        tv_douchette.isEnabled = bad.bondedDevices.size > 0
    }

    override fun onPause() {
        EventBus.getDefault().unregister(this)
        super.onPause()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_choose_mode, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupButtons()
    }

    private fun setupButtons() {

        tv_douchette.isEnabled = false

        tv_douchette.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.root_layout, DouchetteFragment.newInstance(), "DouchetteFragment")
                ?.addToBackStack("DouchetteFragment")
                ?.commit()
        }

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

    }

    @Subscribe
    fun handleBarcode(event: EventShowDouchetteButton) {
        tv_douchette.isEnabled = event.isEnable
    }

}