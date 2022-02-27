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
import androidx.transition.TransitionInflater;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class EditMicrobit extends Fragment implements OnItemSelectedListener {

    int microbitID;

    TextInputLayout microbitTextInput;
    LatLng gPoint;
    String name;
    String type;

    int id ;

    Double x ;
    Double y ;
    LatLng newPoint;
    Spinner spinner;
    public String objectName = "";


    ArrayList<String> a = new ArrayList<>();

    String connection = "https://6e66-148-88-245-146.ngrok.io";


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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
            setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
            setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
        }
        //Menu menu = view.findViewById(R.id.menu);
        getObjects();
        getData(view,savedInstanceState);

        // Set up the toolbar
        setUpToolbar(view);

        spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

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
        final TextInputEditText nameText = view.findViewById(R.id.microbitTextInputName);

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
                            .setMessage("Are you sure you want to update this device?")
                            .setPositiveButton(R.string.update, (dialog, which) -> {
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
                                    json.put("name", nameText.getText().toString());
                                    json.put("lat", lat);
                                    json.put("long", lon);
                                } catch (Exception e) {
                                    System.out.println(e);
                                }

                                String url =  connection + "/updateDevice";

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

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);


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
        JSONArray j = new JSONArray();

        try {
            json.put("mID",microbitID);
            j.put(json);
        }
        catch(Exception e){}

        String url = connection + "/microbitIndividual";

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.POST, url, j, new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject object1 = response.getJSONObject(0);
                             name = (String) object1.get("name");

                             id = (int) object1.get("id");

                            try {
                                 x = (Double) object1.get("x");
                                 y = (Double) object1.get("y");
                                 newPoint = new LatLng(x,y);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            TextInputEditText idBox =  view.findViewById(R.id.microbitTextInput2);
                            TextInputEditText nameBox =  view.findViewById(R.id.microbitTextInputName);

                            nameBox.setText(name);
                            idBox.setText(Integer.toString(id));

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
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
