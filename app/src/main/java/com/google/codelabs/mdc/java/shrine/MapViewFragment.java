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

public class MapViewFragment extends Fragment implements OnMapReadyCallback, AdapterView.OnItemSelectedListener{

    private GoogleMap mMap;
    View rootView;
    PolygonOptions rectOptions;
    PolylineOptions polylineOptions;
    PolylineOptions polylineOptionsCircle;
    CircleOptions circleOptions;
    FrameLayout fram_map;
    Projection projection;
    public double latitude;
    public double longitude;

    String connection = "https://6e66-148-88-245-146.ngrok.io";

    int selectedColorR;
    int selectedColorG;
    int selectedColorB;

    int alpha;
    int ColorR;
    int ColorG;
    int ColorB;

    /* Or the android RGB Color (see the android Color class reference) */
    int selectedColorRGB;
    int backgroundColor;

    boolean Is_MAP_Moveable = true; // to detect map is movable
    boolean delete = false; // to detect map is movable
    boolean editable = false;
    boolean circle = false;

    ArrayList<LatLng> val = new ArrayList<>();
    ArrayList<LatLng> circleVals = new ArrayList<>();

    ArrayList<Polyline> listOfPolyLines = new ArrayList<>();

    ArrayList<Marker> markers = new ArrayList<>();

    ArrayList<Zone> listOfPolygons = new ArrayList<>();

