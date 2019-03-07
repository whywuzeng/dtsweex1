package com.weex.plugins.qrcodesann;

import com.taobao.weex.common.WXModule;
import com.taobao.weex.annotation.JSMethod;
import com.unisoft.zjc.utdts.WXPageActivity;
import com.taobao.weex.bridge.JSCallback;
import com.google.zxing.integration.android.IntentIntegrator;

/**
 * Created by zhangjiacheng on 2018/5/27.
 * https://www.aliyun.com/jiaocheng/3217.html
 */

public class qrcodesann extends WXModule {
    @JSMethod
    public void getQRcode(JSCallback callback) {
        WXPageActivity act=(WXPageActivity)mWXSDKInstance.getContext();
        act.setOnScanFinishCallback(callback);
        IntentIntegrator integrator = new IntentIntegrator(act);
        integrator.initiateScan();
    }
}
