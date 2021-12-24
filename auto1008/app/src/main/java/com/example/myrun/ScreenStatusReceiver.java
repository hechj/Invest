package com.example.myrun;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;



public class ScreenStatusReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.d("ScreenStatusReceiver", "onReceive ACTION_SCREEN_OFF");
            Intent activity = new Intent(context,OnePxActivity.class);
            activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activity);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.d("ScreenStatusReceiver", "onReceive ACTION_SCREEN_ON");
            // 用户解锁，关闭Activity
            // 这里发个广播是什么鬼，其实看下面OnePxAcitivity里面的代码就知道了，发这个广播就是为了finish掉OnePxActivity
            Intent broadcast = new Intent("FinishActivity");
            context.sendBroadcast(broadcast);//发送对应的广播

            Intent it = new Intent(context,LongRunningService.class);
            context.startService(it);
        }
    }
}



