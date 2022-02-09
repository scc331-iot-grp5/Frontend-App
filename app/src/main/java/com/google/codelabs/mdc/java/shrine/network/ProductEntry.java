package com.google.codelabs.mdc.java.shrine.network;

import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A product entry in the list of products.
 */
public class ProductEntry {

    public String name;
    public int userid;
    public String url;


    public ProductEntry(String name, int userid , String url) {
        this.name = name;
        this.userid = userid;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String english) {
        this.name = english;
    }

    public int getUserID() {
        return userid;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String english) {
        this.url = english;
    }

}