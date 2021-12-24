package com.example.myrun;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class OnePxActivity extends AppCompatActivity {
    private static final String TAG = "OnePxActivity";
    //开启前台服务
    Intent it = new Intent(this, WhiteService.class);
    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 收到广播
            Log.d(TAG, "onReceive");
            OnePxActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        // 设置窗口位置在左上角
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.width = 1;
        params.height = 1;
        window.setAttributes(params);

        // 动态注册广播，这个广播是在屏幕亮的时候，发送广播，来关闭当前的Activity
        registerReceiver(receiver, new IntentFilter("FinishActivity"));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {//8.0以上的开启方式不同
            startForegroundService(it);
        } else {
            startService(it);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(it);
        unregisterReceiver(receiver);
        Log.e(TAG, TAG + " onDestory");
        super.onDestroy();
    }

}