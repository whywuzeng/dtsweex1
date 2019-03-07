package com.weex.plugins.baiduAMP;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.unisoft.zjc.utdts.R;
import com.weex.plugins.baiduAMP.algo.Cluster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 2018/12/18.
 * 实时跟踪
 */

public class RealTracking extends WXComponent<View> implements BaiduMap.OnMapLoadedCallback {
    WXSDKInstance mWXSDKInstance;
    MapStatus ms;
    private TextView overlay_date,overlay_sta,overlay_name,overlay_tel;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private List<MapBean> list;
    private ClusterManager<MyItem> mClusterManager;
    private LinearLayout overlay_bom;
    public RealTracking(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
        super(instance, dom, parent);
        mWXSDKInstance = instance;
        baidumap_infowindow = (LinearLayout) LayoutInflater.from(mWXSDKInstance.getContext()).inflate(R.layout.layout_baidu_pop, null);
//        formatter = new SimpleDateFormat("yyyy-MM-dd");

    }

    @Override
    protected View initComponentHostView(@NonNull Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout view = (LinearLayout) inflater
                .inflate(R.layout.activity_overlay, null);
        initView(view);
//        initData(context, view);

        return view;
    }

    private LinearLayout baidumap_infowindow;
    InfoWindow mInfoWindow;
   public void initView(View view){
       ImageView esc=(ImageView)view.findViewById(R.id.esc);
       overlay_date=(TextView) view.findViewById(R.id.overlay_date);
       overlay_sta=(TextView) view.findViewById(R.id.overlay_sta);
       overlay_name=(TextView) view.findViewById(R.id.overlay_name);
       overlay_tel=(TextView) view.findViewById(R.id.overlay_tel);
       overlay_bom=(LinearLayout)view.findViewById(R.id.overlay_bom);
       mMapView = (MapView) view.findViewById(R.id.bmapView);
       mBaiduMap = mMapView.getMap();
       mBaiduMap.setOnMapLoadedCallback(this);
//       mBaiduMap.showMapPoi(false);

       mClusterManager = new ClusterManager<MyItem>(mWXSDKInstance.getContext(), mBaiduMap);
//       // 设置地图监听，当地图状态发生改变时，进行点聚合运算
//       mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
       // 设置maker点击时的响应
       mBaiduMap.setOnMarkerClickListener(mClusterManager);
       mBaiduMap.setOnMapClickListener(listener);
       esc.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               overlay_bom.setVisibility(View.GONE);
           }
       });
       mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
           @Override
           public boolean onClusterClick(Cluster<MyItem> cluster) {
               Toast.makeText(mWXSDKInstance.getContext(),
                       "有" + cluster.getSize() + "辆车在此处!", Toast.LENGTH_SHORT).show();
               return false;
           }


       });
       mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
           @Override
           public boolean onClusterItemClick(MyItem item) {

               for (int i = 0; i <list.size() ; i++) {
                   if(list.get(i).getLon().equals(item.getPosition().longitude+"")){
                       createInfoWindow(baidumap_infowindow, list.get(i));
                       LatLng ll = new LatLng(Double.parseDouble(list.get(i).getLat()), Double.parseDouble(list.get(i).getLon()));
                        mInfoWindow = new InfoWindow(baidumap_infowindow, ll, -47);
                       //显示InfoWindow
                       mBaiduMap.showInfoWindow(mInfoWindow);


                       Map<String,Object> params=new HashMap<>();
                       params.put("Track",list.get(i));
                       mWXSDKInstance.fireGlobalEventCallback("mapRealTrack",params);
                   }
               }

               return false;
           }
       });

   }
    public void createInfoWindow(LinearLayout baidumap_infowindow, final MapBean mapBean){

        ((TextView) baidumap_infowindow.findViewById(R.id.pop_Truckcharsd)).setText(mapBean.getVelocity()+" Km/h");
        ((TextView) baidumap_infowindow.findViewById(R.id.pop_TruckNo)).setText(mapBean.getTruckNo());
        ((TextView) baidumap_infowindow.findViewById(R.id.pop_Truckdate)).setText(mapBean.getGpsTimeStamp());

        baidumap_infowindow.findViewById(R.id.pop_stop).setVisibility(View.GONE);
        baidumap_infowindow.findViewById(R.id.pop_star).setVisibility(View.GONE);
    }
    public void initOverlay(  BitmapDescriptor bdA){
        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(bdA);
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

    @WXComponentProp(name = "list")
    public void setValue(String value) {
        if(value!=null){
            list =new ArrayList<>();
            try {
                JSONArray jsonArray=new JSONArray(value);
                for (int i = 0; i <jsonArray.length() ; i++) {
                    MapBean mapBean=new MapBean();
                    JSONObject jsonObject= (JSONObject) jsonArray.get(i);
                    mapBean.setAccStatus(jsonObject.getString("AccStatus"));
                    mapBean.setLon(jsonObject.getString("TrackLon"));
                    mapBean.setLat(jsonObject.getString("TrackLat"));
                    mapBean.setDriverName(jsonObject.getString("DriverName"));
                    mapBean.setTruckNo(jsonObject.getString("TruckNo"));
                    mapBean.setPhoneNo(jsonObject.getString("phoneNo"));
                    mapBean.setGpsTimeStamp(jsonObject.getString("GpsTimeStamp"));
                    mapBean.setBillstatusname(jsonObject.getString("billstatusname"));
                    mapBean.setVelocity(jsonObject.getString("Velocity"));
                    mapBean.setBill_Id(jsonObject.getString("Bill_Id"));
                    mapBean.setOrientation(jsonObject.getString("Orientation"));
                    list.add(mapBean);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            List<MyItem> items =new ArrayList<>();
            BitmapDescriptor bdA;
            for (int i = 0; i <list.size() ; i++) {
                if(i==0){
                    ms = new MapStatus.Builder().target(new LatLng(Double.parseDouble(list.get(0).getLat()), Double.parseDouble(list.get(0).getLon()))).zoom(7).build();
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
                }

                LatLng llA=new LatLng(Double.parseDouble(list.get(i).getLat()), Double.parseDouble(list.get(i).getLon()));
                float rotate_value = 0;
                try {
                    rotate_value = Float.valueOf(list.get(i).getOrientation());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                items.add(new MyItem(llA, rotate_value));
            }


            addMarkers(items);
//        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(builder.build());
//
//        mBaiduMap.setMapStatus(u);

        }

    }


    /**
     * 向地图添加Marker点
     */
    public void addMarkers(List<MyItem> items) {


        mClusterManager.addItems(items);
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        if(list.size()>1)
        ms = new MapStatus.Builder().target(new LatLng(Double.parseDouble(list.get(0).getLat()), Double.parseDouble(list.get(0).getLon()))).zoom(8).build();
       else
        if(list.size()==1){
            ms = new MapStatus.Builder().target(new LatLng(Double.parseDouble(list.get(0).getLat()), Double.parseDouble(list.get(0).getLon()))).zoom(2).build();

        }
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));



    }

    @Override
    public void onMapLoaded() {
        if(list.size()>1)
            ms = new MapStatus.Builder().target(new LatLng(Double.parseDouble(list.get(0).getLat()), Double.parseDouble(list.get(0).getLon()))).zoom(8).build();
        else
        if(list.size()==1){
            ms = new MapStatus.Builder().target(new LatLng(Double.parseDouble(list.get(0).getLat()), Double.parseDouble(list.get(0).getLon()))).zoom(15).build();

        }
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }

    /**
     * 每个Marker点，包含Marker点坐标以及图标
     */
    public class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private float mRotate;

        public MyItem(LatLng latLng,float rotate) {
            mPosition = latLng;
            mRotate = rotate;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public float getRotate() {
            return mRotate;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            for (int i = 0; i < list.size(); i++) {
                if(Double.parseDouble(mPosition.latitude+"")==Double.parseDouble(list.get(i).getLat())){
                    if(list.get(i).getAccStatus().equals("1")){
                        return BitmapDescriptorFactory
                                .fromResource(R.drawable.che_hong);
                    }else if(list.get(i).getAccStatus().equals("2")){
                        return BitmapDescriptorFactory
                                .fromResource(R.drawable.che_hui);
                    }else if(list.get(i).getAccStatus().equals("0")){
                        return BitmapDescriptorFactory
                                .fromResource(R.drawable.che_lv);
                    }
                }
            }
            return BitmapDescriptorFactory
                    .fromResource(R.drawable.ionc_char);
        }
    }

    @Override
    public void onActivityPause() {
        mMapView.onPause();
        super.onActivityPause();

    }

    @Override
    public void onActivityDestroy() {
        mMapView.onDestroy();
        super.onActivityDestroy();

    }

    @Override
    public void onActivityResume() {
        mMapView.onResume();
        super.onActivityResume();

    }



}
