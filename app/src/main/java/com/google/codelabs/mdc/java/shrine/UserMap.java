package com.google.codelabs.mdc.java.shrine;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;
import com.google.codelabs.mdc.java.shrine.network.Device;
import com.google.codelabs.mdc.java.shrine.network.Zone;
import com.google.codelabs.mdc.java.shrine.network.ZoneCircle;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;



import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class UserMap extends Fragment implements OnMapReadyCallback{

    private GoogleMap mMap;
    View rootView;
    FrameLayout fram_map;
    int userID;

    String connection = "https://6e66-148-88-245-146.ngrok.io";



    ArrayList<Marker> markers = new ArrayList<>();

    ArrayList<Zone> listOfPolygons = new ArrayList<>();

    public UserMap(int userID){
        this.userID = userID;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.user_google_map, container, false);
        setUpToolbar(rootView);

        fram_map = (FrameLayout) rootView.findViewById(R.id.fram_map);
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        // Set cut corner background for API 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rootView.findViewById(R.id.product_grid).setBackgroundResource(R.drawable.shr_product_grid_background_shape);
        }

        MaterialButton nextButton = rootView.findViewById(R.id.logout);

        MaterialButton map = rootView.findViewById(R.id.map);
        MaterialButton profile = rootView.findViewById(R.id.myProfile);
        MaterialButton a = rootView.findViewById(R.id.myAnalytics);

        // Set an error if the password is less than 8 characters.
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false); // Navigate to the next Fragment
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new UserMap(userID), false); // Navigate to the next Fragment
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new MyProfile(userID), false); // Navigate to the next Fragment
            }
        });
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new MyAnalytics(userID), false); // Navigate to the next Fragment
            }
        });

        return rootView;
    }

    /**
     *
     * Change to device by userID
     *
     */
    public void forLocation(){

        String url = connection + "/IndividualLocations";
        JSONArray z = new JSONArray();
        JSONObject json = new JSONObject();

        try {
            json.put("userID",userID);
            z.put(json);
        }
        catch(Exception e){}

        markers.clear();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.POST, url,z, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            System.out.println("Refresh");
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object1 = response.getJSONObject(i);

                                double latitude = (double) object1.get("lat");
                                double longitude = (double) object1.get("lon");
                                String name = (String) object1.get("name");

                                LatLng newCo = new LatLng(latitude, longitude);

                                if (mMap != null) {
                                    Marker mDarwin1 = mMap.addMarker(new MarkerOptions()
                                            .position(newCo)
                                            .title(name));

                                    markers.add(mDarwin1);
                                }
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

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    public void getZonesLocations() {
        String url = connection + "/getZones";
        JSONArray z = new JSONArray();
        z.put(0);

        markers.clear();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, z, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            System.out.println("Refresh");
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object1 = response.getJSONObject(i);

                                String name = (String) object1.get("name");
                                int id = (int) object1.get("id");
                                int r = (int) object1.get("r");
                                int g = (int) object1.get("g");
                                int b = (int) object1.get("b");
                                int color = Color.rgb(r, g, b);
                                int backgroundColor = Color.argb(10, r, g, b);

                                System.out.println(color);
                                System.out.println(backgroundColor);

                                JSONObject aq = object1.getJSONObject("geo_json");
                                JSONArray trimmedJSON = aq.getJSONArray("features");
                                JSONObject a = trimmedJSON.getJSONObject(0);
                                JSONObject geoOb = a.getJSONObject("geometry");
                                JSONArray point = (JSONArray) geoOb.get("coordinates");
                                JSONArray allPoint = (JSONArray) point.getJSONArray(0);
                                ArrayList<LatLng> tempListOfVal = new ArrayList<>();

                                for (int x = 0; x < allPoint.length(); x++) {
                                    JSONArray pair = allPoint.getJSONArray(x);
                                    double lat = (double) pair.get(0);
                                    double lon = (double) pair.get(1);

                                    System.out.println(lat);
                                    System.out.println(lon);
                                    tempListOfVal.add(new LatLng(lon, lat));
                                }

                                if (mMap != null) {
                                    PolygonOptions options = new PolygonOptions();
                                    options.addAll(tempListOfVal);
                                    options.strokeColor(color);
                                    options.strokeWidth(5);
                                    options.fillColor(backgroundColor);

                                    Polygon newPoly = mMap.addPolygon(options);
                                    newPoly.setClickable(true);

                                    listOfPolygons.add(new Zone(newPoly, name, color, backgroundColor,id));
                                    tempListOfVal.clear();
                                }

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

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

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
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        getZonesLocations();
        forLocation();

        LatLng sydney = new LatLng(54.01024, -2.788584); // set camera locations
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMinZoomPreference(15.0f);

    }
}