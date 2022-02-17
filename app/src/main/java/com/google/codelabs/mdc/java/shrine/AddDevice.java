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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

import org.json.JSONObject;

import java.util.regex.Pattern;

public class AddDevice extends Fragment implements OnItemSelectedListener {

        public String objectName = "";
        public String roadName = "";
        TextInputLayout speedLimitTextInput;
        TextInputLayout microbitTextInput;
        LatLng gPoint;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(
                @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Mapbox.getInstance(getContext(), "pk.eyJ1IjoiY2FtZXJvbnB1Z2gyODIiLCJhIjoiY2t6OHdoNG1jMHp3dTJ2bXU4M2kzYmV3bCJ9.RMjNS0Ll5wPTkLt27txUsg");
            // Inflate the layout for this fragment with the ProductGrid theme
            View view = inflater.inflate(R.layout.add_device, container, false);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
                setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
                setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
            }
            //Menu menu = view.findViewById(R.id.menu);


            // Set up the toolbar
            setUpToolbar(view);

            Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
            spinner.setOnItemSelectedListener(this);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.planets_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            Spinner spinnerTwo = (Spinner) view.findViewById(R.id.roadType);
            spinnerTwo.setOnItemSelectedListener(this);
            ArrayAdapter<CharSequence> adapterTwo = ArrayAdapter.createFromResource(getContext(),
                    R.array.Road_array, android.R.layout.simple_spinner_item);
            adapterTwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTwo.setAdapter(adapterTwo);

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

            speedLimitTextInput = view.findViewById(R.id.speedLimit);
            final TextInputEditText speedLimitEditText = view.findViewById(R.id.speedLimitInput);

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
                                        json.put("speedLimit", s);
                                        json.put("lat", lat);
                                        json.put("long", lon);
                                    } catch (Exception e) {
                                    }

                                    String url = "https://f074-86-4-178-72.ngrok.io/addDevice";

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

            return view;
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
