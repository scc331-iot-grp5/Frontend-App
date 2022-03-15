package com.google.codelabs.mdc.java.shrine.network;

public class Type {
    private int id;
    private  String name;

    public Type(int id, String name){
        this.id = id;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
