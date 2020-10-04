package it.orsaferrovie.orsaferrovieapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class NetworkChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        /*final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);*/

        final android.net.NetworkInfo netinfo = connMgr.getActiveNetworkInfo();
        if (netinfo.isConnected()) {
            Intent serviceIntent = new Intent(context, DownloadService.class);
            context.startService(serviceIntent);
        }
    }
}