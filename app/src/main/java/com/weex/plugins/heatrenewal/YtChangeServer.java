package com.weex.plugins.heatrenewal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.unisoft.zjc.utdts.SplashActivity;
import com.weex.utdtsweex.util.QuitterActivity;

import java.util.HashMap;

/**
 * Created by mac on 2018/8/10.
 */

public class YtChangeServer extends WXModule {

    @JSMethod
    public void changedServer(HashMap<String, String> param, final JSCallback jsCallback) {

        String url = null;
        if(param.containsKey("toTestServer")) { url = param.get("toTestServer"); }

        HashMap<String, Object> o = new HashMap<String, Object>();
        SharedPreferences sp = mWXSDKInstance.getContext().getSharedPreferences("User", mWXSDKInstance.getContext().MODE_PRIVATE);
        if(url == null) {

            HashMap<String, Object> param1 = new HashMap<>();
            param.put("msg", "参数错误");
            o.put("error", param1);
            jsCallback.invoke(o);
        }
        else {
           if(url.equals("true")){
               //测试
               //获取到edit对象
               SharedPreferences.Editor edit = sp.edit();
               //通过editor对象写入数据
               edit.putString("isVersion", "true");
               //提交数据存入到xml文件中
               edit.commit();
               loadView();

           }else {

               //正式
               SharedPreferences.Editor edit = sp.edit();
               //通过editor对象写入数据
               edit.putString("isVersion", "false");
               //提交数据存入到xml文件中
               edit.commit();

               loadView();
           }
        }
    }

    private void loadView() {
        Intent intent=new Intent(mWXSDKInstance.getContext(), SplashActivity.class);
        mWXSDKInstance.getContext().startActivity(intent);
        Activity activity = (Activity) mWXSDKInstance.getContext();
        activity.finish();

    }
}
