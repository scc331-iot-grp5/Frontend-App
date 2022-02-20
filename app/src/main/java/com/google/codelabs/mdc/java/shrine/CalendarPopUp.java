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

public class CalendarPopUp{

    int day = 0;
    int month = 0;
    int year = 0;
    String m = " ";

    public void showPopupWindow(final View view, Context context) {


        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.calandar_popup, null);

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

        Button buttonEdit = popupView.findViewById(R.id.getDate);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = picker.getDayOfMonth();
                month = picker.getMonth();
                if(month == 0 || month == 1 ||month == 2 ||month == 3 ||month == 4 ||month == 5 ||month == 6 ||month == 7 ||month == 8){
                    month = month +1;
                    m = '0'+ Integer.toString(month);
                }
                year = picker.getYear();
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
    public String getDate(){
        return (Integer.toString(year) + "-" + m + "-" + Integer.toString(day));
    }

}
