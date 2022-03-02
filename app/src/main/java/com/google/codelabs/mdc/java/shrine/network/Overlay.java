package com.google.codelabs.mdc.java.shrine.network;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.Polygon;

public class Overlay {

    public String url;
    public GroundOverlay overlay;
    public int id;
    public int isDelete;


    public Overlay(GroundOverlay polygon,String name,int id) {
        this.overlay = polygon;
        this.url = name;
        this.id =id;
        this.isDelete = 0;
    }

    public GroundOverlay getOverlay() {
        return overlay;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setOverlay(GroundOverlay overlay) {
        this.overlay = overlay;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

