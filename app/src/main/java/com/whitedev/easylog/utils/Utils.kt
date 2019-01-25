package com.whitedev.easylog.utils

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
            }
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

    }
}