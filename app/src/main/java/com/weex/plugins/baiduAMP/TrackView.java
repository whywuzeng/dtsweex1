package com.weex.plugins.baiduAMP;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.unisoft.zjc.utdts.R;
import com.weex.plugins.baiduAMP.algo.GeoHasher;
import com.weex.plugins.baiduAMP.projection.DrivingRouteOverlay;
import com.weex.plugins.baiduAMP.projection.OverlayManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mac on 2018/12/18.
 */

public class TrackView extends WXComponent<View>  implements BaiduMap.OnMapClickListener,OnGetGeoCoderResultListener,OnGetRoutePlanResultListener {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    WXSDKInstance mWXSDKInstance;
    MapStatus ms;
    GeoCoder mSearch = null;
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    // 搜索相关
    RoutePlanSearch mSearchs = null;    // 搜索模块，也可去掉地图模块独立使用
    int nowSearchType = -1; // 当前进行的检索，供判断浏览节点时结果使用。
    DrivingRouteResult nowResultdrive = null;
    boolean hasShownDialogue = false;
    RouteLine route = null;
    OverlayManager routeOverlay = null;
     private Polyline mPolyline;

    private List<Double> latitudeList = new ArrayList<Double>();
    private List<Double> longitudeList = new ArrayList<Double>();
    private double maxLatitude;
    private double minLatitude;
    private double maxLongitude;
    private double minLongitude;
    private double distance;
    private float level;
    private LinearLayout baidumap_infowindow;
    InfoWindow mInfoWindow;
    public TrackView(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
        super(instance, dom, parent);
        mWXSDKInstance = instance;
        planList=new ArrayList<>();
        baidumap_infowindow = (LinearLayout) LayoutInflater.from(mWXSDKInstance.getContext()).inflate(R.layout.layout_baidu_pop, null);
//
    }

