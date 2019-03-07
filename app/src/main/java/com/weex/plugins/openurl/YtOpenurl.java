package com.weex.plugins.openurl;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.util.HashMap;

/**
 * Created by zhangjiacheng on 2018/6/1.
 */

public class YtOpenurl extends WXModule {

    @JSMethod
    public void openurl(HashMap<String, String> param, final JSCallback jsCallback) {

        String url = null;
        if(param.containsKey("url")) { url = param.get("url"); }

        HashMap<String, Object> o = new HashMap<String, Object>();

        if(url == null) {

            HashMap<String, Object> param1 = new HashMap<>();
            param.put("msg", "参数错误");
            o.put("error", param1);
            jsCallback.invoke(o);
        }
        else {
            Intent intent = new Intent();
            intent.setData(Uri.parse(url));//Url 就是你要打开的网址
            intent.setAction(Intent.ACTION_VIEW);
            ((Activity)mWXSDKInstance.getContext()).startActivity(intent); //启动浏览器
            o.put("complte", "True");
            jsCallback.invoke(o);
        }
    }
}
