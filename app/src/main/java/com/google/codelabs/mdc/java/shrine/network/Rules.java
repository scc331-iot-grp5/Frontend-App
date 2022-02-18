package com.google.codelabs.mdc.java.shrine.network;

import java.util.ArrayList;

public class Rules {

    public String name;
    public int ruleID;
    public String url;
    public ArrayList<Conditions> allCons;
    public String EventType;
    public String message;


    public Rules(String name, int ruleID , String url) {
        this.name = name;
        this.ruleID = ruleID;
        this.url = url;
    }
    public Rules(){
        this.allCons = new ArrayList<>();
    }
    public Rules(ArrayList<Conditions> allCons)
    {
        this.allCons = allCons;
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

    public ArrayList<Conditions> getAllCons() {
        return allCons;
    }
    public boolean isItEmpty(){
        return allCons.isEmpty();
    }
    public void setAllCons(ArrayList<Conditions> allCons) {
        this.allCons = allCons;
    }

    public String getEventType() {
        return EventType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setEventType(String eventType) {
        EventType = eventType;
    }
}
