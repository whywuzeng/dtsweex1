package com.weex.utdtsweex.util;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by budao on 2016/10/12.
 */
public class AppConfig {
  private static final String TAG = "AppConfig";
  private static AppPreferences sPreferences = new AppPreferences();
  private static Context mCotext;
  public static void init(Context context) {
    mCotext=context;
    loadAppSetting(context);
  }

  public static String getLaunchUrl(String url) {
    SharedPreferences sp = mCotext.getSharedPreferences("User", mCotext.MODE_PRIVATE);
    String isTestUrl = sp.getString("isTestUrl", "");

    if(isTestUrl.equals("N"))
    return sPreferences.getString("launch_url", "http://192.168.1.136:8081/dist/index.js");
    else
      return url +"index.js";
  }

  public static Boolean isLaunchLocally() {
    return sPreferences.getBoolean("launch_locally", true
    );
  }
  private static void loadAppSetting(Context context) {
    AppConfigXmlParser parser = new AppConfigXmlParser();
    parser.parse(context);
    sPreferences = parser.getPreferences();
  }
}
