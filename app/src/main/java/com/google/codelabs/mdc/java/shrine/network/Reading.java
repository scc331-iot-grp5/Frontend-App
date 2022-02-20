package com.google.codelabs.mdc.java.shrine.network;

public class Reading {

    public String temp;
    public String acc;
    public String compass;
    public String time;



    public Reading(String name, String userid , String url, String time) {
        this.temp = name;
        this.acc = userid;
        this.compass = url;
        this.time = time;
    }
    public Reading(){
        this.temp = null;
    }
    public String getTemp() {
        return temp;
    }

    public void setTemp(String english) {
        this.temp = english;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public String getCompass() {
        return compass;
    }

    public void setCompass(String compass) {
        this.compass = compass;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
