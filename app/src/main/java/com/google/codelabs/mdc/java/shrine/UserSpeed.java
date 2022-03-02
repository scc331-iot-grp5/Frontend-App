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

public class UserSpeed extends Fragment  implements OnItemSelectedListener{

    View view;
    SpeedometerView Speed;
    float theSpeed = 100f;
    RequestQueue queue;
    TextInputEditText s;
    String connection = "https://6e66-148-88-245-146.ngrok.io";

    int userID;
    String microbitID;
    Spinner spinner;

    ArrayList<Integer> a = new ArrayList<>();
    int style;
    public UserSpeed(int userID, int style){
        this.userID = userID;
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
        view = inflater.inflate(R.layout.user_speed, container, false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
            setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_left));
            setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
        }

        // Set up the toolbar
        setUpToolbar(view);
        queue = Volley.newRequestQueue(getContext());
        Speed = (SpeedometerView)view.findViewById(R.id.speedometer);
        Speed.setLabelConverter(new SpeedometerView.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

        getMicrobits();

        spinner = (Spinner) view.findViewById(R.id.usersMicrobits);
        spinner.setOnItemSelectedListener(this);


        MaterialButton hm = view.findViewById(R.id.save);

        // Set an error if the password is less than 8 characters.
        hm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });

        s = view.findViewById(R.id.urlNameText);
// configure value range and ticks
        Speed.setMaxSpeed(100);
        Speed.setMajorTickStep(25);
        Speed.setMinorTicks(0);
// Configure value range colors
        Speed.addColoredRange(0, 50, Color.GREEN);
        Speed.addColoredRange(50, 75, Color.YELLOW);
        Speed.addColoredRange(75, 100, Color.RED);
        //Speed.setSpeed(theSpeed, 2000, 500);

        //GET SPEED FROM DB UPDATE theSPEED
        return view;
    }

   private void getMicrobits(){
       String url = connection +  "/usersMicrobits";

       JSONArray json = new JSONArray();
       JSONObject j = new JSONObject();

       try {
           j.put("id",userID);
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

                               int microbitID = (int) object1.get("id");
                               a.add(microbitID);


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
    private void getData(){
        String url = connection +  "/speedo";

        JSONArray json = new JSONArray();
        JSONObject j = new JSONObject();

        try {
            j.put("id",microbitID);
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
                                double x = (double) object1.get("speed");
                                theSpeed = (float) (x);
                                Speed.setSpeed(theSpeed, 2000, 500);

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
                ((NavigationHost) getActivity()).navigateTo(new MyAnalytics(userID,style), false); // Navigate to the next Fragment

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
            microbitID = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
