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
import com.google.codelabs.mdc.java.shrine.network.Rules;
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
    Rules newRule = new Rules();
    ArrayList<Conditions> conditions = new ArrayList<>();
    ArrayList<Conditions> conditionsToPass = new ArrayList<>();
    String connection = "https://6e66-148-88-245-146.ngrok.io";
    ArrayList<Conditions> temp ;

    RuleAdapter.ItemClickListener x;
    String conditionsA;

    ArrayList<Conditions> conWithAll = new ArrayList<>();

    boolean anyConditionIsFalse = true;
    int style;

    StepOne(int style){
        this.style = style;
    }
    StepOne(ArrayList<Conditions> x,int style){
        this.conWithAll = x;
        this.style = style;
    }

    StepOne(Rules x, int style){
        this.newRule = x;
        this.style = style;
    }


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


        if(conWithAll.isEmpty() && newRule.isItEmpty()) {
            adapter = new RuleAdapter(getContext(), conditions);
            temp = conditions;
        }
        else if(conWithAll.isEmpty()) {
            adapter = new RuleAdapter(getContext(), newRule.getAllCons());
            temp = newRule.getAllCons();
        }
        else if(newRule.isItEmpty()) {
            adapter = new RuleAdapter(getContext(), conWithAll);
            temp = conWithAll;
        }

        adapter.setClickListener(x);
        adapter.setSpinnerListener(this);
        recyclerView.setAdapter(adapter);

        LinearLayout warning = view.findViewById(R.id.warningLabel);

        MaterialButton add = view.findViewById(R.id.addConditions);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Conditions X = new Conditions(conditionsA);
                temp.add(X);
                adapter.updateList(temp);
                if(anyConditionIsFalse){
                    warning.setVisibility(View.VISIBLE);
                }
            }
        });

        MaterialButton next = view.findViewById(R.id.nextConditions);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Rules a = new Rules(temp);
                ((NavigationHost) getActivity()).navigateTo(new StepThree(a,style), false); // Navigate to the next Fragment
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
                ((NavigationHost) getActivity()).navigateTo(new ViewAllRules(style), false); // Navigate to the next Fragment

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
        Conditions X = new Conditions(adapter.getName(position),adapter.getListSymbol(position), adapter.getValue(position), adapter.getZone(position),
                adapter.getObject(position), adapter.getMicrobits(position),adapter.getTrue(position));

        conditionsToPass.add(X);
        for (int i = 0; i < adapter.getItemCount(); i++) {
            if(i != position){
                Conditions ass = new Conditions(adapter.getName(i),adapter.getListSymbol(i), adapter.getValue(i), adapter.getZone(i),
                        adapter.getObject(i), adapter.getMicrobits(i),adapter.getTrue(i));
                conditionsToPass.add(ass);
            }
        }

        ((NavigationHost) getActivity()).navigateTo(new StepTwo(conditionsToPass,style), false); // Navigate to the next Fragment
    }
}
