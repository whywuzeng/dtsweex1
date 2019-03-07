package com.weex.plugins.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.io.File;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mac on 2018/9/5.
 */

public class CleanMessageUtil extends WXModule {
    @JSMethod
    public void removeCacheImages(HashMap<String, String> param, final JSCallback jsCallback) {


        HashMap<String, Object> o = new HashMap<String, Object>();
        try {
            clearAllCache(mWXSDKInstance.getContext());
            o.put("info", "True");
            jsCallback.invoke(o);
        }catch (Exception e){
            HashMap<String, Object> param1 = new HashMap<>();
            param.put("msg", "缓存删除失败");
            o.put("error", param1);
            jsCallback.invoke(o);
        }

    }
    /**
     * @param context
     *            删除缓存
     */
    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            int size = 0;
            if (children != null) {
                size = children.length;
                for (int i = 0; i < size; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }

        }
        if (dir == null) {
            return true;
        } else {

            return dir.delete();
        }
    }
}
