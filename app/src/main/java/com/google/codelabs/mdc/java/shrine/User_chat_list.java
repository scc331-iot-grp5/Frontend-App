package com.google.codelabs.mdc.java.shrine;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
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
import androidx.lifecycle.ViewModelProvider;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.codelabs.mdc.java.shrine.network.Device;
import com.google.codelabs.mdc.java.shrine.network.ImageRequester;
import com.google.codelabs.mdc.java.shrine.network.Message;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

public class User_chat_list extends Fragment implements MessageAdapter.ItemClickListener, MessageAdapter.ItemLongClickListener{

    String connection = "https://6e66-148-88-245-146.ngrok.io";

    MessageAdapter adapter;
    ArrayList<Message> messages = new ArrayList<>();

    String newTime;
    String newMessage;
    Message newMessageAA;

    TextView tv;
    CircleImageView iv;



    MessageAdapter.ItemClickListener x;
    MessageAdapter.ItemLongClickListener y;

    public int userid;

    public int m;
    RequestQueue queue;

    int style;
    int otherid;
    ImageRequester imageRequester;

    public User_chat_list(int userid,int style, int otherID){
        this.userid = userid;
        this.style = style;
        this.otherid = otherID;
        imageRequester = ImageRequester.getInstance();
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
        View view = inflater.inflate(R.layout.chat_lists, container, false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionInflater inflaterTwo = TransitionInflater.from(requireContext());
            setEnterTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
            setExitTransition(inflaterTwo.inflateTransition(R.transition.slide_right));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.product_grid).setBackgroundResource(R.drawable.shr_product_grid_background_shape);
        }

        queue = Volley.newRequestQueue(getContext());

        setUpToolbar(view);


        MaterialButton nextButton = view.findViewById(R.id.logout);

        MaterialButton map = view.findViewById(R.id.map);
        MaterialButton profile = view.findViewById(R.id.myProfile);
        MaterialButton a = view.findViewById(R.id.myAnalytics);
        MaterialButton chat = view.findViewById(R.id.chat);

        // Set an error if the password is less than 8 characters.
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false); // Navigate to the next Fragment
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new UserMap(userid,style), false); // Navigate to the next Fragment
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new MyProfile(userid,style), false); // Navigate to the next Fragment
            }
        });
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new MyAnalytics(userid,style), false); // Navigate to the next Fragment
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new User_chat_list(userid,style,otherid), false); // Navigate to the next Fragment
            }
        });



        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.chatList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MessageAdapter(getContext(),messages);
        adapter.setClickListener(x);
        adapter.setLongClickListener(y);
        recyclerView.setAdapter(adapter);

        getAllMessagesOnce();
        getDetails();

        TextInputEditText messageToSend = view.findViewById(R.id.search_edit_text);

         tv = view.findViewById(R.id.name);
         iv = view.findViewById(R.id.profile);

        FloatingActionButton fab = view.findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageToSend.getText().toString();
                int isYours = 1;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String time = formatter.format(date);
                newTime = time;
                newMessage = message;

                Message m = new Message(message, isYours, time);
                newMessageAA = m;

                sendMessages();

            }
        });

        Timer t = new Timer();

        t.scheduleAtFixedRate(
                new TimerTask()
                {
                    public void run()
                    {
                        System.out.println("3 seconds passed");
                        getAllMessages();
                    }
                },
                0,      // run first occurrence immediately
                5000);


        return view;
    }
    private void getDetails()
    {
        String url = connection + "/details";

        JSONArray json = new JSONArray();
        JSONObject j = new JSONObject();

        try {
            j.put("userID", otherid);
            json.put(0,j);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.POST, url, json, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            System.out.println("Refresh");
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object1 = response.getJSONObject(i);

                                String url = (String) object1.get("profile_url");
                                String name = (String) object1.get("display_name");

                                imageRequester.setImageFromUrl(iv, url);
                                tv.setText(name);

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
    private void getAllMessages()
    {
        String url = connection + "/messages";

        JSONArray json = new JSONArray();
        JSONObject j = new JSONObject();

        try {
            j.put("userID", userid);
            j.put("selectedUser", otherid);
            json.put(0,j);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.POST, url, json, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            System.out.println("Refresh");
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object1 = response.getJSONObject(i);

                                int isYours = 0;

                                int uID = (int) object1.get("from_userID");
                                String message = (String) object1.get("message");
                                String time = (String) object1.get("created_at");

                                if (uID == userid) {
                                    isYours = 1;
                                }

                                Message temp = new Message(message, isYours, time);

                                if(!messages.contains(temp)){
                                    messages.add(temp);
                                    adapter.updateList(messages);
                                }


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
    private void getAllMessagesOnce()
    {
        String url = connection + "/messages";

        JSONArray json = new JSONArray();
        JSONObject j = new JSONObject();

        try {
            j.put("userID", userid);
            j.put("selectedUser", otherid);
            json.put(0,j);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.POST, url, json, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            System.out.println("Refresh");
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object1 = response.getJSONObject(i);

                                int isYours = 0;

                                int uID = (int) object1.get("from_userID");
                                String message = (String) object1.get("message");
                                String time = (String) object1.get("created_at");

                                if (uID == userid) {
                                    isYours = 1;
                                }

                                Message temp = new Message(message, isYours, time);
                                messages.add(temp);
                                adapter.updateList(messages);


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
    private void sendMessages()
    {
        String url = connection + "/messagesToSend";

        JSONArray json = new JSONArray();
        JSONObject j = new JSONObject();


                try {
                    j.put("message", newMessage);
                    j.put("from_userID", userid);
                    j.put("to_userID", otherid); //temp will be passed when selecting chat
                    j.put("created_at",  newTime);
                    json.put(0,j);
                } catch (Exception e) {
                    e.printStackTrace();
                }


        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.POST, url, json, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {


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
                ((NavigationHost) getActivity()).navigateTo(new AllChats(userid,style), false); // Navigate to the next Fragment

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}
