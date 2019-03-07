package com.weex.plugins.navigator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Created by zhangjiacheng on 2018/5/31.
 */

public class YtNavigator extends WXModule {

    @JSMethod
    public void navigate(HashMap<String, String> param, final JSCallback jsCallback) {

        String latitude = null;
        if(param.containsKey("latitude")) { latitude = param.get("latitude"); }
        String longitude = null;
        if(param.containsKey("longitude")) { longitude = param.get("longitude"); }
        String originName = "";
        if(param.containsKey("originName")) { originName = param.get("originName"); }
        String destinationName = "";
        if(param.containsKey("destinationName")) { destinationName = param.get("destinationName"); }

        HashMap<String, Object> o = new HashMap<String, Object>();

        if(latitude == null || longitude == null ) {

            HashMap<String, Object> param1 = new HashMap<>();
            param.put("msg", "导航参数错误");
            o.put("error", param1);
            jsCallback.invoke(o);
        }
        else {

            //--高德
            if (MDMUtil.appIsInstalled( ((Activity)mWXSDKInstance.getContext()),"com.autonavi.minimap")) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                String t = "0";

                Uri uri = Uri.parse("amapuri://route/plan/?sid=BGVIS1&did=&dlat=" + latitude + "&dlon=" + longitude + "&dname=&dev=0&t="+t);
                intent.setData(uri);
                ((Activity)mWXSDKInstance.getContext()).startActivity(intent);

                o.put("complte", "True");
                jsCallback.invoke(o);
            }
            //--百度
            else if (MDMUtil.appIsInstalled( ((Activity)mWXSDKInstance.getContext()),"com.baidu.BaiduMap")) {//传入指定应用包名
                try {
                    String mode = "driving";
                    String sy="5";
                    Intent intent = Intent.getIntent("intent://map/direction?" +
                            "destination=latlng:" + latitude + "," + longitude+ "|name:我的目的地" +
                            "&coord_type=gcj02" +
                            "&mode="+mode+"&" +
                            "&src=appname#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ((Activity)mWXSDKInstance.getContext()).startActivity(intent);
                    o.put("complte", "True");
                    jsCallback.invoke(o);
                } catch (URISyntaxException e) {
                    Log.e("intent", e.getMessage());
                    HashMap<String, Object> param1 = new HashMap<>();
                    param.put("msg", "百度导航失败");
                    o.put("error", param1);
                    jsCallback.invoke(o);
                }
            }

            else {
                HashMap<String, Object> param1 = new HashMap<>();
                param.put("msg", "您手机未安装导航APP");
                o.put("error", param1);
                jsCallback.invoke(o);

            }

        }

    }

    /*
    高德相关
            double[] gd_lat_lon ;
            if(!RoutingXModel.isGpslatlon){
                gd_lat_lon=  bdToGaoDe(xModel.poc_lat,xModel.poc_lon);
            }else{
                gd_lat_lon= new double[2];
                gd_lat_lon[0]=xModel.poc_lon;
                gd_lat_lon[1]=xModel.poc_lat;
            }*/
            /*
                        http://lbs.amap.com/api/amap-mobile/guide/android/navigation
                        http://lbs.amap.com/api/amap-mobile/guide/android/route
                        将功能Scheme以URI的方式传入data
                        dev 是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
                       //style 导航方式(0 速度快; 1 费用少; 2 路程短; 3 不走高速；4 躲避拥堵；5 不走高速且避免收费；6 不走高速且躲避拥堵；7 躲避收费和拥堵；8 不走高速躲避收费和拥堵)
                        t = 0（驾车）= 1（公交）= 2（步行）= 3（骑行）= 4（火车）= 5（长途客车） （骑行仅在V788以上版本支持）*/

            /*
                        Uri uri = Uri.parse("androidamap://navi?sourceApplication=appname&poiname=fangheng&lat=" +  gd_lat_lon[1] + "&lon=" +  gd_lat_lon[0] + "&dev=0&style="+style);
                        &slat="+gd_lat_lon[1]+"&slon="+gd_lat_lon[0]+"&sname=*/


            /*
            百度相关
            //                            公交检索策略，只针对mode字段填写transit情况下有效，值为数字。
//                            0：推荐路线
//                            2：少换乘
//                            3：少步行
//                            4：不坐地铁
//                            5：时间短
//                            6：地铁优先

                    if ("1".equals(trans_type)) {// 1步行
                        mode = "walking";
                    } else if ("2".equals(trans_type)) {// 2骑行
                        mode = "riding";
                    } else if ("3".equals(trans_type)) {// 3驾车
                        mode = "driving";
                    }

            * */

    /**
     * 百度转高德
     * @param bd_lat
     * @param bd_lon
     * @return
     */
    private double[] bdToGaoDe(double bd_lat, double bd_lon) {
        double[] gd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        gd_lat_lon[0] = z * Math.cos(theta);
        gd_lat_lon[1] = z * Math.sin(theta);
        return gd_lat_lon;
    }
    /**
     * 高德转百度

     * @return
     */
    private double[] gaoDeToBaidu(double gd_lon, double gd_lat) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
        bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
        return bd_lat_lon;
    }
}
