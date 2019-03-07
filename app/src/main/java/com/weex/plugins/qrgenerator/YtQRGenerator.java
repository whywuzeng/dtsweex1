package com.weex.plugins.qrgenerator;

import android.app.Activity;
import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.weex.plugins.camera.Util;
import com.weex.plugins.permission.ModuleResultListener;
import com.weex.plugins.permission.PermissionChecker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by zhangjiacheng on 2018/6/14.
 */

public class YtQRGenerator extends WXModule {
    Boolean isChinese =true;
    String fileName="";
    HashMap<String, String> param;
    JSCallback jsCallback;
    @JSMethod
    public void generateQRCode(HashMap<String, String> param, final JSCallback jsCallback) {
        this.param=param;
        this.jsCallback=jsCallback;
        boolean b = PermissionChecker.lacksPermissions(this.mWXSDKInstance.getContext(), new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"});
        if(b) {
            HashMap<String, String> dialog = new HashMap();
            if(this.isChinese.booleanValue()) {
                dialog.put("title", "权限申请");
                dialog.put("message", "请允许应用使用相机");
            } else {
                dialog.put("title", "Permission Request");
                dialog.put("message", "Please allow the app to use the camera");
            }

            PermissionChecker.requestPermissions((Activity)this.mWXSDKInstance.getContext(), dialog, new ModuleResultListener() {
                public void onResult(Object o) {
                    if(((Boolean)o).booleanValue()) {
                        jsCallback.invoke(Util.getError("CAMERA_PERMISSION_DENIED", 120020));
                    }

                }
            }, 1504, new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"});

        }
        String codeValue = null;
        if(param.containsKey("codeValue")) { codeValue = param.get("codeValue"); }
        String width = null;
        if(param.containsKey("width")) { width = param.get("width"); }
        String height = null;
        if(param.containsKey("height")) { height = param.get("height"); }
        String isQRCode = null;
        if(param.containsKey("isQRCode")) { isQRCode = param.get("isQRCode"); }

        HashMap<String, Object> o = new HashMap<String, Object>();

        if(codeValue == null) {

            HashMap<String, Object> param1 = new HashMap<>();
            param.put("msg", "参数错误");
            o.put("error", param1);
            jsCallback.invoke(o);
        }
        else {
            boolean toQRCode = (isQRCode == null) ? false : isQRCode.equals("true");
            int w = 260;
            int h = toQRCode ? 260 : 114;
            if(width != null) { w = Integer.parseInt(width); }
            if(height != null) { h = Integer.parseInt(height); }
            Bitmap bmp = (toQRCode) ? ZXingUtils.createQRImage(codeValue, w, h) : ZXingUtils.encodeAsBitmap(codeValue, BarcodeFormat.CODE_128,w,h);

            fileName = java.util.UUID.randomUUID().toString() +  ".jpg";//"nat_img_" + new Date().getTime() + ".jpg";
            //Intent intent = new Intent();
            //intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            //intent.addCategory(Intent.CATEGORY_DEFAULT);
            File finalImageFile = null;

                try {
                    finalImageFile = Util.getFile(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                    //listener.onResult(Util.getError(Constant.CAMERA_INTERNAL_ERROR, Constant.CAMERA_INTERNAL_ERROR_CODE));

            }



            //Uri uri = Uri.fromFile(finalImageFile);

            if(finalImageFile == null) {
                HashMap<String, Object> param1 = new HashMap<>();
                param.put("msg", (toQRCode?"生成二维码失败":"生成条形码失败"));
                o.put("error", param1);
                jsCallback.invoke(o);
            }
            else {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(finalImageFile);
                    boolean success = bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                    // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                    if(success){
                        o.put("path", "file://"+finalImageFile.getAbsolutePath());
                        jsCallback.invoke(o);
                    }
                    else {
                        HashMap<String, Object> param1 = new HashMap<>();
                        param.put("msg", (toQRCode?"生成二维码失败":"生成条形码失败"));
                        o.put("error", param1);
                        jsCallback.invoke(o);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    HashMap<String, Object> param1 = new HashMap<>();
                    param.put("msg", (toQRCode?"生成二维码失败":"生成条形码失败"));
                    o.put("error", param1);
                    jsCallback.invoke(o);
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    public void setFile(){


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //可在此继续其他操作。
        generateQRCode(param,jsCallback);
    }
}
