package com.google.codelabs.mdc.java.shrine;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements NavigationHost,NotificationHost {

    private final int x = 5;
    String connection = "https://5f6b-148-88-245-64.ngrok.io";

    String CHANNEL_ID = "Mes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shr_main_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }

    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "message";
            String description = "for messages";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);


        }
    }

    @Override
    public void createWebSocketClient(int userid, int style)
    {

        Timer t = new Timer();

        t.scheduleAtFixedRate(
                new TimerTask()
                {
                    public void run()
                    {
                        System.out.println("3 seconds passed");

                        String url = connection + "/checkNotifications";
                        JSONArray z = new JSONArray();
                        JSONObject json = new JSONObject();

                        try {
                            json.put("userID",userid);
                            z.put(json);
                        }
                        catch(Exception e){}


                        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                                (Request.Method.POST, url,z, new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        try {
                                            System.out.println("Refresh");
                                            for (int i = 0; i < response.length(); i++) {
                                                JSONObject object1 = response.getJSONObject(i);

                                                String name = (String) object1.get("display_name");
                                                System.out.println(name);

                                                Intent intent = new Intent(getApplicationContext(), AllChats.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.putExtra("userID",userid);
                                                intent.putExtra("style",style);
                                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                                        .setSmallIcon(R.drawable.ic_baseline_chat_24)
                                                        .setContentTitle("New message")
                                                        .setContentText("You have a new message from " + name)
                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                        .setContentIntent(pendingIntent)
                                                        .setAutoCancel(true);

                                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());


                                                Random rand = new Random(); //instance of random class
                                                //generate random values from 0-24
                                                int int_random = rand.nextInt(300);
// notificationId is a unique int for each notification that you must define
                                                notificationManager.notify(int_random, builder.build());

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

                        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                    }
                },
                0,      // run first occurrence immediately
                3000);
    }



}
