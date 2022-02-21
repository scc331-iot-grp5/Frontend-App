package com.google.codelabs.mdc.java.shrine;


import android.os.Build;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.codelabs.mdc.java.shrine.network.Conditions;
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
import android.widget.LinearLayout;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.codelabs.mdc.java.shrine.network.Device;
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

public class StepTwo extends Fragment implements OnItemSelectedListener, MicrobitViewAdapter.ItemClickListener {



    ArrayList<Conditions> givenCons = new ArrayList<>();

    MicrobitViewAdapter adapter;
    ArrayList<Device> devices = new ArrayList<>();
    ArrayList<Integer> l = new ArrayList<>();
    MicrobitViewAdapter.ItemClickListener x;

    RequestQueue queue;

    String zone;
    String object;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.x = this;
        setHasOptionsMenu(true);
    }

    StepTwo(ArrayList<Conditions> X){
        this.givenCons = X;
    }
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), "pk.eyJ1IjoiY2FtZXJvbnB1Z2gyODIiLCJhIjoiY2t6OHdoNG1jMHp3dTJ2bXU4M2kzYmV3bCJ9.RMjNS0Ll5wPTkLt27txUsg");
        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.step_two, container, false);
        queue = Volley.newRequestQueue(getContext());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
            setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
            setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
        }
        //Menu menu = view.findViewById(R.id.menu);


        // Set up the toolbar
        setUpToolbar(view);

        ArrayList<String> zones = new ArrayList<>(); //TEMP
        zones.add("1");
        zones.add("2");
        zones.add("3") ;

        Spinner spinner = (Spinner) view.findViewById(R.id.zoneGroups);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapterTwo = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, zones);
        adapterTwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterTwo);

        Spinner spinnerTwo = (Spinner) view.findViewById(R.id.ObjectGroups);
        spinnerTwo.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapterListTwo = ArrayAdapter.createFromResource(getContext(),
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapterListTwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTwo.setAdapter(adapterListTwo);

        getMData(view);

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.selectMicrobits);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new MicrobitViewAdapter(getContext(),devices,1,view,R.layout.m_row);
        adapter.setClickListener(x);

        recyclerView.setAdapter(adapter);



        MaterialButton next = view.findViewById(R.id.nextConditions);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Conditions X = givenCons.get(0);
                X.setZoneGroup(zone);
                X.setObjectGroup(object);
                X.setMicrobits(l);
                X.setAsignedDevices(true);

                ((NavigationHost) getActivity()).navigateTo(new StepOne(givenCons), false); // Navigate to the next Fragment
            }
        });

        return view;
    }
    private void getMData(View view){
        String url = "https://f074-86-4-178-72.ngrok.io/mData2";

        JSONArray json = new JSONArray();
        JSONObject j = new JSONObject();

        try {
            j.put("userID", 0);
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
                                int microID = (int) object1.get("microbitID");
                                String name = (String) object1.get("x");
                                String url = (String) object1.get("name");

                                Device a = new Device(name, microID, url);
                                devices.add(a);
                                adapter.updateList(devices);

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
    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationHost) getActivity()).navigateTo(new StepOne(givenCons), false); // Navigate to the next Fragment

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
        if (spinner.getId() == R.id.zoneGroups){
            zone = adapterView.getItemAtPosition(i).toString();
        }else if (spinner.getId() == R.id.ObjectGroups) {
            object = adapterView.getItemAtPosition(i).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemClick(View view, int position, View big) {
        System.out.println("here");
        if (view.findViewById(R.id.iconTick).getVisibility() == View.GONE) {
            System.out.println('X');
            view.findViewById(R.id.iconTick).setVisibility(View.VISIBLE);
            l.add(adapter.getItem(position));
        } else {
            view.findViewById(R.id.iconTick).setVisibility(View.GONE);
            System.out.println('Y');
            l.remove(Integer.valueOf(adapter.getItem(position)));
        }

    }
}
