package com.google.codelabs.mdc.java.shrine;


import android.graphics.Color;
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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.codelabs.mdc.java.shrine.network.Device;
import com.google.codelabs.mdc.java.shrine.network.ImageRequester;
import com.google.codelabs.mdc.java.shrine.network.ProductEntry;
import com.google.codelabs.mdc.java.shrine.network.Reading;
import com.google.codelabs.mdc.java.shrine.ReadingsAdapter;
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
import java.util.regex.Pattern;

public class CrashMap extends Fragment{

    View view;
    RequestQueue queue;
    CalendarPopUp calenderPopUp;
    String filterDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        view = inflater.inflate(R.layout.crash_map, container, false);
        queue = Volley.newRequestQueue(getContext());
        Mapbox.getInstance(getContext(), "pk.eyJ1IjoiY2FtZXJvbnB1Z2gyODIiLCJhIjoiY2t6OHdoNG1jMHp3dTJ2bXU4M2kzYmV3bCJ9.RMjNS0Ll5wPTkLt27txUsg");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
            setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_left));
            setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
        }
        // Set up the toolbar
        setUpToolbar(view);
        updateMap(view,savedInstanceState);

        //GET SPEED FROM DB UPDATE theSPEED

        Button popupButton = view.findViewById(R.id.selectDate);
        popupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                calenderPopUp = new CalendarPopUp();
                calenderPopUp.showPopupWindow(v,getContext());
            }
        });

        Button re = view.findViewById(R.id.refresh);
        re.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println(calenderPopUp.getDate());
                filterDate = calenderPopUp.getDate();
                updateMap(view,savedInstanceState);
            }
        });

       //MaterialDatePicker a = new MaterialDatePicker
        //System.out.println(calenderPopUp.getDate());


        return view;
    }
    private void updateMap(View view, Bundle savedInstanceState){
        MapView mapView = view.findViewById(R.id.crashMap);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback(){
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.clear();
                String url = "https://f074-86-4-178-72.ngrok.io/getCrashMap";
                JSONArray json = new JSONArray();
                JSONObject j = new JSONObject();

                try {
                    j.put("Date", filterDate);
                    json.put(0,j);
                }
                catch(Exception e){}

                JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                        (Request.Method.POST, url, json, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject object1 = response.getJSONObject(i);
                                        Double x = (Double) object1.get("latitude");
                                        Double y = (Double) object1.get("longitude");
                                        //String tittle = (String) object1.get("microbitId");
                                        LatLng newPoint = new LatLng(x,y);
                                        Icon personMark = IconFactory.getInstance(getContext()).fromResource(R.mipmap.ic_blje_foreground);
                                        Icon carMark = IconFactory.getInstance(getContext()).fromResource(R.mipmap.ic_orange_foreground);


                                        mapboxMap.addMarker(new MarkerOptions()
                                                    .position(newPoint)
                                                    .title("point"))
                                                    .setIcon(carMark);



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
        });
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
                ((NavigationHost) getActivity()).navigateTo(new Anal(), false); // Navigate to the next Fragment

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
