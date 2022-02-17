package com.google.codelabs.mdc.java.shrine.network;

public class Conditions {

    public String name;
    public String operator;
    public String value;


    public Conditions(String name) {
        this.name = name;
    }

    public Conditions(String name, String o, String a) {
        this.name = name;
        this.operator = o;
        this.value = a;
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

    public void setValue(String english) {
        this.value = english;
    }

}
