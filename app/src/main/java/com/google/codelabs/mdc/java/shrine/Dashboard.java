package com.google.codelabs.mdc.java.shrine;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.codelabs.mdc.java.shrine.network.Device;
import com.google.codelabs.mdc.java.shrine.network.ImageRequester;
import com.google.codelabs.mdc.java.shrine.network.ProductEntry;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class Dashboard extends Fragment{

    RequestQueue queue;
    View view;



    GraphView graph2;

    PieChartView pieChartView;
    List<SliceValue> pieData = new ArrayList<>();

    String connection = "https://5f6b-148-88-245-64.ngrok.io";


    boolean filter = true;
    boolean filterSerial = false;
    boolean filterDb = true;

    private final Handler mHandler = new Handler();

    private Runnable mTimer2;

    private LineGraphSeries<DataPoint> mSeries2;
    private double graph2LastXValue = 5d;

    int style;
    Dashboard(int style){
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
        view = inflater.inflate(R.layout.dashboard, container, false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
            setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_left));
            setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
        }
        queue = Volley.newRequestQueue(getContext());
        //Menu menu = view.findViewById(R.id.menu);
        // Set up the toolbar
        setUpToolbar(view);

        // Set cut corner background for API 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.product_grid).setBackgroundResource(R.drawable.shr_product_grid_background_shape);
        }

        MaterialButton nextButton = view.findViewById(R.id.logout);
        MaterialButton addDevice = view.findViewById(R.id.addDevice);
        MaterialButton map = view.findViewById(R.id.map);
        MaterialButton user = view.findViewById(R.id.users);
        MaterialButton rules = view.findViewById(R.id.rules);
        MaterialButton anal = view.findViewById(R.id.a);
        MaterialButton chats = view.findViewById(R.id.chats);

        MaterialButton systemCOnfig = view.findViewById(R.id.SystemConfig);
        MaterialButton dashboard = view.findViewById(R.id.domain);
        MaterialButton object = view.findViewById(R.id.object);

        // Set an error if the password is less than 8 characters.
        systemCOnfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new Settings(style), false); // Navigate to the next Fragment
            }
        });
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new Dashboard(style), false); // Navigate to the next Fragment
            }
        });
        object.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new Objects(style), false); // Navigate to the next Fragment
            }
        });

        // Set an error if the password is less than 8 characters.
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false); // Navigate to the next Fragment
            }
        });
        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new ManDev(style), false); // Navigate to the next Fragment
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new MapViewFragment(0,style), false); // Navigate to the next Fragment
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new AssUser(style), false); // Navigate to the next Fragment
            }
        });
        rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new ViewAllRules(style), false); // Navigate to the next Fragment
            }
        });
        anal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new Anal(style), false); // Navigate to the next Fragment
            }
        });
        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new AllChats_Admins(0,style), false); // Navigate to the next Fragment
            }
        });

        graph2 = (GraphView) view.findViewById(R.id.graph);

        AppCompatImageButton filterButton = view.findViewById(R.id.toolbar_settings_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewa) {
                CardView cv = view.findViewById(R.id.filterOptions);
                if(filter) {
                    cv.setVisibility(View.VISIBLE);
                    filter = false;
                }
                else{
                    cv.setVisibility(View.GONE);
                    filter = true;
                }
            }
        });

        CheckBox fOverlay = view.findViewById(R.id.ServerRequest);
        CheckBox fZones = view.findViewById(R.id.MicrobitSerial);

        fOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDb = !filterDb;
                filterSerial = false;
                fZones.setChecked(false);
                db();

            }
        });

        fZones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterSerial = !filterSerial;
                filterDb = false;
                fOverlay.setChecked(false);
                m();

            }
        });

        mSeries2 = new LineGraphSeries<>();
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(0);
        graph2.getViewport().setMaxX(40);

        mSeries2.setTitle("Total Server Request");
        mSeries2.setDrawBackground(true);
        mSeries2.setColor(Color.argb(255, 255, 60, 60));
        mSeries2.setBackgroundColor(Color.argb(100, 204, 119, 119));

        graph2.addSeries(mSeries2);
        // legend
        graph2.getLegendRenderer().setVisible(true);
        graph2.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        //pie chart -----------

        pieChartView = view.findViewById(R.id.chart);
        getConnectedDevices();


        return view;
    }
    public void db()
    {
        mSeries2.setTitle("Total Server Request");
        mSeries2.setDrawBackground(true);
        mSeries2.setColor(Color.argb(255, 255, 60, 60));
        mSeries2.setBackgroundColor(Color.argb(100, 204, 119, 119));

    }
    public void m()
    {
        mSeries2.setTitle("Device Transfer Rate");
        mSeries2.setDrawBackground(true);
        mSeries2.setColor(Color.argb(255, 60, 255, 60));
        mSeries2.setBackgroundColor(Color.argb(100, 119, 204, 119));

    }
    public void getConnectedDevices()
    {
        JSONArray j = new JSONArray();
        JSONObject json = new JSONObject();

        try {
            json.put("sss","ad");
            j.put(json);
        }
        catch(Exception e){}

        String url = connection + "/connectionDevices";

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, j, new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        int total = 0;
                        try {
                            System.out.println("here");
                            for (int i = 0; i < response.length(); i++) {
                                System.out.println("1");
                                JSONObject object1 = response.getJSONObject(i);
                                int a = (int) object1.get("total");
                                total = total + a;
                                pieData.add(new SliceValue( a, Color.parseColor((String) object1.get("ARGB_Value"))).setLabel((String)object1.get("name") + ": " +Integer.toString(a)));
                            }
                        }catch(Exception e){
                            System.out.println(e);
                        }

                        PieChartData pieChartData = new PieChartData(pieData);
                        pieChartData.setHasLabels(true);
                        pieChartData.setHasCenterCircle(true).setCenterText1("Total Connections: \n" + total).setCenterText1FontSize(12).setCenterText1Color(Color.parseColor("#0097A7"));
                        pieChartView.setPieChartData(pieChartData);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    public void onResume()
    {
        super.onResume();

        mTimer2 = new Runnable() {
            @Override
            public void run() {
                if(filterDb){
                    getSystemStyle(this);
                }

                else if(filterSerial) {
                    getSystemStyleSerial(this);
                }

            }
        };
        mHandler.postDelayed(mTimer2, 5000);
    }
    private void getSystemStyle(Runnable x)
    {
        JSONArray j = new JSONArray();
        JSONObject json = new JSONObject();

        try {
            json.put("sss","ad");
            j.put(json);
        }
        catch(Exception e){}

        String url = connection + "/connectionData";

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, j, new Response.Listener<JSONArray>(){
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            System.out.println("now here");
                            JSONObject object1 = response.getJSONObject(0);
                            int connection = (int) object1.get("user_requests");

                            graph2LastXValue += 1d;

                            mSeries2.appendData(new DataPoint(graph2LastXValue, connection), true, 40);
                            mHandler.postDelayed(x, 600);

                        }catch(Exception e){
                            System.out.println(e);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    private void getSystemStyleSerial(Runnable x)
    {
        JSONArray j = new JSONArray();
        JSONObject json = new JSONObject();

        try {
            json.put("sss","ad");
            j.put(json);
        }
        catch(Exception e){}

        String url = connection + "/connectionDataSerial";

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, j, new Response.Listener<JSONArray>(){
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            System.out.println("now here");
                            JSONObject object1 = response.getJSONObject(0);
                            int connection = (int) object1.get("serial_messages_total");

                            graph2LastXValue += 1d;

                            mSeries2.appendData(new DataPoint(graph2LastXValue, connection), true, 40);
                            mHandler.postDelayed(x, 600);

                        }catch(Exception e){
                            System.out.println(e);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer2);
        super.onPause();
    }


    double mLastRandom = 2;
    Random mRand = new Random();
    private double getRandom()
    {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }
    /*
    public void initGraph(GraphView graph) {
        DataPoint[] points = new DataPoint[50];
        for (int i = 0; i < 50; i++) {
            points[i] = new DataPoint(i, Math.sin(i*0.5) * 20*(Math.random()*10+1));
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);

        // set manual X bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(10);

        // enable scrolling
        graph.getViewport().setScrollable(true);

        series.setTitle("Random Curve");

        graph.addSeries(series);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);;
        /*
        series2.setTitle("speed");
        series2.setDrawBackground(true);
        series2.setColor(Color.argb(255, 255, 60, 60));
        series2.setBackgroundColor(Color.argb(100, 204, 119, 119));
        series2.setDrawDataPoints(true);
        graph.addSeries(series2);
        */


        /*
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

         */

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.product_grid),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.shr_branded_menu), // Menu open icon
                getContext().getResources().getDrawable(R.drawable.shr_close_menu))); // Menu close icon
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.shr_toolbar_menu, menu);
        //menuInflater.inflate(R.menu.objects, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }
}
