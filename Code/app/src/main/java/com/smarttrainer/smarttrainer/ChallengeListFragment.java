package com.smarttrainer.smarttrainer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smarttrainer.smarttrainer.models.GetByID;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luke on 4/11/16.
 */
public class ChallengeListFragment extends DialogFragment {
    ArrayList<String> form_id;
    ArrayList<String> challenger;
    ArrayList<String> challengerID;
    ArrayList<String> maxFreq;
    ArrayList<String> rep;

    ArrayList<String> display;


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        form_id = getArguments().getStringArrayList("form_id");
        challenger = getArguments().getStringArrayList("challenger");
        challengerID = getArguments().getStringArrayList("challengerID");
        maxFreq = getArguments().getStringArrayList("maxFreq");
        rep = getArguments().getStringArrayList("rep");

        display = new ArrayList<>();

        for (int i = 0; i < form_id.size(); i++) {
            display.add(challenger.get(i) + ":    " + GetByID.getExerName(Integer.parseInt(form_id.get(i))) + "\n"
                        + "Rep: " + rep.get(i) + "       " + "Freq: " + maxFreq.get(i));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Challenges")
                .setItems(display.toArray(new String[0]), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent toInstr = new Intent();
                        toInstr.putExtra("ID", form_id.get(which)); // form ID
                        toInstr.putExtra("freq", maxFreq.get(which));
                        toInstr.putExtra("creator", challengerID.get(which));
                        toInstr.setClass(getActivity(), InstrActivity.class);
                        startActivity(toInstr);
                    }
                });
        return builder.create();
    }
}
