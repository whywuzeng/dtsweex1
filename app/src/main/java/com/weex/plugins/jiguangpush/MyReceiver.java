package com.weex.plugins.jiguangpush;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.taobao.weex.WXSDKInstance;

//import com.eBest.mobile.android.apis.utils.AndroidUtils;
//import com.eBest.mobile.android.visit.Login;
//import com.unisoft.cs.AppManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashMap;
import com.taobao.weex.WXSDKManager;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-INSP-Receiver";
    private MediaPlayer mediaPlayer;
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                String mesgValue=bundle.getString(JPushInterface.EXTRA_EXTRA);
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                //EXTRA_MESSAGE
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
                SharedPreferences sp = context.getSharedPreferences("User", context.MODE_PRIVATE);
                String isVersion = sp.getString("switchValue", "");
                if(isVersion.equals("true")&&mesgValue!=null)
                openAssetMusics(context,mesgValue);
                //add by zhangjiacheng
                //WXSDKInstance mWXSDKInstance = new WXSDKInstance(context);
                WXSDKInstance mWXSDKInstance= WXSDKManager.getInstance().getSDKInstance("1");
                HashMap<String,Object> params=new HashMap<>();
                String message =  JPushInterface.EXTRA_MESSAGE;
                message =  TextUtils.isEmpty(message) ? "" : message;
                params.put("message",message);
                mWXSDKInstance.fireGlobalEventCallback("app_push_notification_arrive", params);


            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
                openActivity(context, bundle);


            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 打开assets下的音乐mp3文件
     */
    private void openAssetMusics(Context context,String notifactionId) throws IOException, JSONException {


            //播放 assets/a2.mp3 音乐文件
        JSONObject jsonObject=new JSONObject(notifactionId);
        String  mp=jsonObject.getString("msg_id");
        if(mp!=null){
            AssetFileDescriptor fd = context.getAssets().openFd(""+mp+".mp3");


            if(fd!=null){
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                mediaPlayer.prepare();
                mediaPlayer.start();
            }

        }



    }
    /**
     * 跳到对应的Activity进行处理
     *
     * @param context
     * @param bundle
     */
    private void openActivity(Context context, Bundle bundle) {
        //打开自定义的Activity
        //TODO receive the jpush message. zhangjiacheng
        /*
        //zhangjiacheng
        Map<String,Object> params=new HashMap<>();
        params.put("message","value");
        mWXSDKInstance.fireGlobalEventCallback("app_push_notification_arrive", params);

        String pkg = context.getPackageName();
        PackageManager manager = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.setPackage(pkg);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        String activityName = manager.queryIntentActivities(mainIntent, 0).get(
                0).activityInfo.name;
        Intent intentGo = getIntent(pkg, activityName);
        context.startActivity(intentGo);*/
    }

    /**
     * 获得启动的Intent
     *
     * @param packageName
     * @param activityName
     * @return
     */
    public static Intent getIntent(String packageName, String activityName) {
        ComponentName className = new ComponentName(packageName, activityName);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return intent;
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.e(TAG, message + extras);

    }
}