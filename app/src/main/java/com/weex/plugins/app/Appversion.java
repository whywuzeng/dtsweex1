package com.weex.plugins.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mac on 2018/9/5.
 */

public class Appversion extends WXModule {
    Context mcontext;

    @JSMethod
    public void getAPPJSVersions(HashMap<String, String> param, final JSCallback jsCallback) {

        mcontext = mWXSDKInstance.getContext();

        HashMap<String, Object> o = new HashMap<String, Object>();
        SharedPreferences sp = mWXSDKInstance.getContext().getSharedPreferences("User", MODE_PRIVATE);


        o.put("appVersion", "Android("+packageName(mcontext)+")");
        String isVersion = sp.getString("isVersion", "");
        if (isVersion.equals("true")) {

            o.put("jsVersion", sp.getString("isLoaderValueText", ""));
        } else
        o.put("jsVersion", sp.getString("isLoaderValue", ""));
        o.put("jsLastUpdateTime", "成功,"+sp.getString("jsLastUpdateTime", ""));
        jsCallback.invoke(o);

    }

    public String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }
}
