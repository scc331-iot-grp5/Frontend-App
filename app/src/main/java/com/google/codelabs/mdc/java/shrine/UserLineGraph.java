package com.google.codelabs.mdc.java.shrine;


import android.graphics.Color;
import android.graphics.DashPathEffect;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.codelabs.mdc.java.shrine.network.Conditions;
import com.google.codelabs.mdc.java.shrine.network.Device;
import com.google.codelabs.mdc.java.shrine.network.ImageRequester;
import com.google.codelabs.mdc.java.shrine.network.ProductEntry;
import com.google.codelabs.mdc.java.shrine.network.Reading;
import com.google.codelabs.mdc.java.shrine.ReadingsAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class UserLineGraph extends Fragment implements OnItemSelectedListener{

    View view;
    RequestQueue queue;
    CalendarPopUpTwo calenderPopUp;
    LineChart volumeReportChart;
    String searchType;
    XAxis xAxis;
    YAxis leftAxis;
    String before = " ";
    String after = " ";

    String connection = "https://6e66-148-88-245-146.ngrok.io";

    int userID;
    String microbitID;

    Spinner spinner;

    ArrayList<Integer> a = new ArrayList<>();
    int style;
    public UserLineGraph(int userID, int style){
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
        view = inflater.inflate(R.layout.user_line_graph, container, false);
        queue = Volley.newRequestQueue(getContext());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
            setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_left));
            setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
        }
        // Set up the toolbar
        setUpToolbar(view);
        Button popupButton = view.findViewById(R.id.selectDate);
        popupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                calenderPopUp = new CalendarPopUpTwo();
                calenderPopUp.showPopupWindow(v,getContext());
            }
        });
        getMicrobits();

        spinner = (Spinner) view.findViewById(R.id.usersMicrobits);
        spinner.setOnItemSelectedListener(this);

        MaterialButton add = view.findViewById(R.id.addConditions);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    before = calenderPopUp.getDateBefore();
                    after = calenderPopUp.getDateAfter();
                }catch (Exception e){}

                before = calenderPopUp.getDateBefore();
                after = calenderPopUp.getDateAfter();
                getTempWithParameters(before, after, microbitID);

            }
        });

        volumeReportChart = view.findViewById(R.id.reportingChart);
        volumeReportChart.setTouchEnabled(true);
        volumeReportChart.setPinchZoom(true);

        LimitLine ll1 = new LimitLine(30f,"Title");
        ll1.setLineColor(getResources().getColor(R.color.colorPrimaryDark));
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(35f, "");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);

        xAxis = volumeReportChart.getXAxis();
        leftAxis = volumeReportChart.getAxisLeft();
        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setPosition(position);

        volumeReportChart.getDescription().setEnabled(true);
        Description description = new Description();

        description.setText("Week");
        description.setTextSize(15f);

        List<String> dates = new ArrayList<>();

        List<Double> number = new ArrayList<>();

        dates.add("2022-02-01");
        dates.add("2022-02-02");
        dates.add("2022-02-03");


        number.add(1.0);
        number.add(2.0);
        number.add(3.1);

        xAxis.setValueFormatter(new ClaimsXAxisValueFormatter(dates));
        leftAxis.setValueFormatter(new ClaimsYAxisValueFormatter());
        renderData(dates, number);

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
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        microbitID = adapterView.getItemAtPosition(i).toString();
    }
    public void getTempWithParameters(String b, String a, String id){
        String url = connection + "/tempGraphRange";

        JSONArray json = new JSONArray();
        JSONObject j = new JSONObject();

        try {
            j.put("mID",id);
            j.put("before", b);
            j.put("after", a);
            json.put(0,j);
        }
        catch(Exception e){}

        List<String> dates = new ArrayList<>();

        List<Double> number = new ArrayList<>();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.POST, url, json, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            System.out.println("Refresh");
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object1 = response.getJSONObject(i);

                                String date = (String) object1.get("date");
                                double tot = (double) object1.get("temp");
                                System.out.println(date);
                                System.out.println(tot);
                                number.add(tot);
                                dates.add(date);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        renderData(dates, number);


                        //GET SPEED FROM DB UPDATE theSPEED
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        queue.add(jsonObjectRequest);
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public class ClaimsXAxisValueFormatter extends ValueFormatter {

        List<String> datesList;

        public ClaimsXAxisValueFormatter(List<String> arrayOfDates) {
            this.datesList = arrayOfDates;
        }
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
/*
Depends on the position number on the X axis, we need to display the label, Here, this is the logic to convert the float value to integer so that I can get the value from array based on that integer and can convert it to the required value here, month and date as value. This is required for my data to show properly, you can customize according to your needs.
*/
            Integer position = Math.round(value);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");

            if (value > 1 && value < 2) {
                position = 0;
            } else if (value > 2 && value < 3) {
                position = 1;
            } else if (value > 3 && value < 4) {
                position = 2;
            } else if (value > 4 && value <= 5) {
                position = 3;
            }
            if (position < datesList.size())
                return sdf.format(new Date((Utils.getDateInMilliSeconds(datesList.get(position), "yyyy-MM-dd"))));
            return "";
        }
    }
    public class ClaimsYAxisValueFormatter extends ValueFormatter {

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return value + " ";
        }
    }
    public void renderData(List<String> dates, List<Double> allAmounts) {

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("1");
        xAxisLabel.add("7");
        xAxisLabel.add("14");
        xAxisLabel.add("21");
        xAxisLabel.add("28");
        xAxisLabel.add("30");

        XAxis xAxis = volumeReportChart.getXAxis();
        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setPosition(position);
        xAxis.enableGridDashedLine(2f, 7f, 0f);
        xAxis.setAxisMaximum(5f);
        xAxis.setAxisMinimum(0f);
        xAxis.setLabelCount(6, true);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(7f);
        xAxis.setLabelRotationAngle(315f);

        xAxis.setValueFormatter(new ClaimsXAxisValueFormatter(dates));

        xAxis.setCenterAxisLabels(true);


        xAxis.setDrawLimitLinesBehindData(true);


        LimitLine ll2 = new LimitLine(35f, "");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setLineColor(Color.parseColor("#FFFFFF"));

        xAxis.removeAllLimitLines();
        xAxis.addLimitLine(ll2);


        YAxis leftAxis = volumeReportChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        //leftAxis.addLimitLine(ll1);
        //leftAxis.addLimitLine(ll2);

        leftAxis.setAxisMaximum((float) (Collections.max(allAmounts) + 10f));
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);
        //XAxis xAxis = mBarChart.getXAxis();
        leftAxis.setValueFormatter(new ClaimsYAxisValueFormatter());

        volumeReportChart.getDescription().setEnabled(true);
        Description description = new Description();
        // description.setText(UISetters.getFullMonthName());//commented for weekly reporting
        description.setText("Week");
        description.setTextSize(15f);
        volumeReportChart.getDescription().setPosition(0f, 0f);
        volumeReportChart.setDescription(description);
        volumeReportChart.getAxisRight().setEnabled(false);

        //setData()-- allAmounts is data to display;
        setDataForWeeksWise(allAmounts);

    }
    private void setDataForWeeksWise(List<Double> amounts) {

        ArrayList<Entry> values = new ArrayList<>();
        try {
            values.add(new Entry(1, amounts.get(0).floatValue()));
            values.add(new Entry(2, amounts.get(1).floatValue()));
            values.add(new Entry(3, amounts.get(2).floatValue()));
            values.add(new Entry(4, amounts.get(3).floatValue()));
            values.add(new Entry(5, amounts.get(4).floatValue()));
            values.add(new Entry(6, amounts.get(5).floatValue()));
            values.add(new Entry(7, amounts.get(6).floatValue()));
            values.add(new Entry(8, amounts.get(7).floatValue()));
            values.add(new Entry(9, amounts.get(8).floatValue()));
        }catch (Exception e){}


        LineDataSet set1;
        if (volumeReportChart.getData() != null &&
                volumeReportChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) volumeReportChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            volumeReportChart.getData().notifyDataChanged();
            volumeReportChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "Total");
            set1.setDrawCircles(true);
            set1.enableDashedLine(10f, 0f, 0f);
            set1.enableDashedHighlightLine(10f, 0f, 0f);
            set1.setColor(getResources().getColor(R.color.red));
            set1.setCircleColor(getResources().getColor(R.color.green));
            set1.setLineWidth(2f);//line size
            set1.setCircleRadius(5f);
            set1.setDrawCircleHole(true);
            set1.setValueTextSize(10f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(5f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(5.f);

            set1.setDrawValues(true);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);

            volumeReportChart.setData(data);
        }
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
}
