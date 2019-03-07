package com.weex.plugins.baiduAMP;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by mac on 2018/12/18.
 */

public interface ClusterItem {

    /**
     * The position of this marker. This must always return the same value.
     */
    LatLng getPosition();

    /**
     * 标记点旋转方向
     * @return
     */
    float getRotate();
    
    BitmapDescriptor getBitmapDescriptor();
}
