package com.google.codelabs.mdc.java.shrine.network;

public class Device {

    public String name;
    public int microbitID;
    public String objectName;


    public Device(String name, int userid , String url) {
        this.name = name;
        this.microbitID = userid;
        this.objectName = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String english) {
        this.name = english;
    }

    public int getMicrobitID() {
        return microbitID;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String english) {
        this.objectName = english;
    }

}
