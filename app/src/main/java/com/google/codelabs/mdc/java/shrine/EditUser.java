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

public class EditUser extends Fragment implements  MicrobitViewAdapter.ItemClickListener, MicrobitViewAdapter.ItemLongClickListener{

    MicrobitViewAdapter adapter;
    ArrayList<Device> devices = new ArrayList<>();
    ArrayList<String> dev = new ArrayList<>();
    MicrobitViewAdapter.ItemClickListener x;
    MicrobitViewAdapter.ItemLongClickListener y;

    public int userid;
    public int m;
    RequestQueue queue;
    int userID;
    String name;
    String urlP;
    String background;
    private ImageRequester imageRequester;

    public EditUser(int userid){
        this.userid = userid;
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
        View view = inflater.inflate(R.layout.edit_user, container, false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
            setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
            setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
        }
        queue = Volley.newRequestQueue(getContext());
        imageRequester = ImageRequester.getInstance();
        setUpToolbar(view);
        getData(view);
        getMData(view);
        getMicrobitData(view);

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.listMicrobits);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new MicrobitViewAdapter(getContext(),devices,userid,view);
        adapter.setClickListener(x);
        adapter.setLongClickListener(y);
        recyclerView.setAdapter(adapter);


        Button popupButton = view.findViewById(R.id.assignMicrobit);
        popupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                PopUpClass popUpClass = new PopUpClass();
                popUpClass.showPopupWindow(v,getContext(),dev,Integer.toString(userid));
            }
        });

        return view;
    }


    private void getData(View view){
        JSONObject json = new JSONObject();

        try {
            json.put("userID",userid);
        }
        catch(Exception e){}

        String url = "https://f074-86-4-178-72.ngrok.io/userIndividual";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, json, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                                userID = (int) response.get("userID");
                                name = (String) response.get("name");
                                urlP = (String) response.get("url");
                                background = (String) response.get("background");

                                TextView nameBox =  view.findViewById(R.id.nameBox);
                                NetworkImageView x = view.findViewById(R.id.background);
                                TextView userBox =  view.findViewById(R.id.uid);
                                nameBox.setText(name);
                                userBox.setText(Integer.toString(userID));
                                //nameBox.setTextColor(getResources().getColor(R.color.loginPageBackgroundColor));

                                imageRequester.setImageFromUrl(view.findViewById(R.id.profile), urlP);
                                imageRequester.setImageFromUrl(view.findViewById(R.id.background), background);
                                x.setScaleType(NetworkImageView.ScaleType.CENTER_CROP);

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
    private void getMData(View view){
        String url = "https://f074-86-4-178-72.ngrok.io/mData";

        JSONArray json = new JSONArray();
        JSONObject j = new JSONObject();

        try {
            j.put("userID", userid);
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
                                m = microID;
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
    private void getMicrobitData(View view){
        String url = "https://f074-86-4-178-72.ngrok.io/microbits";

        JSONArray json = new JSONArray();
        JSONObject j = new JSONObject();

        try {
            j.put("microbitID", m);
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
                                String type = (String) object1.get("name");

                                String str = Integer.toString(microID)+":"+type;

                                dev.add(str);
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
                ((NavigationHost) getActivity()).navigateTo(new AssUser(), false); // Navigate to the next Fragment

            }
        });
    }


    @Override
    public void onItemClick(View view, int position, View big) {

    }

    @Override
    public void onItemLongClick(View view, int position) {
        System.out.println("Im here");
        Toast.makeText(view.getContext(), "Position is " + position, Toast.LENGTH_SHORT).show();
    }
}
