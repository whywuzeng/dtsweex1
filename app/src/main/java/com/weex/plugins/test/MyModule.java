package com.weex.plugins.test;

import android.widget.Toast;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.WXModule;

/**
 * Created by Administrator on 2019/3/7.
 * <p>
 * by author wz
 * <p>
 * com.weex.plugins.test
 */

public class MyModule extends WXModule {
    //run ui thread
    @JSMethod(uiThread = true)
    public void printLog(String msg) {
        Toast.makeText(this.mWXSDKInstance.getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    //run JS thread
    @JSMethod (uiThread = false)
    public void fireEventSyncCall(){
        //implement your module logic here
    }
}
