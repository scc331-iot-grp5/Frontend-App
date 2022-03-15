package com.google.codelabs.mdc.java.shrine;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AccGraph extends Fragment {
    LinearLayout chart;
    GraphicalView chartView;
    Connection connect;
    int counter=1;
    XYSeries xySeries1, xySeries2, xySeries3;
    String newName;

    View view;
    int style;
    AccGraph(int style){
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
        view = inflater.inflate(R.layout.acc, container, false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
            setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_left));
            setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
        }

        // Set up the toolbar
        setUpToolbar(view);

        chart = (LinearLayout) view.findViewById(R.id.chart);
        showChart();

        return view;
    }
    private void showChart() {
        XYMultipleSeriesDataset mDataSet= getDataSet();
        XYMultipleSeriesRenderer mRefender= getRefender();
        chartView= ChartFactory.getLineChartView(getContext(), mDataSet, mRefender);
        chart.addView(chartView);
    }
    private XYMultipleSeriesDataset getDataSet() {
        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = (Connection) connectionHelper.conclass();

        try {
            if(connect!=null){
                String query = "SELECT DATE_FORMAT(var.reported_at, '%H%i'), SQRT(POWER(var.accel_x, 2) + POWER(var.accel_y, 2) + POWER(var.accel_z, 2)), d.name\n" +
                        "FROM view_all_readings AS var, devices d, device_types dt,\n" +
                        "     (SELECT MAX(v.reported_at) AS eventDate, device_id from view_all_readings as v group by device_id) as var1\n" +
                        "WHERE var.device_id = d.id\n" +
                        "AND d.type = dt.id\n" +
                        "AND dt.name = 'Car'\n" +
                        "AND var1.device_id = var.device_id\n" +
                        "AND DATE_FORMAT(var.reported_at, '%Y%m%d') = DATE_FORMAT(var1.eventDate, '%Y%m%d')\n" +
                        "AND var.accel_x IS NOT NULL\n" +
                        "AND var.accel_z IS NOT NULL\n" +
                        "AND var.accel_y IS NOT NULL\n" +
                        "ORDER BY d.id, var.reported_at ASC";
                /*String query = "select reading_id, degrees\n" +
                        "from temperature_readings\n" +
                        "where reading_id < 20";*/
                //SQRT(POWER(var.accel_x, 2) + POWER(var.accel_y, 2) + POWER(var.accel_z, 2))
                Statement smt = connect.createStatement();
                ResultSet rs = smt.executeQuery(query);
                XYMultipleSeriesDataset seriesDataset=new XYMultipleSeriesDataset();
                //XYSeries xySeries1=new XYSeries("Temperature of a device on last drive");



                while(rs.next()){
                    String carOwner = rs.getString(3);
                    if (newName == null){
                        xySeries1=new XYSeries(carOwner);
                        newName = carOwner;
                    }
                    else if (!newName.equals(carOwner)){
                        newName = carOwner;
                        counter++;
                        if(counter==2){
                            xySeries2=new XYSeries(carOwner);
                        }
                        else if(counter==3){
                            xySeries3=new XYSeries(carOwner);
                        }
                    }
                    if (counter == 1){
                        double xTime = Double.parseDouble(rs.getString(1));
                        double yAcc = Double.parseDouble(rs.getString(2));
                        //String name = rs.getString(3);
                        xySeries1.add(xTime, yAcc);
                    }
                    else if (counter == 2){
                        double xTime = Double.parseDouble(rs.getString(1));
                        double yAcc = Double.parseDouble(rs.getString(2));
                        //String name = rs.getString(3);
                        xySeries2.add(xTime, yAcc);
                    }
                    else if (counter == 3){
                        double xTime = Double.parseDouble(rs.getString(1));
                        double yAcc = Double.parseDouble(rs.getString(2));
                        //String name = rs.getString(3);
                        xySeries3.add(xTime, yAcc);
                    }
                }
                if (counter == 3){
                    seriesDataset.addSeries(xySeries1);
                    seriesDataset.addSeries(xySeries2);
                    seriesDataset.addSeries(xySeries3);
                }
                else if(counter == 2){
                    seriesDataset.addSeries(xySeries1);
                    seriesDataset.addSeries(xySeries2);
                }
                else if(counter == 1){
                    seriesDataset.addSeries(xySeries1);
                }
                return seriesDataset;
            }
        }
        catch (Exception ex){
            Log.e("Error :", ex.getMessage());
        }

        return null;
    }
    private XYMultipleSeriesRenderer getRefender() {
        XYMultipleSeriesRenderer seriesRenderer=new XYMultipleSeriesRenderer();

        seriesRenderer.setChartTitleTextSize(20);//Set the font size of the chart title (the top text of the chart)
        seriesRenderer.setMargins(new int[] { 40, 30, 30, 20 });//Set the margins, the order is: top left bottom right
        //Axis settings
        seriesRenderer.setAxisTitleTextSize(20);//Set the size of the axis title font
        seriesRenderer.setAxesColor(0xFF5a360c);
        seriesRenderer.setXLabelsColor(0xFF5a360c);
        seriesRenderer.setYAxisMin(0);//Set the starting value of the y axis
        seriesRenderer.setYAxisMax(400);//Set the maximum value of the y axis
        //seriesRenderer.setXAxisMin(0);//Set the x-axis starting value
        //seriesRenderer.setXAxisMax(2400);//Set the maximum value of the x-axis
        seriesRenderer.setXTitle("Time(Hours:Minute)");//Set x axis title
        seriesRenderer.setYTitle("Acceleration");//Set the y-axis title
        //Color setting
        seriesRenderer.setApplyBackgroundColor(true);//Is the background color set by the app
        seriesRenderer.setLabelsColor(0xFF5a360c);//Set label color
        seriesRenderer.setBackgroundColor(Color.argb(100, 255, 231, 224));//Set the background color of the chart
        //Zoom settings
        seriesRenderer.setZoomButtonsVisible(false);//Set whether the zoom button is visible
        seriesRenderer.setZoomEnabled(false); //Whether the chart can be zoomed
        seriesRenderer.setZoomInLimitX(7);

        //Chart mobile settings
        seriesRenderer.setPanEnabled(false);//Can the chart be moved

        //Axis label settings
        seriesRenderer.setLabelsTextSize(20);//Set label font size
        seriesRenderer.setXLabelsAlign(Align.CENTER);
        seriesRenderer.setYLabelsAlign(Align.LEFT);
        seriesRenderer.setXLabels(0);//The number of x-axis labels displayed
        seriesRenderer.addXTextLabel(200, "02:00");//Add text labels for specific x-axis values
        seriesRenderer.addXTextLabel(400, "04:00");
        seriesRenderer.addXTextLabel(600, "06:00");
        seriesRenderer.addXTextLabel(800, "08:00");
        seriesRenderer.addXTextLabel(1200, "12:00");
        seriesRenderer.addXTextLabel(1400, "14:00");
        seriesRenderer.addXTextLabel(1600, "16:00");
        seriesRenderer.addXTextLabel(1800, "18:00");
        seriesRenderer.addXTextLabel(2000, "20:00");
        seriesRenderer.addXTextLabel(2300, "22:00");
        seriesRenderer.addXTextLabel(2400, "24:00");

        seriesRenderer.addYTextLabel(40, "40");
        seriesRenderer.addYTextLabel(80, "80");
        seriesRenderer.addYTextLabel(120, "120");
        seriesRenderer.addYTextLabel(160, "160");
        seriesRenderer.addYTextLabel(200, "200");
        seriesRenderer.addYTextLabel(240, "240");
        seriesRenderer.addYTextLabel(280, "280");
        seriesRenderer.addYTextLabel(320, "320");
        seriesRenderer.addYTextLabel(360, "360");
        seriesRenderer.addYTextLabel(400, "400");

        seriesRenderer.setPointSize(3);//Set the coordinate point size

        seriesRenderer.setMarginsColor(Color.WHITE);//Set the color of the margin space
        seriesRenderer.setClickEnabled(false);
        seriesRenderer.setChartTitle("Last 24 hours acceleration of a device");

        try {
            if(connect!=null){
                String query = "SELECT COUNT(DISTINCT d.name), MIN(DATE_FORMAT(var.reported_at, '%H%i')), MAX(DATE_FORMAT(var.reported_at, '%H%i'))\n" +
                        "FROM view_all_readings AS var, devices d, device_types dt,\n" +
                        "     (SELECT MAX(v.reported_at) AS eventDate, device_id from view_all_readings as v group by device_id) as var1\n" +
                        "WHERE var.device_id = d.id\n" +
                        "AND d.type = dt.id\n" +
                        "AND dt.name = 'Car'\n" +
                        "AND var1.device_id = var.device_id\n" +
                        "AND DATE_FORMAT(var.reported_at, '%Y%m%d') = DATE_FORMAT(var1.eventDate, '%Y%m%d')\n" +
                        "AND var.accel_x IS NOT NULL\n" +
                        "AND var.accel_z IS NOT NULL\n" +
                        "AND var.accel_y IS NOT NULL\n" +
                        "ORDER BY d.id, var.reported_at ASC";
                /*String query = "select reading_id, degrees\n" +
                        "from temperature_readings\n" +
                        "where reading_id < 20";*/
                //SQRT(POWER(var.accel_x, 2) + POWER(var.accel_y, 2) + POWER(var.accel_z, 2))
                Statement smt = connect.createStatement();
                ResultSet rs = smt.executeQuery(query);
                //XYMultipleSeriesDataset seriesDataset=new XYMultipleSeriesDataset();
                //XYSeries xySeries1=new XYSeries("Temperature of a device on last drive");
                //xySeries1=new XYSeries("Temperature of a device on last drive");
                //xySeries2=new XYSeries("Temperature of a device on last drive");
                //xySeries3=new XYSeries("Temperature of a device on last drive");
                while(rs.next()){
                    int xCount = Integer.parseInt(rs.getString(1));
                    seriesRenderer.setXAxisMin(Integer.parseInt(rs.getString(2)) - 200);//Set the starting value of the y axis
                    seriesRenderer.setXAxisMax(Integer.parseInt(rs.getString(3)) + 200);//Set the maximum value of the y axis
                }
                if (counter == 3){
                    XYSeriesRenderer xySeriesRenderer1=new XYSeriesRenderer();
                    xySeriesRenderer1.setAnnotationsColor(0xFF000000);//Set the color of annotations (notes can highlight a certain coordinate)
                    xySeriesRenderer1.setAnnotationsTextAlign(Align.CENTER);//Set the location of the annotation
                    xySeriesRenderer1.setAnnotationsTextSize(15);//Set the size of the note text
                    xySeriesRenderer1.setColor(0xFF0c125a);//The color of the graph or line representing the set of data
                    xySeriesRenderer1.setLineWidth(4);
                    xySeriesRenderer1.setDisplayChartValues(false);//Set whether to display the y-axis coordinate value of the coordinate point
                    xySeriesRenderer1.setChartValuesTextSize(15);
                    seriesRenderer.addSeriesRenderer(xySeriesRenderer1);

                    XYSeriesRenderer xySeriesRenderer2=new XYSeriesRenderer();
                    xySeriesRenderer2.setAnnotationsColor(0xFF000000);//Set the color of annotations (notes can highlight a certain coordinate)
                    xySeriesRenderer2.setAnnotationsTextAlign(Align.CENTER);//Set the location of the annotation
                    xySeriesRenderer2.setAnnotationsTextSize(15);//Set the size of the note text
                    xySeriesRenderer2.setColor(0xFFEaec1e);//The color of the graph or line representing the set of data
                    xySeriesRenderer2.setLineWidth(4);
                    xySeriesRenderer2.setDisplayChartValues(false);//Set whether to display the y-axis coordinate value of the coordinate point
                    xySeriesRenderer2.setChartValuesTextSize(15);
                    seriesRenderer.addSeriesRenderer(xySeriesRenderer2);

                    XYSeriesRenderer xySeriesRenderer3=new XYSeriesRenderer();
                    xySeriesRenderer3.setAnnotationsColor(0xFF000000);//Set the color of annotations (notes can highlight a certain coordinate)
                    xySeriesRenderer3.setAnnotationsTextAlign(Align.CENTER);//Set the location of the annotation
                    xySeriesRenderer3.setAnnotationsTextSize(15);//Set the size of the note text
                    xySeriesRenderer3.setColor(0xFF1e35ec);//The color of the graph or line representing the set of data
                    xySeriesRenderer3.setLineWidth(4);
                    xySeriesRenderer3.setDisplayChartValues(false);//Set whether to display the y-axis coordinate value of the coordinate point
                    xySeriesRenderer3.setChartValuesTextSize(15);
                    seriesRenderer.addSeriesRenderer(xySeriesRenderer3);
                }
                else if(counter == 2){
                    XYSeriesRenderer xySeriesRenderer1=new XYSeriesRenderer();
                    xySeriesRenderer1.setAnnotationsColor(0xFF000000);//Set the color of annotations (notes can highlight a certain coordinate)
                    xySeriesRenderer1.setAnnotationsTextAlign(Align.CENTER);//Set the location of the annotation
                    xySeriesRenderer1.setAnnotationsTextSize(15);//Set the size of the note text
                    xySeriesRenderer1.setColor(0xFF0c125a);//The color of the graph or line representing the set of data
                    xySeriesRenderer1.setLineWidth(4);
                    xySeriesRenderer1.setDisplayChartValues(false);//Set whether to display the y-axis coordinate value of the coordinate point
                    xySeriesRenderer1.setChartValuesTextSize(15);

                    seriesRenderer.addSeriesRenderer(xySeriesRenderer1);

                    XYSeriesRenderer xySeriesRenderer2=new XYSeriesRenderer();
                    xySeriesRenderer2.setAnnotationsColor(0xFF000000);//Set the color of annotations (notes can highlight a certain coordinate)
                    xySeriesRenderer2.setAnnotationsTextAlign(Align.CENTER);//Set the location of the annotation
                    xySeriesRenderer2.setAnnotationsTextSize(15);//Set the size of the note text
                    xySeriesRenderer2.setColor(0xFF5c1604);//The color of the graph or line representing the set of data
                    xySeriesRenderer2.setLineWidth(4);
                    xySeriesRenderer2.setDisplayChartValues(false);//Set whether to display the y-axis coordinate value of the coordinate point
                    xySeriesRenderer2.setChartValuesTextSize(15);
                    seriesRenderer.addSeriesRenderer(xySeriesRenderer2);
                }
                else if(counter == 1){
                    XYSeriesRenderer xySeriesRenderer1=new XYSeriesRenderer();
                    xySeriesRenderer1.setAnnotationsColor(0xFF000000);//Set the color of annotations (notes can highlight a certain coordinate)
                    xySeriesRenderer1.setAnnotationsTextAlign(Align.CENTER);//Set the location of the annotation
                    xySeriesRenderer1.setAnnotationsTextSize(15);//Set the size of the note text
                    xySeriesRenderer1.setColor(0xFF5c1604);//The color of the graph or line representing the set of data
                    xySeriesRenderer1.setLineWidth(4);
                    xySeriesRenderer1.setDisplayChartValues(false);//Set whether to display the y-axis coordinate value of the coordinate point
                    xySeriesRenderer1.setChartValuesTextSize(15);
                    seriesRenderer.addSeriesRenderer(xySeriesRenderer1);
                }
            }
        }
        catch (Exception ex){
            Log.e("Error :", ex.getMessage());
        }

        /*Tracer, set the overall effect of the chart, such as x, y axis effect, zoom ratio, color settings*/

        /*The tracer of a certain group of data, depicting the personalized display effect of the group of data, mainly the effect of font and color*/
        /*XYSeriesRenderer xySeriesRenderer1=new XYSeriesRenderer();
        xySeriesRenderer1.setAnnotationsColor(0xFFFF0000);//Set the color of annotations (notes can highlight a certain coordinate)
        xySeriesRenderer1.setAnnotationsTextAlign(Align.CENTER);//Set the location of the annotation
        xySeriesRenderer1.setAnnotationsTextSize(12);//Set the size of the note text
        xySeriesRenderer1.setColor(0xFF5c1604);//The color of the graph or line representing the set of data
        xySeriesRenderer1.setLineWidth(3);
        xySeriesRenderer1.setDisplayChartValues(false);//Set whether to display the y-axis coordinate value of the coordinate point
        xySeriesRenderer1.setChartValuesTextSize(12);//Set the font size of the displayed coordinate point value
*/

        /*The tracer of a certain group of data, depicting the personalized display effect of the group of data, mainly the effect of font and color*/
        /*XYSeriesRenderer xySeriesRenderer2=new XYSeriesRenderer();
        xySeriesRenderer2.setPointStyle(PointStyle.CIRCLE);//Display style of coordinate points
        xySeriesRenderer2.setPointStrokeWidth(3);//The size of the coordinate point
        xySeriesRenderer2.setColor(0xFF00C8FF);//The color of the graph or line representing the set of data
        xySeriesRenderer2.setDisplayChartValues(false);//Set whether to display the y-axis coordinate value of the coordinate point
        xySeriesRenderer2.setChartValuesTextSize(12);//Set the font size of the displayed coordinate point value*/


        //seriesRenderer.addSeriesRenderer(xySeriesRenderer2);
        return seriesRenderer;
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
                ((NavigationHost) getActivity()).navigateTo(new Anal(style), false); // Navigate to the next Fragment

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
