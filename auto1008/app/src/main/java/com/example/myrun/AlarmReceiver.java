package com.example.myrun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "onReceive");

        Intent i = new Intent(context,LongRunningService.class);
        i.putExtra("run", true);
        context.startService(i);
    }
}