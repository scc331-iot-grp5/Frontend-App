package com.google.codelabs.mdc.java.shrine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.net.URL;

public class PopupOverlay extends AsyncTask<String,Void,Bitmap> {

    LatLng point;
    GoogleMap map;


    PopupOverlay(LatLng point, GoogleMap map){
        this.point = point;
        this.map = map;
    }

    public void showPopupWindow(final View view, Context context) {
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.add_overlay, null);



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


        TextInputEditText url_ti = popupView.findViewById(R.id.url);

        popupView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String URL = url_ti.getText().toString();
                if(URL != null)
                    new PopupOverlay(point,map).execute(URL);

                popupWindow.dismiss();
                return true;
            }
        });
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        try {
            java.net.URL url = new URL(urls[0]);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return image;
        } catch(IOException e) {
            System.out.println(e);
        }
        return null;
    }
    @Override
    protected void onPostExecute(Bitmap result) {
        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(result))
                .position(point, 20, 20);
        map.addGroundOverlay(newarkMap);
    }
}
