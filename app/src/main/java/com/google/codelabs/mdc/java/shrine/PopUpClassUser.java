package com.google.codelabs.mdc.java.shrine;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;

import org.json.JSONObject;

import java.util.ArrayList;

public class PopUpClassUser implements AdapterView.OnItemSelectedListener {

    //PopupWindow display method
    ArrayList<String> ids = new ArrayList();
    String id;
    String objectName;
    String userID;
    TextInputEditText name;
    TextInputEditText email;
    TextInputEditText profile;
    CheckBox access;
    String connection = "https://5f6b-148-88-245-64.ngrok.io";

    TextInputEditText banner;
    boolean admin = false;


    public void showPopupWindow(final View view, Context context,ArrayList<String> x) {


        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_user, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        this.ids = x;
        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler

        TextView test2 = popupView.findViewById(R.id.titleText_U);
        test2.setText("Enter Details to Add a User");


        Spinner spinner = (Spinner) popupView.findViewById(R.id.spinneraaa_U);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, ids);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        name = popupView.findViewById(R.id.name_text);
        email = popupView.findViewById(R.id.email_text);
        profile = popupView.findViewById(R.id.profile_Pic_url);
        banner = popupView.findViewById(R.id.banner_url);
        access = popupView.findViewById(R.id.access);

        access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                admin = true;
            }
        });

        Button buttonEdit = popupView.findViewById(R.id.messageButton_U);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToDb(v);
                //As an example, display the message
                Toast.makeText(view.getContext(), "User has been added successfully!", Toast.LENGTH_SHORT).show();
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

    private void sendToDb(View view) {
        String a = name.getText().toString();
        String b = email.getText().toString();
        String c = profile.getText().toString();
        String d = banner.getText().toString();
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
        JSONObject json = new JSONObject();

        try {
            json.put("fullname",a);
            json.put("email",b);
            json.put("profileURL",c);
            json.put("bannerURL",d);
            json.put("is_admin",admin);
        }
        catch(Exception e){}

        String url = connection + "/newUser";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, json, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        MySingleton.getInstance(view.getContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.spinneraaa_U) {
            String text = adapterView.getItemAtPosition(i).toString();
            if (text.contains(":")) {
                String[] animalsArray = text.split(":");
                objectName = animalsArray[1];
                id = animalsArray[0];

            }

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}