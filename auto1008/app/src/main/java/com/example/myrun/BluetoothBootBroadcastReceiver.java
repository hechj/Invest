package com.example.myrun;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothBootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BootBroadcastReceiver";
    private static final boolean DBG = true;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (DBG) {
                Log.d(TAG, "onReceive BOOT_COMPLETED_ACTION");
            }
            Intent toIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            context.startActivity(toIntent);
        }
    }


}
