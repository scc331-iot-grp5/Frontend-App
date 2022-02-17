package com.google.codelabs.mdc.java.shrine.network;

public class Rules {

    public String name;
    public int ruleID;
    public String url;


    public Rules(String name, int ruleID , String url) {
        this.name = name;
        this.ruleID = ruleID;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String english) {
        this.name = english;
    }

    public int getMicrobitID() {
        return ruleID;
    }

    public String getObjectName() {
        return url;
    }

    public void setObjectName(String english) {
        this.url = english;
    }

}
