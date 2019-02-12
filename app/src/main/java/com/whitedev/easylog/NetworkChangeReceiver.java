package com.whitedev.easylog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Objects;

public class NetworkChangeReceiver extends BroadcastReceiver {

    public static final String NETWORK_SWITCH_FILTER = "com.devglan.broadcastreceiver.NETWORK_SWITCH_FILTER";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.requireNonNull(intent.getAction()).equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE")) {

            NetworkInfo networkInfo =
                    intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                Intent intnt = new Intent(NETWORK_SWITCH_FILTER);
                intnt.putExtra("is_connected", true);
                context.sendBroadcast(intnt);
            } else {

                Intent intnt = new Intent(NETWORK_SWITCH_FILTER);
                intnt.putExtra("is_connected", false);
                context.sendBroadcast(intnt);
            }


        }
    }

}