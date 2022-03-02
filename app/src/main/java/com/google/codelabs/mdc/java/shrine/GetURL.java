package com.google.codelabs.mdc.java.shrine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.net.URL;

public class GetURL extends AsyncTask<String,Void,Bitmap> {

    public interface AsyncResponse {
        void processFinish(GroundOverlay response);
    }

    public AsyncResponse delegate = null;
    LatLng point;
    GoogleMap map;
    String urlS;
    String h;
    String w;
    int bearing;

    public GetURL(int bearing, String h, String w,String url ,LatLng point, GoogleMap map ,AsyncResponse delegate){
        this.delegate = delegate;
        this.map = map;
        this.point = point;
        this.urlS = url;
        this.h = h;
        this.w = w;
        this.bearing = bearing;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        try {
            java.net.URL url = new URL(urlS);
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
                .position(point, Integer.parseInt(w), Integer.parseInt(h))
                .bearing(bearing)
                .clickable(true);

        GroundOverlay newMap = map.addGroundOverlay(newarkMap);

        delegate.processFinish(newMap);
    }
}
