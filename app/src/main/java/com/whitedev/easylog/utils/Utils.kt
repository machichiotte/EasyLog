package com.whitedev.easylog.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.whitedev.easylog.R
import com.whitedev.easylog.utils.Constants.Companion.BASE_URL
import com.whitedev.easylog.utils.Constants.Companion.PREFS_ID
import com.whitedev.easylog.utils.Constants.Companion.USER_BASE_URL

class Utils {
    
    companion object {
        
        fun showSnackBar(content: String, isSuccess: Boolean, view: View) {
            val snackBar = Snackbar.make(
                view, // Parent view
                content, // Message to show
                Snackbar.LENGTH_LONG // How long to display the message.
            )
            
            // change snackbar text color
            val snackbarTextId = android.support.design.R.id.snackbar_text
            val textView = snackBar.view.findViewById(snackbarTextId) as TextView
            
            if (isSuccess)
                textView.setTextColor(
                    ContextCompat.getColor(view.context, android.R.color.holo_green_light)
                )
            else
                textView.setTextColor(
                    ContextCompat.getColor(view.context, android.R.color.holo_red_light)
                )
            
            snackBar.show()
        }
        
        fun isInternetconnected(ct: Context): Boolean {
            
            //todo remettre quand phone
            return true
            
            
            /*
              val connected: Boolean
              //get the connectivity manager object to identify the network state.
              val connectivityManager = ct.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
              //Check if the manager object is NULL, this check is required. to prevent crashes in few devices.


              if (connectivityManager != null) {
                  //Check Mobile data or Wifi net is present

                  connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state ==
                          NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(
                      ConnectivityManager.TYPE_WIFI
                  ).state == NetworkInfo.State.CONNECTED
                  return connected
              } else {
                  return false
              } */
            
        }
        
        fun playNotif(context: Context?, isSuccess: Boolean) = try {
            val mPlayer: MediaPlayer = if (isSuccess)
                MediaPlayer.create(context, R.raw.plucky)
            else
                MediaPlayer.create(context, R.raw.error)
            
            mPlayer.start()
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        fun showDialogColis(context: Context?, nbColis: Int) {
            context?.let {
                AlertDialog.Builder(it)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(it.getString(R.string.colis_dialog_title, nbColis))
                    .setMessage(it.getString(R.string.scan_again_for_confirmation))
                    .setPositiveButton(it.getString(R.string.close)) { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
            }
        }
        
        fun checkBaseUrl(activity: Activity): String {
            var baseUrl = BASE_URL
            
            activity.getSharedPreferences(PREFS_ID, AppCompatActivity.MODE_PRIVATE)?.getString(USER_BASE_URL, null)
                ?.let {
                    baseUrl = it
                }
            
            return baseUrl
        }
        
        @SuppressLint("HardwareIds")
        fun getBluetoothMac(context: Context): String? {
            var result: String? = null
            if (context.checkCallingOrSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Hardware ID are restricted in Android 6+
                    // https://developer.android.com/about/versions/marshmallow/android-6.0-changes.html#behavior-hardware-id
                    // Getting bluetooth mac via reflection for devices with Android 6+
                    result = android.provider.Settings.Secure.getString(
                        context.contentResolver,
                        "bluetooth_address"
                    )
                } else {
                    val bta = BluetoothAdapter.getDefaultAdapter()
                    result = if (bta != null) bta.address else ""
                }
            }
            return result
        }
        
        @SuppressLint("HardwareIds")
        fun getDeviceId(context: Context): String {
            return Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }
        
        
    }
}