package com.weex.plugins.geolocation;


import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.unisoft.zjc.utdts.WXApplication;

import java.util.HashMap;
import java.util.List;

import static com.taobao.weex.WXEnvironment.getApplication;


/**
 * Created by zhangjiacheng on 2018/5/27.
 */

public class GeolocationModule {
    private LocationService locationService;
    private LocationManager mWatchLocationManager;
    int maximumAge = 0;
    int timeout = 10000;
    String model = "highAccuracy";
    MyCountDownTimer countDownTimer;
    LocationListener mLocationListener;
    private Context mContext;
    private static volatile GeolocationModule instance = null;

    private GeolocationModule(Context context){
        mContext = context;
    }

    public static GeolocationModule getInstance(Context context) {
        if (instance == null) {
            synchronized (GeolocationModule.class) {
                if (instance == null) {
                    instance = new GeolocationModule(context);
                }
            }
        }

        return instance;
    }
     ModuleResultListener mylistener =null;

    public void get(final ModuleResultListener listener) {
        mylistener=listener;
        locationService = ((WXApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);


        locationService.setLocationOption(locationService.getDefaultLocationClientOption());



        locationService.start();// 定位SDK
        if (listener != null) return;
        if (listener == null) return;
        if (mContext == null) {
            listener.onResult(Constant.ERROR_NULL_CONTEXT);
            return;
        }

        final LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        String locationProvider;
//        if (providers.contains(LocationManager.PASSIVE_PROVIDER)) {
//            locationProvider = LocationManager.PASSIVE_PROVIDER;
//        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
//            locationProvider = LocationManager.GPS_PROVIDER;
//        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
//            locationProvider = LocationManager.NETWORK_PROVIDER;
//        } else {
//            listener.onResult(Util.getError(Constant.LOCATION_UNAVAILABLE, Constant.LOCATION_UNAVAILABLE_CODE));
//            return;
//        }

//        Location location = locationManager.getLastKnownLocation(locationProvider);
//        if (location != null) {
//            listener.onResult(getLocationInfo(location));
//            return;
//        } else {
//            locationManager.requestLocationUpdates(locationProvider, 0, 0, new LocationListener() {
//                @Override
//                public void onLocationChanged(Location location) {
//                    listener.onResult(getLocationInfo(location));
//                    locationManager.removeUpdates(this);
//                    return;
//                }
//
//                @Override
//                public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                }
//
//                @Override
//                public void onProviderEnabled(String provider) {
//
//                }
//
//                @Override
//                public void onProviderDisabled(String provider) {
//
//                }
//            });
//        }
    }

    public void watch(HashMap<String, Object> options, final ModuleResultListener listener) {
        if (listener == null) return;
        if (mContext == null) {
            listener.onResult(Constant.ERROR_NULL_CONTEXT);
            return;
        }

        if (mWatchLocationManager != null) {
            listener.onResult(Util.getError(Constant.LOCATION_SERVICE_BUSY, Constant.LOCATION_SERVICE_BUSY_CODE));
            return;
        }

        try {
            maximumAge = options.containsKey("maximumAge") ? (int) options.get("maximumAge") : maximumAge;
            timeout = options.containsKey("timeout") ? (int) options.get("timeout") : timeout;
            model = options.containsKey("model") ? (String) options.get("model") : model;
        } catch (ClassCastException e) {
            e.printStackTrace();
            listener.onResult(Util.getError(Constant.WATCH_LOCATION_INVALID_ARGUMENT, Constant.WATCH_LOCATION_INVALID_ARGUMENT_CODE));
        }

        mWatchLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria crite = new Criteria();
        crite.setAccuracy(model.equals("highAccuracy") ? Criteria.ACCURACY_FINE:Criteria.ACCURACY_COARSE); //精度
        crite.setPowerRequirement(Criteria.POWER_LOW); //功耗类型选择
        String provider = mWatchLocationManager.getBestProvider(crite, true);

        if (provider != null) {
            countDownTimer = new MyCountDownTimer(timeout, timeout, listener);
            countDownTimer.start();
            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
//                    listener.onResult(getLocationInfo(location));
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer.start();
                    }

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
            mWatchLocationManager.requestLocationUpdates(provider, maximumAge, 0, mLocationListener);
        } else {
            listener.onResult(Util.getError(Constant.LOCATION_UNAVAILABLE, Constant.LOCATION_UNAVAILABLE_CODE));
            return;
        }

    }

    public void clearWatch(ModuleResultListener listener) {
        if (listener == null)return;
        if (mWatchLocationManager == null) {
            listener.onResult(Util.getError(Constant.LOCATION_SERVICE_BUSY, Constant.LOCATION_SERVICE_BUSY_CODE));
            return;
        }
        if (mLocationListener != null) {
            mWatchLocationManager.removeUpdates(mLocationListener);
            mLocationListener = null;
            mWatchLocationManager = null;
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        listener.onResult(null);
    }

    private HashMap<String, Object> getLocationInfo(BDLocation location) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("latitude", location.getLatitude());
        result.put("longitude", location.getLongitude());
        result.put("speed", location.getSpeed());
//        result.put("accuracy", location.getAccuracy());
        return result;
    }

    class MyCountDownTimer extends CountDownTimer{
        ModuleResultListener mListener;
        public MyCountDownTimer(long millisInFuture, long countDownInterval, ModuleResultListener listener) {
            super(millisInFuture, countDownInterval);
            mListener = listener;
        }

        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            if (mLocationListener != null && mWatchLocationManager != null) {
                mWatchLocationManager.removeUpdates(mLocationListener);
            }
            mWatchLocationManager = null;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            mListener.onResult(Util.getError(Constant.LOCATION_TIMEOUT, Constant.LOCATION_TIMEOUT_CODE));
        }
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                locationService.stop();
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */

                mylistener.onResult(getLocationInfo(location));
//                sb.append(location.getTime());
//                sb.append("\nlocType : ");// 定位类型
//                sb.append(location.getLocType());
//                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
//                sb.append(location.getLocTypeDescription());
//                sb.append("\nlatitude : ");// 纬度
//                sb.append(location.getLatitude());
//                sb.append("\nlontitude : ");// 经度
//                sb.append(location.getLongitude());
//                sb.append("\nradius : ");// 半径
//                sb.append(location.getRadius());
//                sb.append("\nCountryCode : ");// 国家码
//                sb.append(location.getCountryCode());
//                sb.append("\nCountry : ");// 国家名称
//                sb.append(location.getCountry());
//                sb.append("\ncitycode : ");// 城市编码
//                sb.append(location.getCityCode());
//                sb.append("\ncity : ");// 城市
//                sb.append(location.getCity());
//                sb.append("\nDistrict : ");// 区
//                sb.append(location.getDistrict());
//                sb.append("\nStreet : ");// 街道
//                sb.append(location.getStreet());
//                sb.append("\naddr : ");// 地址信息
//                sb.append(location.getAddrStr());
//                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
//                sb.append(location.getUserIndoorState());
//                sb.append("\nDirection(not all devices have value): ");
//                sb.append(location.getDirection());// 方向
//                sb.append("\nlocationdescribe: ");
//                sb.append(location.getLocationDescribe());// 位置语义化信息
//                sb.append("\nPoi: ")Poi;// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
//                logMsg(sb.toString());
            }
        }

    };


}
