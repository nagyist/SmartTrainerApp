package com.smarttrainer.smarttrainer;

import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.band.BandException;

/**
 * Created by ld on 3/18/16.
 */
public class StartTask extends AsyncTask<Void, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Void... params) {
        /*try {
            if (getConnectedBandClient()) {
                Log.d("", "Band is connected.\n");
                if (addTile()) {
                    updatePages();
                }
            } else {
                appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                return false;
            }
        } catch (BandException e) {
            handleBandException(e);
            return false;
        } catch (Exception e) {
            appendToUI(e.getMessage());
            return false;
        }*/

        return true;
    }
/*
    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            btnStop.setEnabled(true);
        } else {
            btnStart.setEnabled(true);
        }
    }

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                appendToUI("Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        appendToUI("Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }
    */
}