package com.example.myrun;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myrun.ScreenStatusReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    //定义一个全局变量

    long time;
    private static final String TAG = "MainActivity";
    private static final String package_name="com.alibaba.android.rimet";//应用包名
    private ScreenStatusReceiver mScreenStatusReceiver;

    private EditText ed_text;
    private Button b_begin;
    private TextView et_disp;
    
    private void registSreenStatusReceiver() {
        mScreenStatusReceiver = new ScreenStatusReceiver();
        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
        screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStatusReceiver, screenStatusIF);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }
    


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ed_text = (EditText)findViewById(R.id.ed_t);
        b_begin = (Button)findViewById(R.id.bt_begin);
        et_disp = (TextView)findViewById(R.id.et_disp);

        Log.d(TAG, "myrun");
//        registSreenStatusReceiver();
        String sdk_str = ""+ Build.VERSION.SDK_INT;

        boolean b_Ignor = isIgnoringBatteryOptimizations();
        Log.d(TAG, "" + b_Ignor);
        if(!b_Ignor)
            ignoreBatteryOptimization();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {//8.0以上的开启方式不同
//            startForegroundService(it);
//        } else {
//            startService(it);
//        }

        Log.i(TAG,"sdk:"+sdk_str);
        b_begin.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){           //重写onClick方法

                try{
                    /*
                    Intent intent = new Intent();
                    intent.setClassName("com.alibaba.android.rimet", "com.alibaba.android.rimet.biz.LaunchHomeActivity");
                    startActivity(intent);

                     */
                    /*
                    PackageManager packageManager = getPackageManager();//获取包管理器
                    Intent it = packageManager.getLaunchIntentForPackage(package_name);
                    startActivity(it);//启动应用
                    Log.d(TAG, "success");
                    */

                    
                    String text = ed_text.getText().toString();
                    int i_text = Integer.parseInt(text);
                    String disp_time="";//提示时间

                    Date date = new Date(System.currentTimeMillis()+i_text*60*1000);
                    disp_time =  new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format(date);
                    Log.d(TAG, "时间:" + disp_time);
                    Log.d(TAG,""+i_text);

                    et_disp.setText(disp_time);
                    Intent intent = new Intent(MainActivity.this,LongRunningService.class);
                    intent.putExtra("value",text);
                    intent.putExtra("run", false);
                    ed_text.setEnabled(false);
                    b_begin.setEnabled(false);

                    Toast.makeText(MainActivity.this, "运行成功！", Toast.LENGTH_SHORT).show();
                    startService(intent);
                }catch (Exception e) {
                    Log.e(TAG, "error");
                    e.printStackTrace();
                }
            }
        });






    }


    @Override
    // 监听返回按键（重写activity方法）
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - time > 1500) {
                Toast.makeText(this, "再按一次退出程序！", Toast.LENGTH_SHORT).show();
                time = System.currentTimeMillis();
            } else {
//                unregisterReceiver(mScreenStatusReceiver);
                stopService(new Intent(this,LongRunningService.class));


                //退出
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 忽略电池优化
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void ignoreBatteryOptimization() {
        try{
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            boolean hasIgnored = powerManager.isIgnoringBatteryOptimizations(this.getPackageName());
            if(!hasIgnored) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:"+getPackageName()));
                startActivity(intent);
            }
        }catch(Exception e){
            //TODO :handle exception
            Log.e(TAG,e.getMessage());
        }
    }


}