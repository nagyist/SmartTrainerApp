package com.smarttrainer.smarttrainer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by ld on 3/21/16.
 */
public class ProcedureFragment extends DialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Lie on your back with your feet flat on the floor. If" +
                "your feet don’t reach the floor, use a stable board to" +
                "accommodate size. Grasp the barbell with a wider" +
                "than shoulder-width grip, wrapping thumbs around" +
                "the bar. Hold the barbell at arm’s length above your" +
                "upper-chest area.")
                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }
}
