package com.google.codelabs.mdc.java.shrine;


import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.google.android.material.button.MaterialButton;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class AddDevice extends Fragment {

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
            View view = inflater.inflate(R.layout.add_device, container, false);
            //Menu menu = view.findViewById(R.id.menu);


            // Set up the toolbar
            setUpToolbar(view);

            Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.planets_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setPrompt("Please select ");
            spinner.setAdapter(adapter);

            MapView mapView = view.findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);

            mapView.getMapAsync(new OnMapReadyCallback(){
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng point) {
                            mapboxMap.clear();
                            Toast.makeText(getContext(), String.format("Location Selected at: %s", point.toString()), Toast.LENGTH_LONG).show();
                            mapboxMap.addMarker(new MarkerOptions()
                                    .position(point)
                                    .title("New Location"));
                        }
                    });
                }
            });



        /*
        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        ProductCardRecyclerViewAdapter adapter = new ProductCardRecyclerViewAdapter(
                ProductEntry.initProductEntryList(getResources()));
        recyclerView.setAdapter(adapter);
        int largePadding = getResources().getDimensionPixelSize(R.dimen.shr_product_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.shr_product_grid_spacing_small);
        recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));
        */
            // Set cut corner background for API 23+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.findViewById(R.id.product_grid).setBackgroundResource(R.drawable.shr_product_grid_background_shape);
            }

            MaterialButton nextButton = view.findViewById(R.id.logout);
            MaterialButton home = view.findViewById(R.id.home);
            MaterialButton addDevice = view.findViewById(R.id.addDevice);

            // Set an error if the password is less than 8 characters.
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false); // Navigate to the next Fragment
                }
            });
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((NavigationHost) getActivity()).navigateTo(new ProductGridFragment(), false); // Navigate to the next Fragment
                }
            });
            addDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((NavigationHost) getActivity()).navigateTo(new AddDevice(), false); // Navigate to the next Fragment
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
