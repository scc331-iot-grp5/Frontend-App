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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Dashboard extends Fragment{

    RequestQueue queue;
    View view;

    int style;
    Dashboard(int style){
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
        view = inflater.inflate(R.layout.dashboard, container, false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
            setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_left));
            setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
        }
        queue = Volley.newRequestQueue(getContext());
        //Menu menu = view.findViewById(R.id.menu);
        // Set up the toolbar
        setUpToolbar(view);

        // Set cut corner background for API 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.product_grid).setBackgroundResource(R.drawable.shr_product_grid_background_shape);
        }

        MaterialButton nextButton = view.findViewById(R.id.logout);
        MaterialButton systemCOnfig = view.findViewById(R.id.SystemConfig);
        MaterialButton domain = view.findViewById(R.id.domain);

        // Set an error if the password is less than 8 characters.
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false); // Navigate to the next Fragment
            }
        });
        systemCOnfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new Settings(style), false); // Navigate to the next Fragment
            }
        });
        domain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // ((NavigationHost) getActivity()).navigateTo(new MapViewFragment(style), false); // Navigate to the next Fragment
            }
        });



        return view;
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
}
