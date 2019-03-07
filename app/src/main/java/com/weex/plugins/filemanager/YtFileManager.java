package com.weex.plugins.filemanager;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.io.File;
import java.util.HashMap;

/**
 * Created by zhangjiacheng on 2018/6/1.
 */

public class YtFileManager extends WXModule {

    @JSMethod
        public void fileExists(HashMap<String, String> param, final JSCallback jsCallback) {

        String url = null;
        if(param.containsKey("filePath")) { url = param.get("filePath"); }
        boolean exists = true;
        if(url == null) {
            exists = false;
        }
        else {
            try
            {
                File f=new File(url);
                if(!f.exists())
                {
                    exists = false;
                }

            }
            catch (Exception e)
            {
                exists = false;
            }

            exists = true;
        }

        HashMap<String, Object> o = new HashMap<String, Object>();
        o.put("exists", (exists ? "true" : "false"));
        jsCallback.invoke(o);
    }


}
