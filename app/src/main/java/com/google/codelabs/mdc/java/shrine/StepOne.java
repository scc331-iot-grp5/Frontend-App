package com.google.codelabs.mdc.java.shrine;


import android.os.Build;
import android.os.Bundle;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class StepOne extends Fragment implements OnItemSelectedListener, RuleAdapter.ItemClickListener {

    RuleAdapter adapter;
    ArrayList<Conditions> conditions = new ArrayList<>();
    RuleAdapter.ItemClickListener x;
    String conditionsA;
    boolean anyConditionIsFalse = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.x = this;
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), "pk.eyJ1IjoiY2FtZXJvbnB1Z2gyODIiLCJhIjoiY2t6OHdoNG1jMHp3dTJ2bXU4M2kzYmV3bCJ9.RMjNS0Ll5wPTkLt27txUsg");
        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.step_one, container, false);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
            setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
            setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
        }
        //Menu menu = view.findViewById(R.id.menu);


        // Set up the toolbar
        setUpToolbar(view);

        Spinner spinner = (Spinner) view.findViewById(R.id.conditions);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapterList = ArrayAdapter.createFromResource(getContext(),
                R.array.cond, android.R.layout.simple_spinner_item);
        adapterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterList);

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rvRules);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new RuleAdapter(getContext(),conditions);
        adapter.setClickListener(x);
        adapter.setSpinnerListener(this);
        recyclerView.setAdapter(adapter);


        LinearLayout warning = view.findViewById(R.id.warningLabel);

        MaterialButton add = view.findViewById(R.id.addConditions);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Conditions X = new Conditions(conditionsA);
                conditions.add(X);
                adapter.updateList(conditions);
                if(anyConditionIsFalse){
                    warning.setVisibility(View.VISIBLE);
                }
            }
        });

        MaterialButton next = view.findViewById(R.id.nextConditions);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //need to add the extra attributes later
                //Conditions X = new Conditions(adapter.getCondition(),adapter.getListSymbol(), adapter.getValue());
                //ArrayList<Conditions> temp = new ArrayList<>();
                //temp.add(X);
                //((NavigationHost) getActivity()).navigateTo(new StepThree(temp), false); // Navigate to the next Fragment
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

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationHost) getActivity()).navigateTo(new ViewAllRules(), false); // Navigate to the next Fragment

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
        if (spinner.getId() == R.id.conditions) {
            conditionsA = adapterView.getItemAtPosition(i).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemClick(View view, int position) {
        Conditions X = new Conditions(adapter.getName(position),adapter.getListSymbol(position), adapter.getValue(position));
        ((NavigationHost) getActivity()).navigateTo(new StepTwo(X), false); // Navigate to the next Fragment
    }
}