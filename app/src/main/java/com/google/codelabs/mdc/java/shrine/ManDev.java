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

public class ManDev extends Fragment implements MicrobitViewAdapter.ItemClickListener, MicrobitViewAdapter.ItemLongClickListener{

    MicrobitViewAdapter adapter;
    ArrayList<Device> devices = new ArrayList<>();
    ArrayList<String> dev = new ArrayList<>();
    MicrobitViewAdapter.ItemClickListener x;
    MicrobitViewAdapter.ItemLongClickListener y;

    String connection = "https://5f6b-148-88-245-64.ngrok.io";


    RequestQueue queue;
    View view;

    int style;
    ManDev(int style){
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

        this.x = this;
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        view = inflater.inflate(R.layout.all_dev, container, false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
            setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_left));
            setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
        }
        queue = Volley.newRequestQueue(getContext());
        //Menu menu = view.findViewById(R.id.menu);
        // Set up the toolbar
        setUpToolbar(view);
        getMData(view);

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.devView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new MicrobitViewAdapter(getContext(),devices,0,view,R.layout.microbit_row);
        adapter.setClickListener(x);
        adapter.setLongClickListener(y);
        recyclerView.setAdapter(adapter);

        TextInputEditText searchField = view.findViewById(R.id.search_edit_text);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });

        // Set cut corner background for API 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.product_grid).setBackgroundResource(R.drawable.shr_product_grid_background_shape);
        }

        MaterialButton nextButton = view.findViewById(R.id.logout);
        MaterialButton addDevice = view.findViewById(R.id.addDevice);
        MaterialButton map = view.findViewById(R.id.map);
        MaterialButton user = view.findViewById(R.id.users);
        MaterialButton rules = view.findViewById(R.id.rules);
        MaterialButton anal = view.findViewById(R.id.a);
        MaterialButton chats = view.findViewById(R.id.chats);

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

        FloatingActionButton fab = view.findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testTwo();
                ((NavigationHost) getActivity()).navigateTo(new AddDevice(style), false); // Navigate to the next Fragment

            }
        });

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

        return view;
    }
    //get all data not with userID
    private void getMData(View view){
        String url = connection + "/mData2";

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
    void filter(String text){
        ArrayList<Device> temp = new ArrayList();
        for(Device d: devices){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getName().toLowerCase().contains(text.toLowerCase()) || Integer.toString(d.getMicrobitID()).contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
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

    private void test(View view){
        view.findViewById(R.id.backdrop).setVisibility(View.GONE);
    }
    private void testTwo(){
        view.findViewById(R.id.backdrop).setVisibility(View.GONE);
    }
    @Override
    public void onItemClick(View view, int position, View big) {
        test(big);
        ((NavigationHost) getActivity()).navigateTo(new EditMicrobit(adapter.getItem(position),style), false); // Navigate to the next Fragment
        //Toast.makeText(getContext(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}
