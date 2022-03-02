package com.google.codelabs.mdc.java.shrine;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.codelabs.mdc.java.shrine.heatmaplib.HeatMap;
import com.google.codelabs.mdc.java.shrine.heatmaplib.HeatMapMarkerCallback;
import com.google.codelabs.mdc.java.shrine.network.Device;
import com.google.codelabs.mdc.java.shrine.network.ImageRequester;
import com.google.codelabs.mdc.java.shrine.network.ProductEntry;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.os.AsyncTask;
import androidx.annotation.AnyThread;
import androidx.appcompat.app.AppCompatActivity;

public class HMap extends Fragment implements CompoundButton.OnCheckedChangeListener{
    private HeatMap map;
    private boolean testAsync = false;
    float left = 0;
    float top=0;
    RequestQueue queue;
    double temperature=0;
    View view;

    String connection = "https://6e66-148-88-245-146.ngrok.io";

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
        queue = Volley.newRequestQueue(getContext());
        setUpToolbar(view);

        CheckBox box = view.findViewById(R.id.change_async_status);
        box.setOnCheckedChangeListener(this);

        map = view.findViewById(R.id.example_map);
        map.setMinimum(0.0);
        map.setMaximum(100.0);
        map.setLeftPadding(100);
        map.setRightPadding(100);
        map.setTopPadding(100);
        map.setBottomPadding(100);
        map.setMarkerCallback(new HeatMapMarkerCallback.CircleHeatMapMarker(0xff9400D3));
        map.setRadius(80.0);
        Map<Float, Integer> colors = new ArrayMap<>();
        //build a color gradient in HSV from red at the center to green at the outside
        for (int i = 0; i < 21; i++) {
            float stop = ((float)i) / 20.0f;
            int color = doGradient(i * 5, 0, 100, 0xff00ff00, 0xffff0000);
            colors.put(stop, color);
        }
        map.setColorStops(colors);

        map.setOnMapClickListener(new HeatMap.OnMapClickListener() {
            @Override
            public void onMapClicked(int x, int y, HeatMap.DataPoint closest) {

            }
        });

        getData();
        return view;
    }

    private void getData(){
        String url = connection + "/heatMap";

        JSONArray json = new JSONArray();
        JSONObject j = new JSONObject();

        try {
            j.put("a", 0);
            json.put(0,j);
        }
        catch(Exception e){}

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.POST, url, json, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            System.out.println("Refresh");
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object1 = response.getJSONObject(i);
                                double x = (double) object1.get("lat");
                                double y = (double) object1.get("l");
                                x = x/1000;
                                y = y/1000;
                                addData(x,y);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        queue.add(jsonObjectRequest);
    }

    private void addData(double x, double y) {
        if (testAsync) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    drawNewMap(x,y);
                    map.forceRefreshOnWorkerThread();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            map.invalidate();
                        }
                    });
                }
            });
        }
        else {
            drawNewMap(x,y);
            map.forceRefresh();
        }
    }

    private void drawNewMap(double x, double y) {
        temperature = Double.parseDouble("22");
        HeatMap.DataPoint point = new HeatMap.DataPoint( clamp((float)x, 0.0f, 1.0f),
                clamp((float)y, 0.0f, 1.0f), clamp(temperature, 0.0, 100.0));
        map.addData(point);

    }

    @SuppressWarnings("SameParameterValue")
    private float clamp(float value, float min, float max) {
        return value * (max - min) + min;
    }

    @SuppressWarnings("SameParameterValue")
    private double clamp(double value, double min, double max) {
        return value * (max - min) + min;
    }

    @SuppressWarnings("SameParameterValue")
    private static int doGradient(double value, double min, double max, int min_color, int max_color) {
        if (value >= max) {
            return max_color;
        }
        if (value <= min) {
            return min_color;
        }
        float[] hsvmin = new float[3];
        float[] hsvmax = new float[3];
        float frac = (float)((value - min) / (max - min));
        Color.RGBToHSV(Color.red(min_color), Color.green(min_color), Color.blue(min_color), hsvmin);
        Color.RGBToHSV(Color.red(max_color), Color.green(max_color), Color.blue(max_color), hsvmax);
        float[] retval = new float[3];
        for (int i = 0; i < 3; i++) {
            retval[i] = interpolate(hsvmin[i], hsvmax[i], frac);
        }
        return Color.HSVToColor(retval);
    }

    private static float interpolate(float a, float b, float proportion) {
        return (a + ((b - a) * proportion));
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        testAsync = !testAsync;
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