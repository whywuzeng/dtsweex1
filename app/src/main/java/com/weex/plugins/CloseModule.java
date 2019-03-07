package com.weex.plugins;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.taobao.weex.devtools.common.LogUtil;
import com.weex.utdtsweex.util.QuitterActivity;

import java.util.HashMap;

/**
 * Created by mac on 2018/6/28.
 */

public class CloseModule extends WXModule {
    @JSMethod(uiThread = false)
    public void closeApp(HashMap<String, Object> param, final JSCallback jsCallback) {
        boolean exists =false;
        LogUtil.e("触发关闭效果");
        try {
            QuitterActivity.finishActivity();
            exists=true;
        }catch (Exception e){
            exists=false;
        }

        HashMap<String, Object> o = new HashMap<String, Object>();
        o.put("exists", (exists ? "true" : "false"));
        jsCallback.invoke(o);
    }
}
