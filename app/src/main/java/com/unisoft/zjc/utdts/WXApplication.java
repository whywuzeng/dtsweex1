package com.unisoft.zjc.utdts;

import android.app.Application;
import android.app.Service;
import android.content.SharedPreferences;
import android.os.Vibrator;

import com.alibaba.weex.plugin.loader.WeexPluginContainer;
import com.baidu.mapapi.SDKInitializer;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;
import com.weex.plugins.CloseModule;
import com.weex.plugins.app.Appversion;
import com.weex.plugins.app.CleanMessageUtil;
import com.weex.plugins.appswitch.VoicePromptSwitch;
import com.weex.plugins.baiduAMP.RealTracking;
import com.weex.plugins.baiduAMP.TrackView;
import com.weex.plugins.filemanager.YtFileManager;
import com.weex.plugins.fileremover.YtFileRemover;
import com.weex.plugins.geolocation.LocationService;
import com.weex.plugins.heatrenewal.YtChangeServer;
import com.weex.plugins.imageuploader.YtImageUploader;
import com.weex.plugins.jiguangpush.JPushPlugin;
import com.weex.plugins.navigator.YtNavigator;
import com.weex.plugins.openurl.YtOpenurl;
import com.weex.plugins.qrcodesann.qrcodesann;
import com.weex.plugins.qrgenerator.YtQRGenerator;
import com.weex.plugins.test.MyModule;
import com.weex.utdtsweex.extend.ImageAdapter;
import com.weex.utdtsweex.extend.WXEventModule;
import com.weex.utdtsweex.util.AppConfig;

import cn.jpush.android.api.JPushInterface;

public class WXApplication extends Application {
  public LocationService locationService;
  public Vibrator mVibrator;
  @Override
  public void onCreate() {
    super.onCreate();
    /***
     * 初始化定位sdk，建议在Application中创建
     */
    locationService = new LocationService(getApplicationContext());
    mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
    SDKInitializer.initialize(getApplicationContext());
    //setup jpush, by zhangjiacheng
    JPushInterface.setDebugMode(true);
    JPushInterface.init(this);
    SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
    SharedPreferences.Editor edit = sp.edit();
    //通过editor对象写入数据NN
    edit.putString("isTestUrl", "N");//是否使用热更新JS
    edit.commit();
    WXSDKEngine.addCustomOptions("appName", "WXSample");
    WXSDKEngine.addCustomOptions("appGroup", "WXApp");
    WXSDKEngine.initialize(this,
        new InitConfig.Builder().setImgAdapter(new ImageAdapter()).build()
    );
    try {

      WXSDKEngine.registerModule("event", WXEventModule.class);
      WXSDKEngine.registerModule("yclose", CloseModule.class);
      //by zhangjiacheng
      WXSDKEngine.registerModule("ytqrdecoder",qrcodesann.class);
      WXSDKEngine.registerModule("ytjiguang",JPushPlugin.class);
      WXSDKEngine.registerModule("ytNavigator",YtNavigator.class);
      WXSDKEngine.registerModule("ytOpenurl", YtOpenurl.class);
      WXSDKEngine.registerModule("ytFileManager", YtFileManager.class);
      WXSDKEngine.registerModule("ytImageUploader", YtImageUploader.class);
      WXSDKEngine.registerModule("ytQRGenerator", YtQRGenerator.class);
      WXSDKEngine.registerModule("ytFileRemover", YtFileRemover.class);
      WXSDKEngine.registerModule("ytChangeServer", YtChangeServer.class);
      WXSDKEngine.registerModule("ytGetAppJSVersions", Appversion.class);
      WXSDKEngine.registerModule("ytRemoveCacheImages", CleanMessageUtil.class);

      WXSDKEngine.registerModule("ytSwitch",VoicePromptSwitch.class);
      WXSDKEngine.registerComponent("realTracking-view", RealTracking.class);
      WXSDKEngine.registerComponent("tracetrackView-view", TrackView.class);
      WXSDKEngine.registerModule("MyModule",MyModule.class);

    } catch (WXException e) {
      e.printStackTrace();
    }
    AppConfig.init(this);
    WeexPluginContainer.loadAll(this);
  }





}
