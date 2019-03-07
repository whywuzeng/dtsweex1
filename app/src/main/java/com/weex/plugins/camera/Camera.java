package com.weex.plugins.camera;

import android.app.Activity;
import android.content.Intent;
import com.alibaba.weex.plugin.annotation.WeexModule;
import com.weex.plugins.permission.ModuleResultListener;
import com.weex.plugins.permission.PermissionChecker;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by zhangjiacheng on 2018/5/27.
 */

@WeexModule(
        name = "ytcamera"
)
public class Camera extends WXModule {
    JSCallback mImageCallBack;
    JSCallback mVideoCallBack;
    String lang = Locale.getDefault().getLanguage();
    Boolean isChinese;

    public Camera() {
        this.isChinese = Boolean.valueOf(this.lang.startsWith("zh"));
    }

    @JSMethod
    public void captureImage(HashMap<String, Object> param, final JSCallback jsCallback) {
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
        } else {
            this.realCaptureImage(param, jsCallback);
        }

    }

    public void realCaptureImage(HashMap<String, Object> param, final JSCallback jsCallback) {
        this.mImageCallBack = jsCallback;
        CameraModule.getInstance(this.mWXSDKInstance.getContext()).captureImage((Activity)this.mWXSDKInstance.getContext(), new com.weex.plugins.camera.ModuleResultListener() {
            public void onResult(Object o) {
                jsCallback.invoke(o);
            }
        });
    }

    @JSMethod
    public void captureVideo(HashMap<String, Object> param, final JSCallback jsCallback) {
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
        } else {
            this.realCaptureVideo(param, jsCallback);
        }

    }

    public void realCaptureVideo(HashMap<String, Object> param, final JSCallback jsCallback) {
        this.mVideoCallBack = jsCallback;
        CameraModule.getInstance(this.mWXSDKInstance.getContext()).captureVideo((Activity)this.mWXSDKInstance.getContext(), new com.weex.plugins.camera.ModuleResultListener() {
            public void onResult(Object o) {
                jsCallback.invoke(o);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Object o = CameraModule.getInstance(this.mWXSDKInstance.getContext()).onCaptureImgActivityResult(requestCode, resultCode, data);
        Object o1 = CameraModule.getInstance(this.mWXSDKInstance.getContext()).onCaptureVideoActivityResult(requestCode, resultCode, data);
        if(this.mImageCallBack != null) {
            this.mImageCallBack.invoke(o);
            this.mImageCallBack = null;
        }

        if(this.mVideoCallBack != null) {
            this.mVideoCallBack.invoke(o1);
            this.mVideoCallBack = null;
        }

    }
}

