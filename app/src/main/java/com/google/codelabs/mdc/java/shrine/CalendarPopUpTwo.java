package com.google.codelabs.mdc.java.shrine;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;

import org.json.JSONObject;

import java.util.ArrayList;

public class CalendarPopUpTwo{

    int day = 0;
    int month = 0;
    int year = 0;
    int day2 = 0;
    int month2 = 0;
    int year2 = 0;

    public void showPopupWindow(final View view, Context context) {
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.calandar_popup_2, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler
        DatePicker picker =(DatePicker) popupView.findViewById(R.id.datePicker1);

        Button buttonEdit = popupView.findViewById(R.id.getDateBefore);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = picker.getDayOfMonth();
                month = picker.getMonth();
                year = picker.getYear();
            }
        });

        Button buttonEdit1 = popupView.findViewById(R.id.getDateAfter);
        buttonEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day2 = picker.getDayOfMonth();
                month2 = picker.getMonth();
                year2 = picker.getYear();
                popupWindow.dismiss();

            }
        });

        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }
    public String getDateBefore(){
        return (Integer.toString(day) + "-" + Integer.toString(month) + "-" + Integer.toString(year));
    }
    public String getDateAfter(){
        return (Integer.toString(day2) + "-" + Integer.toString(month2) + "-" + Integer.toString(year2));
    }

}