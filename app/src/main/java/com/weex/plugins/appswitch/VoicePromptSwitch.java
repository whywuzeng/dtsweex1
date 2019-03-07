package com.weex.plugins.appswitch;

import android.content.SharedPreferences;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.util.HashMap;

/**
 * Created by mac on 2018/12/22.
 */

public class VoicePromptSwitch extends WXModule {
    @JSMethod
   public void setVoicePromptSwitch(HashMap<String, String> param, final JSCallback jsCallback){
        SharedPreferences sp = mWXSDKInstance.getContext().getSharedPreferences("User", mWXSDKInstance.getContext().MODE_PRIVATE);
        HashMap<String, Object> o = new HashMap<String, Object>();
        if(param.containsKey("switchValue")) {
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("switchValue",  param.get("switchValue")+"");
            edit.commit();
        }
        o.put("complte", "True");
        jsCallback.invoke(o);
    }
}
