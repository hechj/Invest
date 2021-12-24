package com.example.myrun;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;



public class LongRunningService extends Service {
    private static final String TAG = "LongRunningService";
    public boolean run_state;
    public static int i_min=1;

    private AlarmManager manager ;
    private PendingIntent pendingIntent;

    /**
     * android 5.0之后如何获取当前运行的应用包名？
     * @param context
     * @return
     */
    public static String getCurrentPkgName(Context context) {
        ActivityManager.RunningAppProcessInfo currentInfo = null;

        Field field = null;
        int START_TASK_TO_FRONT = 2;
        String pkgName = null;
        try {
            field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List appList = am.getRunningAppProcesses();
        List<ActivityManager.RunningAppProcessInfo> processes = ((ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo app : processes) {
            if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                Integer state = null;
                try {
                    state = field.getInt(app);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (state != null && state == START_TASK_TO_FRONT) {
                    currentInfo = app;
                    break;
                }
            }
        }
        if (currentInfo != null) {
            pkgName = currentInfo.processName;
        }
        return pkgName;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        run_state = intent.getBooleanExtra("run",false);
        Log.d(TAG,  "run_state:" + run_state);
        String s_min=null;
        if(!run_state) {
            try {
                s_min = intent.getStringExtra("value");
            }catch (Exception e ) {
                e.printStackTrace();
            }
        }
        if (s_min != null) {
            Log.d(TAG, "mins:" + s_min);
            i_min = Integer.parseInt(s_min);
            Log.d(TAG, "i_min:" + i_min);
        }


        if(run_state) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String S_currpkg = getCurrentPkgName(getApplication());
                    if (S_currpkg!= null)
                        Log.d(TAG, S_currpkg);

                    if (!S_currpkg.equals("com.alibaba.android.rimet")) {
                        PackageManager packageManager = getPackageManager();//获取包管理器
                        Intent it = packageManager.getLaunchIntentForPackage("com.alibaba.android.rimet");
                        startActivity(it);//启动应用
                    }
                }
            }).start();
        }
        Log.d(TAG, "success");


        long anHour = i_min*60*1000;
        long elapsed = SystemClock.elapsedRealtime();

        long triggerAtTime = elapsed+anHour;
        Log.d(TAG,"最大长整型数:"+Long.MAX_VALUE);
        Log.d(TAG,"i_min:"+i_min);
        System.out.println("分钟数:"+i_min);
        Log.d(TAG, "elapsed:" + elapsed);
        Log.d(TAG, "triggerAtTime:" + triggerAtTime);

        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this,AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,0,i,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 6.0
            manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//  4.4
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime, pendingIntent);
        } else {
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime, pendingIntent);
        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        Log.e(TAG, TAG + " onDestory");
        manager.cancel(pendingIntent);
        super.onDestroy();
    }

}