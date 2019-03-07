/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */

package com.weex.plugins.baiduAMP.algo;


import com.baidu.mapapi.model.LatLng;

import java.util.Collection;

/**
 * A collection of ClusterItems that are nearby each other.
 */
public interface Cluster<T extends com.weex.plugins.baiduAMP.ClusterItem> {
    public LatLng getPosition();

    Collection<T> getItems();

    int getSize();
}