package com.google.codelabs.mdc.java.shrine.network;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Conditions {

    public String name;
    public String operator = " ";
    public String value = " ";
    public String zoneGroup = " ";
    public String objectGroup = " ";
    public ArrayList<Integer> microbits = new ArrayList<>();
    public boolean asignedDevices = false;


    public Conditions(String name) {
        this.name = name;
        this.value = "0";
        this.operator = " ";
    }

    public Conditions(String name, String o, String a) {
        this.name = name;
        this.operator = o;
        this.value = a;
    }

    public Conditions(String name, String o, String a, String zone, String obj, ArrayList<Integer> k, boolean is) {
        this.name = name;
        this.operator = o;
        this.value = a;
        this.zoneGroup = zone;
        this.objectGroup = obj;
        this.microbits = k;
        this.asignedDevices = is;
    }

    public String getName() {
        return name;
    }

    public void setName(String english) {
        this.name = english;
    }
    public void setOperator(String english) {
        this.operator = english;
    }
    public String getOperator(){return this.operator;}
    public String getValue(){return this.value;}
    public String getZoneGroup(){return this.zoneGroup;}
    public String getObjectGroup(){return this.objectGroup;}

    public ArrayList<Integer> getMicrobits() {
        return microbits;
    }

    public boolean isTrue(){return this.asignedDevices;}

    public void setValue(String english) {
        this.value = english;
    }

    public void setZoneGroup(String english) {
        this.zoneGroup = english;
    }
    public void setObjectGroup(String english) {
        this.objectGroup = english;
    }
    public void setMicrobits(ArrayList<Integer> english) {
        this.microbits = english;
    }
    public void setAsignedDevices(boolean english) {
        this.asignedDevices = english;
    }


}
