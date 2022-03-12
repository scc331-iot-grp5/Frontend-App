package com.google.codelabs.mdc.java.shrine;


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
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

public class Settings extends Fragment{

    String connection = "https://5f6b-148-88-245-64.ngrok.io";


    RequestQueue queue;

    boolean orangeA = false;
    boolean redA= false;
    boolean greenA= false;
    boolean roseA= false;
    boolean purpleA= false;

    CheckBox orange;
    CheckBox gold ;
    CheckBox red ;
    CheckBox green ;
    CheckBox purple ;

    int style;
    public Settings(int style){
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
        View view = inflater.inflate(R.layout.settings, container, false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
            setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
            setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
        }
        queue = Volley.newRequestQueue(getContext());
        setUpToolbar(view);

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

         orange = view.findViewById(R.id.orange);
         gold = view.findViewById(R.id.rose);
         red = view.findViewById(R.id.red);
         green = view.findViewById(R.id.green);
         purple = view.findViewById(R.id.purple);

        orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orangeA = !orangeA;
                gold.setChecked(false);
                red.setChecked(false);
                green.setChecked(false);
                purple.setChecked(false);
                redA= false;
                greenA= false;
                roseA= false;
                purpleA= false;


            }
        });

        gold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roseA = !roseA;
                orange.setChecked(false);
                red.setChecked(false);
                green.setChecked(false);
                purple.setChecked(false);
                redA= false;
                greenA= false;
                orangeA= false;
                purpleA= false;

            }
        });

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redA = !redA;
                gold.setChecked(false);
                orange.setChecked(false);
                green.setChecked(false);
                purple.setChecked(false);
                roseA= false;
                greenA= false;
                orangeA= false;
                purpleA= false;

            }
        });
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                greenA = !greenA;
                orange.setChecked(false);
                red.setChecked(false);
                purple.setChecked(false);
                gold.setChecked(false);
                roseA= false;
               redA= false;
                orangeA= false;
                purpleA= false;

            }
        });
        purple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purpleA = !purpleA;
                gold.setChecked(false);
                red.setChecked(false);
                green.setChecked(false);
                orange.setChecked(false);
                roseA= false;
                redA= false;
                orangeA= false;
                greenA= false;
            }
        });

        getSystemStyle();

        return view;
    }
    private void getSystemStyle()
    {
        JSONArray j = new JSONArray();
        JSONObject json = new JSONObject();

        try {
            json.put("sss","ad");
            j.put(json);
        }
        catch(Exception e){}

        String url = connection + "/colorSettings";

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, j, new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject object1 = response.getJSONObject(0);
                            if ((int)(object1.get("styleID")) == 2000016) {
                                roseA = true;
                                gold.setChecked(true);
                            }
                            else if ((int)(object1.get("styleID")) == 2000552) {
                                orangeA = true;
                                orange.setChecked(true);
                            }
                            else if ((int)(object1.get("styleID")) == 3) {
                                greenA = true;
                                green.setChecked(true);
                            }
                            else if ((int)(object1.get("styleID")) == 4) {
                                purpleA = true;
                                purple.setChecked(true);
                            }
                            else if ((int)(object1.get("styleID")) == 5) {
                               redA = true;
                               red.setChecked(true);
                            }
                        }catch(Exception e){}

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    private void sendColorToDB()
    {
        JSONArray j = new JSONArray();
        JSONObject json = new JSONObject();
        int styleID = 2000016;
        if(roseA)
            styleID = 2000016;
        else if(orangeA)
            styleID = 2000552;
        else if(greenA)
            styleID = 3;
        else if(purpleA)
            styleID = 4;
        else if(redA)
            styleID = 5;


        try {
            json.put("styleID",styleID);
            j.put(json);
        }
        catch(Exception e){}

        String url = connection + "/setColor";

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.POST, url, j, new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {

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
}
