package com.bettypower;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.renard.betty.R;
import com.renard.betty.TextFairyApplication;


public class NetworkBroadcastReciver extends BroadcastReceiver {


    public NetworkBroadcastReciver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        TextFairyApplication application = (TextFairyApplication) context.getApplicationContext();
        if(isInternetConnected(context)) {
            // Here, I read all the pending requests from the SharedPreferences file, and execute them one by one.
            application.setPalimpsest();
            if(application.getAllPalimpsestMatch()==null) {
                Toast toast = Toast.makeText(context, R.string.device_connected, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
