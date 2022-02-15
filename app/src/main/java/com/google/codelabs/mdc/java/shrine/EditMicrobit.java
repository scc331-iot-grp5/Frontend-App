package com.google.codelabs.mdc.java.shrine;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Pattern;

public class EditMicrobit extends Fragment implements OnItemSelectedListener {

    int microbitID;
    TextInputLayout speedLimitTextInput;
    TextInputLayout microbitTextInput;
    LatLng gPoint;
    String name;
    String type;
    String rt;
    int id ;
    int speed ;
    Double x ;
    Double y ;
    LatLng newPoint;
    Spinner spinner;
    Spinner spinnerTwo;
    public String objectName = "";
    public String roadName = "";


    public EditMicrobit(int userid){
        this.microbitID = userid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        Mapbox.getInstance(getContext(), "pk.eyJ1IjoiY2FtZXJvbnB1Z2gyODIiLCJhIjoiY2t6OHdoNG1jMHp3dTJ2bXU4M2kzYmV3bCJ9.RMjNS0Ll5wPTkLt27txUsg");

        View view = inflater.inflate(R.layout.edit_device, container, false);
        //Menu menu = view.findViewById(R.id.menu);
        getData(view,savedInstanceState);

        // Set up the toolbar
        setUpToolbar(view);

        spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);


        spinnerTwo = (Spinner) view.findViewById(R.id.roadType);
        spinnerTwo.setOnItemSelectedListener(this);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MapView mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback(){
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.addMarker(new MarkerOptions()
                        .position(newPoint)
                        .title("Current Location"));

                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        mapboxMap.clear();
                        gPoint = point;
                        Toast.makeText(getContext(), String.format("Location Selected at: %s", point.toString()), Toast.LENGTH_LONG).show();
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(point)
                                .title("New Location"));
                    }
                });
            }
        });

        microbitTextInput = view.findViewById(R.id.microbitText2);
        final TextInputEditText microbitEditText = view.findViewById(R.id.microbitTextInput2);

        speedLimitTextInput = view.findViewById(R.id.speedLimit2);
        final TextInputEditText speedLimitEditText = view.findViewById(R.id.speedLimitInput2);
        final TextInputEditText la = view.findViewById(R.id.microbitTextInputName);

        microbitEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isMValid(microbitEditText.getText())) {
                    microbitTextInput.setError(null); //Clear the error
                }
                else if(containsLetters(microbitEditText.getText())) {
                    microbitTextInput.setError(getString(R.string.m_letters));
                }
                else if(!containsLetters(microbitEditText.getText())) {
                    microbitTextInput.setError(null); //Clear the error
                }
                return false;
            }
        });

        speedLimitEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isSValid(speedLimitEditText.getText())) {
                    speedLimitTextInput.setError(null);
                }
                else if(containsLetters(speedLimitEditText.getText())) {
                    speedLimitTextInput.setError(getString(R.string.m_letters));
                }
                else if(!containsLetters(speedLimitEditText.getText())) {
                    speedLimitTextInput.setError(null); //Clear the error
                }
                return false;
            }
        });

        MaterialButton add = view.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isMValid(microbitEditText.getText())) {
                    microbitTextInput.setError(getString(R.string.m_length));
                }
                else if(!objectName.equals("Select an Object")) {
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle(R.string.d_tittle)
                            .setMessage(R.string.d_extra)
                            .setPositiveButton(R.string.update, (dialog, which) -> {
                                JSONObject json = new JSONObject();
                                String s;
                                if (roadName.equals("Select a Road Type")) {
                                    roadName = "undefined";
                                }
                                s = "undefined";
                                if (speedLimitEditText.getText() != null) {
                                    s = speedLimitEditText.getText().toString();
                                }
                                Double lat = 0.0;
                                Double lon = 0.0;

                                if (gPoint != null) {
                                    lat = gPoint.getLatitude();
                                    lon = gPoint.getLongitude();
                                }
                                try {
                                    json.put("roadType", roadName);
                                    json.put("object", objectName);
                                    json.put("microbitID", microbitEditText.getText().toString());
                                    json.put("mName", la.getText().toString());
                                    json.put("speedLimit", s);
                                    json.put("lat", lat);
                                    json.put("long", lon);
                                } catch (Exception e) {
                                    System.out.println(e);
                                }

                                String url = "https://f074-86-4-178-72.ngrok.io/updateDevice";

                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                        (Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                // TODO: Handle error
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // TODO: Handle error

                                            }
                                        });

                                MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
                            })
                            .setNegativeButton(R.string.d_cancel, (dialog, which) -> {

                            })
                            .show();
                }
                else{
                    microbitTextInput.setError(getString(R.string.o_error));
                }
            }
        });

        return view;



    }
    public static <T> T[] add2BeginningOfArray(T[] elements, T element)
    {
        T[] newArray = Arrays.copyOf(elements, elements.length + 1);
        newArray[0] = element;
        System.arraycopy(elements, 0, newArray, 1, elements.length);

        return newArray;
    }
    private void getData(View view,Bundle savedInstanceState){
        JSONObject json = new JSONObject();

        try {
            json.put("mID",microbitID);
        }
        catch(Exception e){}

        String url = "https://f074-86-4-178-72.ngrok.io/microbitIndividual";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, json, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                             name = (String) response.get("name");
                             type = (String) response.get("type");
                             id = (int) response.get("id");
                            if(type.equals("RoadMark")){
                                 speed = (int) response.get("speed");
                                 rt = (String) response.get("rt");
                                 x = (Double) response.get("x");
                                 y = (Double) response.get("y");
                                 newPoint = new LatLng(x,y);
                                 speedLimitTextInput.setEnabled(true);
                                 TextInputEditText speedLimitEditText = view.findViewById(R.id.speedLimitInput2);
                                 speedLimitEditText.setText(Integer.toString(speed));

                                String[] myItemsa= getResources().getStringArray(R.array.Road_array_2);
                                String[] myItemsTwoa = add2BeginningOfArray(myItemsa,rt);
                                ArrayAdapter<CharSequence> adapterTwo = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, myItemsTwoa);
                                adapterTwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerTwo.setAdapter(adapterTwo);
                            }


                            TextInputEditText idBox =  view.findViewById(R.id.microbitTextInput2);
                            TextInputEditText nameBox =  view.findViewById(R.id.microbitTextInputName);

                            nameBox.setText(name);
                            idBox.setText(Integer.toString(id));

                            String[] myItems= getResources().getStringArray(R.array.types);
                            String[] myItemsTwo = add2BeginningOfArray(myItems,type);
                            ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, myItemsTwo);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);



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
    private boolean isMValid(@Nullable Editable text) {
        return text != null && text.length() == 4;
    }
    private boolean isSValid(@Nullable Editable text) {
        if(text != null && text.length() > 1){
            return true;
        }
        else if(text == null){
            return true;
        }
        return false;
    }
    private boolean containsLetters(@Nullable Editable text) {
        return Pattern.matches("[a-zA-Z]+", text.toString());
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
                ((NavigationHost) getActivity()).navigateTo(new ManDev(), false); // Navigate to the next Fragment

            }
        });
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.shr_toolbar_menu, menu);
        //menuInflater.inflate(R.menu.objects, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.spinner) {
            microbitTextInput.setError(null);
            objectName = adapterView.getItemAtPosition(i).toString();
            if(objectName.equals("Road Mark")){
                speedLimitTextInput.setEnabled(true);
            }
            else{
                speedLimitTextInput.setEnabled(false);
            }
        }else if (spinner.getId() == R.id.roadType) {
            roadName = adapterView.getItemAtPosition(i).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