    @Override
    protected View initComponentHostView(@NonNull Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout view = (LinearLayout) inflater
                .inflate(R.layout.activity_routeplan, null);
        initView(view);
//        initData(context, view);

        return view;
    }
    public void initView(View view){
        mMapView = (MapView) view.findViewById(R.id.map);
        mBaiduMap = mMapView.getMap();
// 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        // 初始化搜索模块，注册事件监听
        mSearchs = RoutePlanSearch.newInstance();
        mSearchs.setOnGetRoutePlanResultListener(this);
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng latLng_en = marker.getPosition();


                    if(latLng_en.longitude==Double.parseDouble(list.get(list.size()-1).getLon())){
                        createInfoWindow(baidumap_infowindow, list.get(list.size()-1));
                        mInfoWindow = new InfoWindow(baidumap_infowindow, new LatLng(Double.parseDouble(list.get(list.size()-1).getLat()), Double.parseDouble(list.get(list.size()-1).getLon())), -47);
                        //显示InfoWindow
                        mBaiduMap.showInfoWindow(mInfoWindow);

                       return true;
                     }
                for (int i = 0; i <list.size() ; i++) {
                    if(list.get(i).getWarpoint().equals("1")){
                        if(latLng_en.longitude==Double.parseDouble(list.get(i).getLon())){
                            createInfoWindowS(baidumap_infowindow, list.get(i));
                            mInfoWindow = new InfoWindow(baidumap_infowindow, new LatLng(Double.parseDouble(list.get(i).getLat()), Double.parseDouble(list.get(i).getLon())), -47);
                            //显示InfoWindow
                            mBaiduMap.showInfoWindow(mInfoWindow);
                        }

                    }
                }



                return true;
            }
        });
        mBaiduMap.setOnMapClickListener(listener);
    }
    //地图单击事件
    BaiduMap.OnMapClickListener listener = new BaiduMap.OnMapClickListener() {
        /**
         * 地图单击事件回调函数
         * @param point 点击的地理坐标
         */
        public void onMapClick(LatLng point){

            if (mInfoWindow != null) {
                mBaiduMap.hideInfoWindow();
                mMapView.postInvalidate();
            }

        }
        /**
         * 地图内 Poi 单击事件回调函数
         * @param poi 点击的 poi 信息
         */
        public boolean onMapPoiClick(MapPoi poi){
            return false;
        }
    };
    public void createInfoWindow(LinearLayout baidumap_infowindow, final MapBean mapBean){

                ((TextView) baidumap_infowindow.findViewById(R.id.pop_Truckcharsd_text)).setText("总里程数");
        ((TextView) baidumap_infowindow.findViewById(R.id.pop_Truckcharsd)).setText(mapBean.getSumMilesNum()+" KM");
        ((TextView) baidumap_infowindow.findViewById(R.id.pop_TruckNo)).setText(mapBean.getTruckNo());
        ((TextView) baidumap_infowindow.findViewById(R.id.pop_Truckdate)).setText(mapBean.getGpsTimeStamp());
      baidumap_infowindow.findViewById(R.id.pop_stop).setVisibility(View.GONE);
        baidumap_infowindow.findViewById(R.id.pop_star).setVisibility(View.GONE);


    }
    public void createInfoWindowS(LinearLayout baidumap_infowindow, final MapBean mapBean){

        ((TextView) baidumap_infowindow.findViewById(R.id.pop_Truckcharsd_text)).setText("类型");
        ((TextView) baidumap_infowindow.findViewById(R.id.pop_Truckcharsd)).setText(mapBean.getReasonname());
        ((TextView) baidumap_infowindow.findViewById(R.id.pop_TruckNo_text)).setText("说明");
        ((TextView) baidumap_infowindow.findViewById(R.id.pop_TruckNo)).setText(mapBean.getAbnormalMessage());
        ((TextView) baidumap_infowindow.findViewById(R.id.pop_Truckdate)).setText(mapBean.getAbnormalSTime());
        ((TextView) baidumap_infowindow.findViewById(R.id.pop_Truckcharstar_text)).setText("状态");
        ((TextView) baidumap_infowindow.findViewById(R.id.pop_Truckcharstar)).setText(mapBean.getIsRecover());
        ((TextView) baidumap_infowindow.findViewById(R.id.pop_Truckcharsop_text)).setText("停留");
        ((TextView) baidumap_infowindow.findViewById(R.id.pop_Truckcharstop)).setText(mapBean.getSmuite()+"分钟");
        baidumap_infowindow.findViewById(R.id.pop_stop).setVisibility(View.VISIBLE);
        baidumap_infowindow.findViewById(R.id.pop_star).setVisibility(View.VISIBLE);

    }
    private List<PlanNode> planList;
    /**
     * 计算两个Marker之间的距离
     */
    private void calculateDistance() {
        distance = GeoHasher.GetDistance(maxLatitude, maxLongitude, minLatitude, minLongitude);
        getLevel();
    }

    /**
     *根据距离判断地图级别
     */
    private void getLevel() {
        int zoom[] = {10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 1000, 2000, 25000, 50000, 100000, 200000, 500000, 1000000, 2000000};
        Log.i("info", "maxLatitude==" + maxLatitude + ";minLatitude==" + minLatitude + ";maxLongitude==" + maxLongitude + ";minLongitude==" + minLongitude);
        Log.i("info", "distance==" + distance);
        for (int i = 0; i < zoom.length; i++) {
            int zoomNow = zoom[i];
            if (zoomNow - distance * 1000 > 0) {
                level = 18 - i + 6;
                //设置地图显示级别为计算所得level
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(level).build()));
                break;
            }
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude,
                result.getLocation().longitude);

        Toast.makeText(mWXSDKInstance.getContext(), strInfo, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(mWXSDKInstance.getContext(), "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(mWXSDKInstance.getContext(), result.getSematicDescription() + " adcode: "  , Toast.LENGTH_LONG).show();
        PlanNode stNode = PlanNode.withCityNameAndPlaceName(result.getAddressDetail().city.replace("市",""), result.getSematicDescription());



        planList.add(stNode);
        if(planList.size()>1){
            setDrivingRoute(planList);
//            planList.clear();
        }
//        mBaiduMap.clear();
    }

    public void setDrivingRoute(List<PlanNode> list){
        for (int i = 0; i < list.size() ; i++) {
            mSearchs.drivingSearch((new DrivingRoutePlanOption())
                    .from(list.get(0)).to(list.get(1)));
            nowSearchType = 1;
        }
    }
    List<MapBean> list;
    MarkerOptions oob;
    @WXComponentProp(name = "list")
    public void setValue(String value) {
        String lon ="";
        String lat ="";
        List<LatLng> points = new ArrayList<>();
        list =new ArrayList<>();
        if(value!=null){
            try {
                JSONArray jsonArray=new JSONArray(value);
                for (int i = 0; i <jsonArray.length() ; i++) {
                    JSONObject jsonObject= (JSONObject) jsonArray.get(i);
                    MapBean data =new MapBean();
                    if(i==0){
                        ms = new MapStatus.Builder().target(new LatLng(Double.parseDouble(jsonObject.getString("TrackLat")), Double.parseDouble(jsonObject.getString("TrackLon")))).zoom(12).build();
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
                        LatLng st = new LatLng(Double.parseDouble(jsonObject.getString("TrackLat")), Double.parseDouble(jsonObject.getString("TrackLon")));



                        MarkerOptions ooA = new MarkerOptions().position(st).icon(ionc_st)
                                .zIndex(9).draggable(true);
                        mBaiduMap.addOverlay(ooA);

                    }
                    data.setWarpoint(jsonObject.getString("warpoint"));
                    data.setReasonname(jsonObject.getString("reasonname"));
                    data.setAbnormalMessage(jsonObject.getString("AbnormalMessage"));
                    data.setAbnormalSTime(jsonObject.getString("AbnormalSTime"));
                    data.setSmuite(jsonObject.getString("Smuite"));
                    data.setIsRecover(jsonObject.getString("IsRecover"));

                    if(jsonObject.getString("warpoint").equals("1")){
                        LatLng en = new LatLng(Double.parseDouble(jsonObject.getString("TrackLat")), Double.parseDouble(jsonObject.getString("TrackLon")));



                        MarkerOptions oob = new MarkerOptions().position(en).icon(ionc)
                                .zIndex(9).draggable(true);
                        mBaiduMap.addOverlay(oob);
                    }

                    if(i==jsonArray.length()-1){

                        LatLng en = new LatLng(Double.parseDouble(jsonObject.getString("TrackLat")), Double.parseDouble(jsonObject.getString("TrackLon")));



                        MarkerOptions oob = new MarkerOptions().position(en).icon(ionc_en)
                                .zIndex(9).draggable(true);
                        mBaiduMap.addOverlay(oob);

                    }


                    lon=jsonObject.getString("TrackLon");
                    lat=jsonObject.getString("TrackLat");
                    data.setLat(lat);
                    data.setLon(lon);
                    data.setTruckNo(jsonObject.getString("TruckNo"));
                    data.setGpsTimeStamp(jsonObject.getString("GpsTimeStamp"));
                    data.setSumMilesNum(jsonObject.getString("SumMilesNum"));

                    LatLng ptCenter = new LatLng((Float.valueOf(lat)), (Float.valueOf(lon)));
                    points.add(ptCenter)   ;
                    list.add(data);
                    latitudeList.add(Double.parseDouble(lat));
                    longitudeList.add(Double.parseDouble(lon));
//                    mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter).newVersion(0).radius(500));


                }
                maxLatitude = Collections.max(latitudeList);
                minLatitude = Collections.min(latitudeList);
                maxLongitude = Collections.max(longitudeList);
                minLongitude = Collections.min(longitudeList);
//                calculateDistance();
                    OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
                 mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                 mPolyline.setDottedLine(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }



    }
    BitmapDescriptor ionc = BitmapDescriptorFactory
            .fromResource(R.drawable.yichang);
    BitmapDescriptor ionc_st = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_st);

    BitmapDescriptor ionc_en = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_en);

    @Override
    public void onMapClick(LatLng latLng) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(mWXSDKInstance.getContext(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;


           if (result.getRouteLines().size() > 0) {
                route = result.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                routeOverlay = overlay;
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
//                mBtnPre.setVisibility(View.VISIBLE);
//                mBtnNext.setVisibility(View.VISIBLE);
            } else {
                Log.d("route result", "结果数<0");
                return;
            }

        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
//            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
//            }
            return null;
        }
    }
}
