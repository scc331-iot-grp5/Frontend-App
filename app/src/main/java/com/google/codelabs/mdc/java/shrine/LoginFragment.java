package com.google.codelabs.mdc.java.shrine;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Fragment representing the login screen for Shrine.
 */
public class LoginFragment extends Fragment {

    boolean login = false;
    int style;
    String connection = "https://5f6b-148-88-245-64.ngrok.io";


    WebSocketClient mWebSocketClient;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.shr_login_fragment, container, false);
        final TextInputLayout passwordTextInput = view.findViewById(R.id.password_text_input);
        final TextInputEditText passwordEditText = view.findViewById(R.id.password_edit_text);
        final TextInputEditText usernameEditText = view.findViewById(R.id.username_edit_text);
        MaterialButton nextButton = view.findViewById(R.id.next_button);

        // Set an error if the password is less than 8 characters.
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPasswordValid(passwordEditText.getText())) {
                    passwordTextInput.setError(getString(R.string.shr_error_password));
                }
                else{
                    passwordTextInput.setError(null);
                    isLoginCorrect(usernameEditText.getText().toString(),passwordEditText.getText().toString(),passwordTextInput);
                }
            }
        });
        // Clear the error once more than 8 characters are typed.
        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isPasswordValid(passwordEditText.getText())) {
                    passwordTextInput.setError(null); //Clear the error
                }
                return false;
            }
        });

        getSystemStyle();
        return view;
    }


    private boolean isPasswordValid(@Nullable Editable text) {
        return text != null && text.length() >= 2;
    }
    private void getSystemStyle()
    {
        JSONArray j = new JSONArray();
        JSONObject json = new JSONObject();

        try {
            json.put("sss","ad");
            j.put(json);
        }
        catch(Exception e){}

        String url = connection + "/colorSettings";

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, j, new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject object1 = response.getJSONObject(0);
                            style = (int) object1.get("styleID");

                        }catch(Exception e){}

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    private void isLoginCorrect(@Nullable String username, @Nullable String password, TextInputLayout p){
        JSONArray j = new JSONArray();
        JSONObject json = new JSONObject();

        try {
            json.put("username",username);
            json.put("password",password);
            j.put(json);
        }
        catch(Exception e){}

        String url = connection + "/log-in";

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.POST, url, j, new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject object1 = response.getJSONObject(0);
                            if ((int)(object1.get("is_administrator")) == 1) {
                                System.out.println("Admin");
                                ((NavigationHost) getActivity()).navigateTo(new MapViewFragment((int)object1.get("id"),style), false); // Navigate to the next Fragment
                            }
                            else if ((int)(object1.get("is_super_admin")) == 1) {
                                System.out.println("Super Admin");
                                ((NavigationHost) getActivity()).navigateTo(new Dashboard(style), false); // Navigate to the next Fragment
                            }
                            else if ((int)(object1.get("is_administrator")) == 0) {
                                System.out.println("User");
                                ((NavigationHost) getActivity()).navigateTo(new UserMap((int)object1.get("id"),style), false); // Navigate to the next Fragment
                            }
                            else{
                                p.setError(getString(R.string.shr_incorrect_password));
                            }
                        }catch(Exception e){}

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    public Context getApplicationContext() {
        return this.getContext();
    }
}