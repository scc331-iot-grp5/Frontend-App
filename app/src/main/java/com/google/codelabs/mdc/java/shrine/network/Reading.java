package com.google.codelabs.mdc.java.shrine.network;

public class Reading {

    public String temp;
    public String acc;
    public String compass;


    public Reading(String name, String userid , String url) {
        this.temp = name;
        this.acc = userid;
        this.compass = url;
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


}
