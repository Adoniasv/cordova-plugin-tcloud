package com.tcloud.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;

public class BootCompleted extends BroadcastReceiver {

    private static String TAG = "BootCompleted";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG, "onReceive");
        Log.e(TAG, intent.getAction());
        
        // Inicio GPS
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()) ) {
            Intent pushIntent = new Intent(context, LocationService.class);
            context.startService(pushIntent);
        }

    }
}