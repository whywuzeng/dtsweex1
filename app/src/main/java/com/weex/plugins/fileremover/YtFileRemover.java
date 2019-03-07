package com.weex.plugins.fileremover;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.io.File;
import java.util.HashMap;


/**
 * Created by zhangjiacheng on 2018/6/14.
 */

public class YtFileRemover extends WXModule {

    @JSMethod
    public  void  rm(HashMap<String, String> param, final JSCallback jsCallback) {

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

        if(exists) {
            boolean deleteSuccess = false;//deleteFile(url);

            File file = new File(url);
            // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    System.out.println("删除单个文件" + url + "成功！");
                    deleteSuccess = true;
                } else {
                    System.out.println("删除单个文件" + url + "失败！");
                    deleteSuccess = false;
                }
            } else {
                System.out.println("删除单个文件失败：" + url + "不存在！");
                deleteSuccess = false;
            }

            if(deleteSuccess) { exists = false; }
        }

        HashMap<String, Object> o = new HashMap<String, Object>();
        o.put("removed", (exists ? "false" :  "true"));
        jsCallback.invoke(o);
    }
}
