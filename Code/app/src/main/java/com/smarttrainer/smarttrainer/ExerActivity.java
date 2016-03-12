package com.smarttrainer.smarttrainer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.SampleRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExerActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private BandClient client = null;
    private TextView txtStatus;

    private double curAx = 0;
    private double curAy = 0;
    private double curAz = 0;

    List<List<Double>> ls;

    Handler recorder = new Handler();
    Runnable writeArray = new Runnable(){
        public void run(){
            List<Double> cur = new ArrayList<>();
            cur.add(curAx);
            cur.add(curAy);
            cur.add(curAz);
            ls.add(cur);
            runner.postDelayed( this, 50 );
        }
    };

    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {
            if (event != null) {
                curAx = event.getAccelerationX();
                curAy = event.getAccelerationY();
                curAz = event.getAccelerationZ();
            }
        }
    };

    private TextToSpeech tts;
    private static final int TTS_REQUEST_CODE = 903;
    private Handler runner = new Handler();
    Runnable testExer = new Runnable(){
        public void run(){
            appendToUI(String.format(" X = %.3f \n Y = %.3f\n Z = %.3f", curAx, curAy, curAz));
            String toSpeak = "Yo, Man!";
            tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            ls.clear();
            runner.postDelayed( this, 6000 );
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exer2);
        tts = new TextToSpeech(this, this);

        txtStatus = (TextView) findViewById(R.id.txt_status);
        txtStatus.setText("Ready");
        new AccelerometerSubscriptionTask().execute();

        Button stop = (Button) findViewById(R.id.stop_btn);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runner.removeCallbacks(testExer);
            }
        });

        ls = new ArrayList<List<Double>>(4096);
        recorder.postDelayed(writeArray, 50);
    }

    private class AccelerometerSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    appendToUI("Band is connected.\n");
                    client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS16);
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (client != null) {
            try {
                client.getSensorManager().unregisterAccelerometerEventListener(mAccelerometerEventListener);
            } catch (BandIOException e) {
                appendToUI(e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (client != null) {
            try {
                client.disconnect().await();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
        }
        super.onDestroy();
    }

    private void appendToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtStatus.setText(string);
            }
        });
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

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US);
            tts.setSpeechRate((float) 0.5);
            runner.postDelayed(testExer, 6000);
        }
    }
}
