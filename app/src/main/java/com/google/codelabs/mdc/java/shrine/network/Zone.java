package com.google.codelabs.mdc.java.shrine.network;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Polygon;

public class Zone {

    public String name;
    public int borderColor;
    public int fillColor;

    public Polygon polygon;


    public Zone(Polygon polygon,String name , int borderColor, int fillColor) {
        this.polygon = polygon;
        this.name = name;
        this.borderColor = borderColor;
        this.fillColor = fillColor;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public String getName() {
        return name;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public Polygon getPolygon() {
        return polygon;
    }

}
