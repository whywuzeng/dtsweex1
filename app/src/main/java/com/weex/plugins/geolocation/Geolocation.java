package com.weex.plugins.geolocation;

import android.app.Activity;
import android.text.TextUtils;

import com.alibaba.weex.plugin.annotation.WeexModule;
import com.taobao.weex.WXSDKInstance;
import com.weex.plugins.permission.ModuleResultListener;
import com.weex.plugins.permission.PermissionChecker;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import java.util.HashMap;
import java.util.Locale;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by zhangjiacheng on 2018/5/27.
 */

@WeexModule(
        name = "ytgeolocation"
)
public class Geolocation extends WXModule {
    JSCallback mGetCallback;
    JSCallback mWatchCallback;
    HashMap<String, Object> mWatchParam;
    public static final int GET_REQUEST_CODE = 103;
    public static final int WATCH_REQUEST_CODE = 104;
    String lang = Locale.getDefault().getLanguage();
    Boolean isChinese;

    public Geolocation() {
        this.isChinese = Boolean.valueOf(this.lang.startsWith("zh"));
    }

    @JSMethod
    public void get(final JSCallback jsCallback) {
        boolean b = PermissionChecker.lacksPermissions(this.mWXSDKInstance.getContext(), new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"});
        if(b) {
            this.mGetCallback = jsCallback;
            HashMap<String, String> dialog = new HashMap();
            if(this.isChinese.booleanValue()) {
                dialog.put("title", "权限申请");
                dialog.put("message", "请允许应用获取地理位置");
            } else {
                dialog.put("title", "Permission Request");
                dialog.put("message", "Please allow the app to get your location");
            }

            PermissionChecker.requestPermissions((Activity)this.mWXSDKInstance.getContext(), dialog, new ModuleResultListener() {
                public void onResult(Object o) {
                    if(o != null && o.toString().equals("true")) {
                        jsCallback.invoke(Util.getError("LOCATION_PERMISSION_DENIED", 160020));
                    }

                }
            }, 103, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"});
        } else {

            /*/
            //add by zhangjiacheng
            //WXSDKInstance mWXSDKInstance = new WXSDKInstance(context);
            HashMap<String,Object> params=new HashMap<>();
            String message =  JPushInterface.EXTRA_MESSAGE;
            message =  TextUtils.isEmpty(message) ? "" : message;
            params.put("message",message);
            mWXSDKInstance.fireGlobalEventCallback("app_push_notification_arrive", params);
            /*/

            GeolocationModule.getInstance(this.mWXSDKInstance.getContext()).get(new com.weex.plugins.geolocation.ModuleResultListener() {
                public void onResult(Object o) {
                    jsCallback.invoke(o);
                }
            });
        }

    }

    @JSMethod
    public void watch(HashMap<String, Object> param, final JSCallback jsCallback) {
        boolean b = PermissionChecker.lacksPermissions(this.mWXSDKInstance.getContext(), new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"});
        if(b) {
            this.mWatchCallback = jsCallback;
            this.mWatchParam = param;
            HashMap<String, String> dialog = new HashMap();
            if(this.isChinese.booleanValue()) {
                dialog.put("title", "权限申请");
                dialog.put("message", "请允许应用获取地理位置");
            } else {
                dialog.put("title", "Permission Request");
                dialog.put("message", "Please allow the app to get your location");
            }

            PermissionChecker.requestPermissions((Activity)this.mWXSDKInstance.getContext(), dialog, new ModuleResultListener() {
                public void onResult(Object o) {
                    if(o != null && o.toString().equals("true")) {
                        jsCallback.invoke(Util.getError("LOCATION_PERMISSION_DENIED", 160020));
                    }

                }
            }, 104, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"});
        } else {
            GeolocationModule.getInstance(this.mWXSDKInstance.getContext()).watch(param, new com.weex.plugins.geolocation.ModuleResultListener() {
                public void onResult(Object o) {
                    jsCallback.invokeAndKeepAlive(o);
                }
            });
        }

    }

    @JSMethod
    public void clearWatch(final JSCallback jsCallback) {
        GeolocationModule.getInstance(this.mWXSDKInstance.getContext()).clearWatch(new com.weex.plugins.geolocation.ModuleResultListener() {
            public void onResult(Object o) {
                jsCallback.invoke(o);
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 103) {
            if(PermissionChecker.hasAllPermissionsGranted(grantResults)) {
                GeolocationModule.getInstance(this.mWXSDKInstance.getContext()).get(new com.weex.plugins.geolocation.ModuleResultListener() {
                    public void onResult(Object o) {
                        Geolocation.this.mGetCallback.invoke(o);
                    }
                });
            } else if(this.mGetCallback != null) {
                this.mGetCallback.invoke(Util.getError("LOCATION_PERMISSION_DENIED", 160020));
            }
        }

        if(requestCode == 104) {
            if(PermissionChecker.hasAllPermissionsGranted(grantResults)) {
                GeolocationModule.getInstance(this.mWXSDKInstance.getContext()).watch(this.mWatchParam, new com.weex.plugins.geolocation.ModuleResultListener() {
                    public void onResult(Object o) {
                        Geolocation.this.mWatchCallback.invokeAndKeepAlive(o);
                    }
                });
            } else if(this.mWatchCallback != null) {
                this.mWatchCallback.invoke(Util.getError("LOCATION_PERMISSION_DENIED", 160020));
            }
        }

    }
}

