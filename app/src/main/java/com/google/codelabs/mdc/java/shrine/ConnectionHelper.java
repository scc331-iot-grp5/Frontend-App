package com.google.codelabs.mdc.java.shrine;

import static java.sql.DriverManager.getConnection;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConnectionHelper {
    Connection connection;
    String ip, port, db, un, password;

    @SuppressLint("NewApi")
    public Connection conclass() {
        ip = "127.0.0.1";
        port = "3306";
        db = "iota";
        un = "Drushali";
        password = "Astjigmakdru@2001";
        StrictMode.ThreadPolicy tpolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tpolicy);
        Connection con = null;
        String ConnectionURL = null;
        /*try {
            Class.forName("com.mysql.jdbc.Driver");
            String url1 = "jdbc:mysql://10.0.2.2:3306/iota1?characterEncoding=utf8";
            String user = "root";
            String password = "Astjigmakdru@2001";

            con = getConnection(url1, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }*/

        try {
            Properties connectionProps = new Properties();
            connectionProps.put("user", "root");
            connectionProps.put("password", "AdaLovelace1815");
            connectionProps.put("useSSL", "false");

            Class.forName("com.mysql.jdbc.Driver");
            String url1 = "jdbc:mysql://dodecahedron.noah.katapult.cloud:3306/iota";

            con = getConnection(url1,connectionProps);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}
