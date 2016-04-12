package com.smarttrainer.smarttrainer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luke on 4/11/16.
 */
public class FriendListFragment extends DialogFragment {
    ArrayList<Integer> id = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    List<Integer> mSelectedItems = new ArrayList<>();  // Where we track the selected items
    boolean ready = false;
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {


        String url = "http://52.3.117.15:8000/user/get_all_users";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        Log.d("Volley", "URL: " + url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley", "Get_USER_SUCCESS");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray users = jsonObject.getJSONArray("users");
                            for (int i = 0; i < users.length(); i++) {
                                Log.d("JSON", "DEBUG");
                                id.add(users.getJSONObject(i).getInt("user_id"));
                                name.add(users.getJSONObject(i).getString("name"));
                            }
                            ready = true;
                        } catch (Exception e) {

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley", "GET_USER_FAILURE");
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        for (int i = 0; i < name.size(); i++) {
            Log.d("name", name.get(i));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        while(!ready){
            SystemClock.sleep(1000);
        }
        builder.setTitle("Pick Friends")
                .setMultiChoiceItems(name.toArray(new String[0]), null, new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            mSelectedItems.add(which);
                        } else if (mSelectedItems.contains(which)) {
                            mSelectedItems.remove(Integer.valueOf(which));
                        }
                    }
                })
        // Set the action buttons
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                String url = "http://52.3.117.15:8000/user/create_challenge?"
                        + "challenger=" + "4"
                        + "&form_id=" + "0"
                        + "&min_frequency=" + "0.3"
                        + "&max_frequency=" + "0.7"
                        + "&repetition=" + getArguments().getInt("rep")
                        + "&challengee=" + "1";
//                String url ="http://www.google.com";
                Log.d("Volley", "URL: " + url);
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Volley", "Success");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Volley", "Failure");
                            }
                        });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        return builder.create();
    }
}
