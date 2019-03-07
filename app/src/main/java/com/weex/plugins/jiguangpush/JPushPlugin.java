package com.weex.plugins.jiguangpush;

import android.app.Activity;

import com.taobao.weex.common.WXModule;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;

import java.util.HashMap;

/**
 * Created by zhangjiacheng on 2018/5/29.
 */

public class JPushPlugin extends WXModule {

    //JSCallback mCallBack;

    @JSMethod
    public void setJiguangPush(HashMap<String, String> param, final JSCallback jsCallback) {

        String aliasPre = "";//param.get("eviroment");//StrUtil.nullToStr(com.eBest.mobile.android.db.DBManager2.getSystemConfig("INSP_JPUSH"));
        if(param.containsKey("eviroment")) { aliasPre = param.get("eviroment"); }
        String userid = "";
        if(param.containsKey("userid")) { userid = param.get("userid"); }
        String alias = aliasPre +  userid;
        System.out.println("JPush=alias=" + alias);
        HashMap<String, String> o = new HashMap<String, String>();
        try {
            TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
            tagAliasBean.action = TagAliasOperatorHelper.ACTION_SET;
            tagAliasBean.alias = alias;
            tagAliasBean.isAliasAction = true;
            TagAliasOperatorHelper.getInstance().handleAction( ( (Activity) mWXSDKInstance.getContext()), TagAliasOperatorHelper.sequence, tagAliasBean);
            o.put("complete" , "True");
            jsCallback.invoke(o);
        } catch (Exception e) {
            e.printStackTrace();
            o.put("complete" , "False");
            jsCallback.invoke(o);
        }

    }

    //private void setJPushAlias(HashMap<String, String> param) {
    //}
}

