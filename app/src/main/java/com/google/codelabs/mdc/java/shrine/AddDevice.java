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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class AddDevice extends Fragment implements OnItemSelectedListener {

        public String objectName = "";
        TextInputLayout microbitTextInput;
        LatLng gPoint;

        String connection = "https://5f6b-148-88-245-64.ngrok.io";
        RequestQueue queue;
        Spinner spinner;
        ArrayList<String> a = new ArrayList<>();

        int style;
        AddDevice(int style){
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
            Mapbox.getInstance(getContext(), "pk.eyJ1IjoiY2FtZXJvbnB1Z2gyODIiLCJhIjoiY2t6OHdoNG1jMHp3dTJ2bXU4M2kzYmV3bCJ9.RMjNS0Ll5wPTkLt27txUsg");
            // Inflate the layout for this fragment with the ProductGrid theme
            View view = inflater.inflate(R.layout.add_device, container, false);
            queue = Volley.newRequestQueue(getContext());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
                setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
                setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
            }
            //Menu menu = view.findViewById(R.id.menu);
            // Set up the toolbar
            setUpToolbar(view);

            spinner = (Spinner) view.findViewById(R.id.spinner);
            spinner.setOnItemSelectedListener(this);

            getObjects();

            MapView mapView = view.findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);

            mapView.getMapAsync(new OnMapReadyCallback(){
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
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


            MaterialButton add = view.findViewById(R.id.add);
            microbitTextInput = view.findViewById(R.id.microbitText);
            final TextInputEditText microbitEditText = view.findViewById(R.id.microbitTextInput);
            final TextInputEditText nameInput = view.findViewById(R.id.microbitTextInputName);

            // Set an error if the password is less than 8 characters.
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
                                .setPositiveButton(R.string.d_add, (dialog, which) -> {
                                    JSONObject json = new JSONObject();

                                    Double lat = 0.0;
                                    Double lon = 0.0;
                                    if (gPoint != null) {
                                        lat = gPoint.getLatitude();
                                        lon = gPoint.getLongitude();
                                    }
                                    try {
                                        json.put("object", objectName);
                                        json.put("microbitID", microbitEditText.getText().toString());
                                        json.put("name", nameInput.getText().toString());
                                        json.put("lat", lat);
                                        json.put("long", lon);

                                    } catch (Exception e) {
                                    }

                                    String url = connection + "/addDevice";

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

            // Clear the error
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

            return view;
        }
    private void getObjects(){
        String url = connection +  "/objects";

        JSONArray json = new JSONArray();
        JSONObject j = new JSONObject();

        try {
            j.put("id",0);
            json.put(0,j);
        }
        catch(Exception e){}

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, json, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            System.out.println("Refresh");
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object1 = response.getJSONObject(i);

                                String names = (String) object1.get("name");
                                a.add(names);
                            }
                            ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, a);
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

        queue.add(jsonObjectRequest);


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
                    ((NavigationHost) getActivity()).navigateTo(new ManDev(style), false); // Navigate to the next Fragment

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
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
