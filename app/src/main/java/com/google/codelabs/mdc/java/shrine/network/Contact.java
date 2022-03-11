package com.google.codelabs.mdc.java.shrine.network;

import java.util.Objects;

public class Contact {

    private String name;
    private String recentMessage;
    private String Date;
    private String url;
    private int id;

    public Contact(int id,String name, String recentMessage, String Date, String url){
        this.id = id;
        this.name = name;
        this.recentMessage = recentMessage;
        this.Date = Date;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return Date;
    }

    public String getRecentMessage() {
        return recentMessage;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setRecentMessage(String recentMessage) {
        this.recentMessage = recentMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(name, contact.name) && Objects.equals(recentMessage, contact.recentMessage) && Objects.equals(Date, contact.Date) && Objects.equals(url, contact.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, recentMessage, Date, url);
    }
}
