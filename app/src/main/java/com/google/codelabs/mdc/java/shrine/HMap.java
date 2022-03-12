package com.google.codelabs.mdc.java.shrine;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class HMap extends Fragment {

    private GoogleMap mMap;
    HeatmapTileProvider heatmapTileProvider;
    Connection connect;
    View view;

    int style;
    HMap(int style){
        this.style = style;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(style == 2000016)
            getContext().setTheme(R.style.Theme_Shrine);
        else if(style == 2000552)
            getContext().setTheme(R.style.Theme_Shrine_Autumn);
        else if(style == 3)
            getContext().setTheme(R.style.Theme_Shrine_Blue);
        else if(style == 4)
            getContext().setTheme(R.style.Theme_Shrine_Purple);
        else if(style == 5)
            getContext().setTheme(R.style.Theme_Shrine_Red);

        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        view = inflater.inflate(R.layout.hmap, container, false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
            setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_left));
            setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
        }

        // Set up the toolbar
        setUpToolbar(view);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = (Connection) connectionHelper.conclass();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                CameraUpdate point = CameraUpdateFactory.newLatLng(new LatLng(54.0097537, -2.7875727));
                mMap.moveCamera(point);
                mMap.animateCamera(point);

                buildheatmap();
            }
        });

        return view;
    }


    int[] colors = {
            Color.GREEN,    // green
            Color.YELLOW,    // yellow
            Color.rgb(255,165,0), //Orange
            Color.RED,              //red
            Color.rgb(153,50,204), //dark orchid
            Color.rgb(165,42,42) //brown
    };

    float[] startpoints = {
            0.0f,    //0-50
            0.1f,   //51-100
            0.2f,   //101-150
            0.3f,   //151-200
            0.4f,    //201-300
            0.6f      //301-500
    };


    private ArrayList addheatmap() {
        ArrayList<WeightedLatLng> arr = new ArrayList<>();
        try {
            //ConnectionHelper connectionHelper = new ConnectionHelper();
            //connect = (Connection) connectionHelper.conclass();
            if(connect!=null){
                String query = "SELECT var.latitude, var.longitude, coalesce(var.temp_degrees, 20)/100, max(var.reported_at)\n" +
                        "FROM view_all_readings AS var, devices d, device_types dt\n" +
                        "WHERE var.device_id = d.id\n" +
                        "AND d.type = dt.id\n" +
                        "AND dt.name = 'Car'\n" +
                        "AND var.latitude IS NOT NULL\n" +
                        "GROUP BY var.latitude, var.longitude, var.temp_degrees";
                Statement smt = connect.createStatement();
                ResultSet rs = smt.executeQuery(query);

                while(rs.next()){
                    double lat = Double.parseDouble(rs.getString(1));
                    double lng = Double.parseDouble(rs.getString(2));
                    double temperature = Double.parseDouble(rs.getString(3));
                    arr.add(new WeightedLatLng(new LatLng(lat, lng),temperature));
                }
            }
        }
        catch (Exception ex){
            Log.e("Error :", ex.getMessage());
        }
        Log.e("adding heatmap","yes");

        return arr;
    }


    private void buildheatmap(){
        Gradient gradient = new Gradient(colors,startpoints);
        heatmapTileProvider = new HeatmapTileProvider.Builder()
                .weightedData(addheatmap())
                .radius(20)
                .gradient(gradient)
                .build();
        TileOverlayOptions tileoverlayoptions = new TileOverlayOptions().tileProvider(heatmapTileProvider);
        TileOverlay tileoverlay = mMap.addTileOverlay(tileoverlayoptions);
        tileoverlay.clearTileCache();
        Toast.makeText(getContext(),"added heatmap",Toast.LENGTH_SHORT).show();
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationHost) getActivity()).navigateTo(new Anal(style), false); // Navigate to the next Fragment

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.shr_toolbar_menu, menu);
        //menuInflater.inflate(R.menu.objects, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

}