    int flags[] = {R.drawable.baseline_gps_not_fixed_24,R.drawable.ic_outline_rectangle_24, R.drawable.ic_outline_circle_24, R.drawable.ic_outline_polyline_24, R.drawable.ic_baseline_edit_24, R.drawable.ic_baseline_delete_24};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.google_map, container, false);
        setUpToolbar(rootView);

        fram_map = (FrameLayout) rootView.findViewById(R.id.fram_map);
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        Spinner spin = (Spinner) rootView.findViewById(R.id.simpleSpinner);
        spin.setOnItemSelectedListener(this);

        CustomAdapter customAdapter=new CustomAdapter(getContext(),flags);
        spin.setAdapter(customAdapter);

        final ColorPicker cp = new ColorPicker(getActivity(), 0, 0, 0);

        final ColorPicker fill = new ColorPicker(getActivity(),255, 0, 0, 0);

        ImageButton im = (ImageButton) rootView.findViewById(R.id.Button01);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cp.show();
            }
        });

        ImageButton bg = (ImageButton) rootView.findViewById(R.id.Button02);
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fill.show();
            }
        });


        cp.show();
        cp.dismiss();
        fill.show();
        fill.dismiss();

        Button okColor = (Button) cp.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* You can get single channel (value 0-255) */
                selectedColorR = cp.getRed();
                selectedColorG = cp.getGreen();
                selectedColorB = cp.getBlue();

                /* Or the android RGB Color (see the android Color class reference) */
                selectedColorRGB = cp.getColor();
                im.setColorFilter(selectedColorRGB, android.graphics.PorterDuff.Mode.MULTIPLY);

                cp.dismiss();
            }
        });

        Button okColor2 = (Button) fill.findViewById(R.id.okColorButton);
        okColor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* You can get single channel (value 0-255) */
                ColorR = fill.getRed();
                ColorG = fill.getGreen();
                ColorB = fill.getBlue();
                alpha = fill.getAlpha();

                /* Or the android RGB Color (see the android Color class reference) */
                backgroundColor = fill.getColor();
                bg.setColorFilter(backgroundColor, android.graphics.PorterDuff.Mode.MULTIPLY);

                fill.dismiss();
            }
        });

        // Set cut corner background for API 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rootView.findViewById(R.id.product_grid).setBackgroundResource(R.drawable.shr_product_grid_background_shape);
        }

        MaterialButton nextButton = rootView.findViewById(R.id.logout);
        MaterialButton addDevice = rootView.findViewById(R.id.addDevice);
        MaterialButton map = rootView.findViewById(R.id.map);
        MaterialButton user = rootView.findViewById(R.id.users);
        MaterialButton rules = rootView.findViewById(R.id.rules);
        MaterialButton anal = rootView.findViewById(R.id.a);

        // Set an error if the password is less than 8 characters.
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAllZonesToDB();
                ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false); // Navigate to the next Fragment
            }
        });
        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAllZonesToDB();
                ((NavigationHost) getActivity()).navigateTo(new ManDev(), false); // Navigate to the next Fragment
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAllZonesToDB();
                ((NavigationHost) getActivity()).navigateTo(new MapViewFragment(), false); // Navigate to the next Fragment
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAllZonesToDB();
                ((NavigationHost) getActivity()).navigateTo(new AssUser(), false); // Navigate to the next Fragment
            }
        });
        rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAllZonesToDB();
                ((NavigationHost) getActivity()).navigateTo(new ViewAllRules(), false); // Navigate to the next Fragment
            }
        });
        anal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAllZonesToDB();
                ((NavigationHost) getActivity()).navigateTo(new Anal(), false); // Navigate to the next Fragment
            }
        });

        return rootView;
    }
    public void DrawLines(){
        polylineOptions = new PolylineOptions();
        polylineOptions.addAll(val);
        polylineOptions.color(selectedColorRGB);
        polylineOptions.width(5);
        Polyline newLine = mMap.addPolyline(polylineOptions);
        listOfPolyLines.add(newLine);

    }
    public void DrawLinesCircle(){

        for (int i = 0; i < listOfPolyLines.size(); i++) {
            listOfPolyLines.get(i).setVisible(false);
        }

        polylineOptionsCircle = new PolylineOptions();
        polylineOptionsCircle.addAll(circleVals);
        polylineOptionsCircle.color(selectedColorRGB);
        polylineOptionsCircle.width(5);
        Polyline newLine = mMap.addPolyline(polylineOptionsCircle);
        //listOfPolyLines.clear();
        listOfPolyLines.add(newLine);

    }
    public void Draw_Circle(){
        circleOptions = new CircleOptions();
        circleOptions.center(circleVals.get(0));

        double radius = distance(circleVals.get(0).latitude,circleVals.get(1).latitude,circleVals.get(0).longitude,circleVals.get(1).longitude,0.0,0.0);

        circleOptions.radius(radius);
        circleOptions.strokeColor(selectedColorRGB);
        circleOptions.fillColor(backgroundColor);
        Circle newCircle = mMap.addCircle(circleOptions);
        newCircle.setClickable(false);
        newCircle.setVisible(false);

        PolygonOptions ada = new PolygonOptions();
        ada.addAll(getPolgonFromCircle(newCircle));
        ada.strokeColor(newCircle.getStrokeColor());
        ada.strokeWidth(5);
        ada.fillColor(newCircle.getFillColor());

        Polygon newPoly = mMap.addPolygon(ada);
        newPoly.setVisible(true);
        newPoly.setClickable(true);

        listOfPolygons.add( new Zone(newPoly,"temp name",selectedColorRGB,backgroundColor,1000));

        for (int i = 0; i < listOfPolyLines.size(); i++) {
            listOfPolyLines.get(i).setVisible(false);
        }
        listOfPolyLines.clear();
    }
    public void Draw_Map(boolean fin) {
        rectOptions = new PolygonOptions();
        rectOptions.addAll(val);
        rectOptions.strokeColor(selectedColorRGB);
        rectOptions.strokeWidth(5);
        rectOptions.fillColor(backgroundColor);

        Polygon newPoly = mMap.addPolygon(rectOptions);

        newPoly.setClickable(true);

        listOfPolygons.add( new Zone(newPoly,"temp name",selectedColorRGB,backgroundColor,1000));

        for (int i = 0; i < listOfPolyLines.size(); i++) {
            listOfPolyLines.get(i).setVisible(false);
        }
        listOfPolyLines.clear();

    }
    public void forLocation(){

        String url = connection + "/locations";
        JSONArray z = new JSONArray();
        z.put(0);

        markers.clear();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url,z, new Response.Listener<JSONArray>() {
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
    public void getZonesLocations()
    {
        String url = connection + "/getZones";
        JSONArray z = new JSONArray();
        z.put(0);

        markers.clear();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url,z, new Response.Listener<JSONArray>() {
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
                                int color = Color.rgb(r,g,b);
                                int ba = (int) object1.get("ba");
                                int br = (int) object1.get("br");
                                int bg = (int) object1.get("bg");
                                int bb = (int) object1.get("bb");
                                int backgroundColor = Color.argb(ba,br,bg,bb);

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
                                    tempListOfVal.add(new LatLng(lon,lat));
                                }

                                if(mMap != null)
                                {
                                    PolygonOptions options = new PolygonOptions();
                                    options.addAll(tempListOfVal);
                                    options.strokeColor(color);
                                    options.strokeWidth(5);
                                    options.fillColor(backgroundColor);

                                    Polygon newPoly = mMap.addPolygon(options);
                                    newPoly.setClickable(true);

                                    listOfPolygons.add( new Zone(newPoly,name,color,backgroundColor,id));
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

    public void sendAllZonesToDB()
    {
        //delete all delete();
        for (int i = 0; i < listOfPolygons.size(); i++)
        {
            Polygon temp = listOfPolygons.get(i).getPolygon();
            String name = listOfPolygons.get(i).getName();
            int idd = listOfPolygons.get(i).getId();
            int border = listOfPolygons.get(i).getBorderColor();
            int background = listOfPolygons.get(i).getFillColor();
            int isD = listOfPolygons.get(i).getToDelete();

            JSONArray points = new JSONArray();
            JSONArray allPoints = new JSONArray();
            JSONArray geoJSON = new JSONArray();
            JSONObject b = new JSONObject();
            JSONObject a = new JSONObject();
            JSONObject json = new JSONObject();
            JSONObject f = new JSONObject();
            JSONObject zoneObject = new JSONObject();

            for (int x = 0; x < temp.getPoints().size(); x++) {
                JSONArray coord = new JSONArray();
                double lat = temp.getPoints().get(x).latitude;
                double lon = temp.getPoints().get(x).longitude;
                try {
                    coord.put(lon);
                    coord.put(lat);
                } catch (Exception e) {}
                points.put(coord);
            }

            try {
                allPoints.put(points);

                json.put("type", "Polygon");
                json.put("coordinates", allPoints);

                a.put("type", "Feature");
                a.put("properties", new JSONObject());
                a.put("geometry", json);

                geoJSON.put(a);
                b.put("type", "FeatureCollection");
                b.put("features", geoJSON);

                f.put("geoJSON", b.toString());

                zoneObject.put("id",idd);
                zoneObject.put("isDelete",isD);
                zoneObject.put("name",name);
                zoneObject.put("geoJSON",b);
                zoneObject.put("Rborder",selectedColorR);
                zoneObject.put("Gborder",selectedColorG);
                zoneObject.put("Bborder",selectedColorB);
                zoneObject.put("Abackground",alpha);
                zoneObject.put("Rbackground",ColorR);
                zoneObject.put("Gbackground",ColorG);
                zoneObject.put("Bbackground",ColorB);

            } catch (Exception e) {
            }

            String url = connection + "/zone";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, zoneObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error

                        }
                    });

            MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
        }
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
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        Toast.makeText(getContext(), "selected", Toast.LENGTH_LONG).show();
        if (flags[position] == R.drawable.ic_outline_rectangle_24){
            Is_MAP_Moveable = true;
            delete = false;
            editable = false;
            circle = false;
        }
        if (flags[position] == R.drawable.baseline_gps_not_fixed_24){
            Is_MAP_Moveable = false;
            delete = false;
            editable = false;
            circle = false;
        }
        if(flags[position] == R.drawable.ic_baseline_delete_24){
            delete = true;
            Is_MAP_Moveable = false;
            editable = false;
            circle = false;
        }
        if(flags[position] == R.drawable.ic_baseline_edit_24){
            Is_MAP_Moveable = false;
            delete = false;
            editable = true;
            circle = false;
        }
        if(flags[position] == R.drawable.ic_outline_circle_24){
            Is_MAP_Moveable = false;
            delete = false;
            editable = false;
            circle = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        getZonesLocations();
        forLocation();

        LatLng sydney = new LatLng(54.01024, -2.788584); // set camera locations
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMinZoomPreference(15.0f);

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(@NonNull Polygon polygon) {
                if(delete) {
                    polygon.setVisible(false);
                    polygon.setClickable(false);

                    for (int i = 0; i < listOfPolygons.size(); i++) {
                        if((listOfPolygons.get(i).getPolygon()).equals(polygon)) {
                            listOfPolygons.get(i).setToDelete(1);
                        }
                    }
                }
                if(editable){
                    for (int i = 0; i < listOfPolygons.size(); i++) {
                        if ((listOfPolygons.get(i).getPolygon()).equals(polygon))  {
                            PopUpEditName pu = new PopUpEditName();
                            pu.showPopupWindow(rootView,getContext(),listOfPolygons.get(i));
                        }
                    }
                }
            }
        });

        fram_map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(Is_MAP_Moveable) {
                    float x = event.getX();
                    float y = event.getY();

                    int x_co = Math.round(x);
                    int y_co = Math.round(y);

                    projection = mMap.getProjection();
                    Point x_y_points = new Point(x_co, y_co);

                    LatLng latLng = mMap.getProjection().fromScreenLocation(x_y_points);
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;

                    int eventaction = event.getAction();
                    switch (eventaction) {
                        case MotionEvent.ACTION_DOWN:
                            // finger touches the screen
                            val.add(new LatLng(latitude, longitude));
                            break;

                        case MotionEvent.ACTION_MOVE:
                            // finger moves on the screen
                            val.add(new LatLng(latitude, longitude));
                            DrawLines();
                            break;

                        case MotionEvent.ACTION_UP:
                            // finger leaves the screen
                            Draw_Map(true);
                            val.clear();
                            break;
                    }
                    return Is_MAP_Moveable;
                }
                if(circle) {
                    float x = event.getX();
                    float y = event.getY();

                    int x_co = Math.round(x);
                    int y_co = Math.round(y);

                    projection = mMap.getProjection();
                    Point x_y_points = new Point(x_co, y_co);

                    LatLng latLng = mMap.getProjection().fromScreenLocation(x_y_points);
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;

                    int eventaction = event.getAction();
                    switch (eventaction) {
                        case MotionEvent.ACTION_DOWN:
                            // finger touches the screen
                            circleVals.add(0,new LatLng(latitude, longitude));
                            break;

                        case MotionEvent.ACTION_MOVE:
                            // finger moves on the screen
                            circleVals.add(1,new LatLng(latitude, longitude));
                            DrawLinesCircle();
                            break;

                        case MotionEvent.ACTION_UP:
                            // finger leaves the screen
                            Draw_Circle();
                            circleVals.clear();
                            break;
                    }
                    return circle;
                }
                return false;
            }
        });
    }
    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
    public ArrayList<LatLng> getPolgonFromCircle(Circle circle){
        double R = 6378.1;
        ArrayList<LatLng> temp = new ArrayList<>();

        for (int x = 0; x < 360; x = x+ 15) {
            double brng = Math.toRadians(x);

            double d = circle.getRadius() / 1000;
            double centerLat = circle.getCenter().latitude;
            double centerLon = circle.getCenter().longitude;


            double lat1 = Math.toRadians(centerLat);
            double lon1 = Math.toRadians(centerLon);

            double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d / R) + Math.cos(lat1) * Math.sin(d / R) * Math.cos(brng));
            double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(d / R) * Math.cos(lat1), Math.cos(d / R) - Math.sin(lat1) * Math.sin(lat2));

            double newLat = Math.toDegrees(lat2);
            double newLon = Math.toDegrees(lon2);
            LatLng newCo= new LatLng(newLat,newLon);
            temp.add(newCo);
        }

        return temp;
    }
}