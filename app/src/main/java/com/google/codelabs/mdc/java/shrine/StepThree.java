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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class StepThree extends Fragment implements OnItemSelectedListener{

    public Rules newRule;
    String event;

    StepThree(Rules x){
        this.newRule = x;
    }
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
        View view = inflater.inflate(R.layout.step_three, container, false);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
            setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
            setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
        }
        //Menu menu = view.findViewById(R.id.menu);


        // Set up the toolbar
        setUpToolbar(view);

        Spinner spinner = (Spinner) view.findViewById(R.id.Events);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapterList = ArrayAdapter.createFromResource(getContext(),
                R.array.events, android.R.layout.simple_spinner_item);
        adapterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterList);

        TextInputEditText nameInput = view.findViewById(R.id.messageText);
        TextInputEditText message = view.findViewById(R.id.ruleNameText);
        TextInputEditText url = view.findViewById(R.id.urlNameText);


        MaterialButton save = view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject json = new JSONObject();
                JSONArray array = new JSONArray();

                for (int i = 0; i < newRule.getAllCons().size(); i++){
                    JSONArray arrayBigger = new JSONArray();
                    arrayBigger.put(newRule.getAllCons().get(i).getZoneGroup());
                    arrayBigger.put(newRule.getAllCons().get(i).getObjectGroup());
                    for (int a = 0; a<newRule.getAllCons().get(i).getMicrobits().size(); a++){
                        arrayBigger.put(newRule.getAllCons().get(i).getMicrobits().get(a).toString());
                    }
                    arrayBigger.put(newRule.getAllCons().get(i).getName());
                    arrayBigger.put(newRule.getAllCons().get(i).getValue());
                    arrayBigger.put(newRule.getAllCons().get(i).getOperator());

                    array.put(arrayBigger);
                }

                try {
                    json.put("Event Type", event);
                    json.put("Rule Name", nameInput.getText().toString());
                    json.put("Message",message.getText().toString() );
                    json.put("Rule Image", url.getText().toString());
                    json.put("Conditions", array);
                } catch (Exception e) {
                }

                String url = "https://f074-86-4-178-72.ngrok.io/addRule";

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

                ((NavigationHost) getActivity()).navigateTo(new ViewAllRules(), false);
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
                ((NavigationHost) getActivity()).navigateTo(new StepOne(newRule), false); // Navigate to the next Fragment

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
        if (spinner.getId() == R.id.Events) {
            event = adapterView.getItemAtPosition(i).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
