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
    ArrayList<String> userID;
    ArrayList<String> name;
    int rep;
    double maxFreq;
    double minFreq;
    List<Integer> mSelectedItems = new ArrayList<>();  // Where we track the selected items

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        userID = getArguments().getStringArrayList("id");
        name = getArguments().getStringArrayList("name");
        rep = getArguments().getInt("rep");
        maxFreq = getArguments().getDouble("fre");
        minFreq = maxFreq / 2;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        + "challenger=" + ExistingUser.getUserName(FriendListFragment.this.getContext())
                        + "&form_id=" + "3"
                        + "&min_frequency=" + minFreq
                        + "&max_frequency=" + maxFreq
                        + "&repetition=" + rep
                        + "&challengee=" + userID.get(mSelectedItems.get(0));
//                String url ="http://www.google.com";
                Log.d("Volley", "URL: " + url);
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Volley", "Send challenge successfully");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Volley", "Fail to send challenge");
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
