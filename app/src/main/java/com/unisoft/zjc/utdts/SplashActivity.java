//package com.weex.app;
package com.unisoft.zjc.utdts;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.unisoft.zjc.utdts.R;
import com.weex.plugins.heatrenewal.DownLoaderTask;
import com.weex.plugins.heatrenewal.FileDownLoaderTask;
import com.weex.plugins.heatrenewal.ZipExtractorTask;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static Activity activity;

    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CAMERA};

    private static final int PM_CAMERA_AND_WIFI = 1002;
    SharedPreferences sp;
    Boolean isFill;
    String PATH;

    public ZipExtractorTask.ZipOverListener mZipOverListener = new ZipExtractorTask.ZipOverListener() {
        @Override
        public void zipOver() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadView();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View textView = findViewById(R.id.fullscreen_content);
        PATH = Environment.getExternalStorageDirectory() + "/" + getApplication().getPackageName() + "/";
        activity = this;

        if (Build.VERSION.SDK_INT >= 23) {//判断当前系统是不是Android6.0
            methodRequiresTwoPermission();


        } else {
            doDownLoadWork();
        }


//    AnimationSet animationSet = new AnimationSet(false);
//    animationSet.addAnimation(scaleAnimation);
//    animationSet.addAnimation(rotateAnimation);
//    animationSet.setDuration(0);
//
//    animationSet.setAnimationListener(new Animation.AnimationListener() {
//      @Override
//      public void onAnimationStart(Animation animation) {
//      }
//
//      @Override
//      public void onAnimationEnd(Animation animation) {
//        Intent intent = new Intent(SplashActivity.this, WXPageActivity.class);
//        Uri data = getIntent().getData();
//        if (data != null) {
//          intent.setData(data);
//        }
//        intent.putExtra("from", "splash");
//        startActivity(intent);
//        finish();
//      }
//
//      @Override
//      public void onAnimationRepeat(Animation animation) {
//      }
//    });
//    textView.startAnimation(animationSet);
    }

    private void loadView() {
        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
        String isVersion = sp.getString("isVersion", "");
        String value = "";
        String isLoaderValue="";
        String isLoaderValueText="";
//        String value = "6.0.0";
        if (isVersion.equals("true")) {
            isLoaderValueText = sp.getString("isLoaderValueText", "");
        } else
            isLoaderValue = sp.getString("isLoaderValue", "");

        SharedPreferences.Editor edit = sp.edit();
        if (isVersion.equals("true")) {
            edit.putString("isValueTest", isLoaderValueText);
        } else
            edit.putString("isValue", isLoaderValue);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        edit.putString("jsLastUpdateTime", simpleDateFormat.format(date));
        //提交数据存入到xml文件中
        edit.commit();
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Intent intent = new Intent(SplashActivity.this, WXPageActivity.class);
        Uri data = getIntent().getData();
        if (data != null) {
            intent.setData(data);
        }
        intent.putExtra("from", "splash");
        startActivity(intent);
        finish();
    }

    /**
     * 下载
     */
    private void doDownLoadWork() {
        isFill = false;
        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
        String value = sp.getString("isVersion", "");
        String url = "";
        if (value != null) {
            if (value.equals("true")) {
                    url = "https://exeutest.blob.core.chinacloudapi.cn/app/dtsversion.json";
            } else
                url = "http://tt.ab-inbev.cn/TrackApp/AppUpdate/dtsversion.json";
        } else {
            url = "http://tt.ab-inbev.cn/TrackApp/AppUpdate/dtsversion.json";
        }
        DownLoaderTask task = new DownLoaderTask(url,
                PATH, this, mZipOverListener, isFill);
        //DownLoaderTask task = new DownLoaderTask("http://192.168.9.155/johnny/test.h264", getCacheDir().getAbsolutePath()+"/", this);
        task.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }

    }

    @AfterPermissionGranted(PM_CAMERA_AND_WIFI)
    private void methodRequiresTwoPermission() {
        //String[] perms = {Manifest.permission.CAMERA, Manifest.permission.CHANGE_WIFI_STATE};
        String[] perms = {android.Manifest.permission.CAMERA, android.Manifest.permission.CHANGE_WIFI_STATE};
        if (EasyPermissions.hasPermissions(this, PERMISSIONS_STORAGE)) {
            // Already have permission, do the thing
            // ...
            doDownLoadWork();

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.camera_and_wifi_rationale),
                    PM_CAMERA_AND_WIFI, PERMISSIONS_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
