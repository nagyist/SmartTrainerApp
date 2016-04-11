package com.smarttrainer.smarttrainer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
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
import com.smarttrainer.smarttrainer.models.GetByID;
import com.smarttrainer.smarttrainer.models.MotionJudge;
import com.smarttrainer.smarttrainer.models.MotionJudgeImpl;
import com.smarttrainer.smarttrainer.models.MotionJudgeSelfDefinedSpeedImpl;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class ExerActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private int id = 0;
    private BandClient client = null;
    private TextView txtStatus;
    private TextView curScore;
    private TextView curSet;
    private TextView curRep;
    private TextView prevBest;
    private boolean mute = false;
    private int curSetCount;

    private double curAx = 0;
    private double curAy = 0;
    private double curAz = 0;

    private float minFreq = 0f;
    private float maxFreq = 0f;

    List<float[]> ls;

    Handler recorder = new Handler();
    Runnable writeArray = new Runnable(){
        public void run(){
            float[] cur = new float[3];
            cur[0] = (float)curAx;
            cur[1] = (float)curAy;
            cur[2] = (float)curAz;
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


    MotionJudge mj;
    private TextToSpeech tts;
    private int finished = 0;
    private static final int TTS_REQUEST_CODE = 903;
    private Handler runner = new Handler();
    Runnable testExer = new Runnable(){
        public void run(){
            String toSpeak = mj.judgeMotion(ls).getDescription();
            finished += mj.getCount(ls);
            if (!mute)
                tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            appendToUI("" + (curSetCount + 1), String.valueOf(finished), (float) 66.88, false);
            ls.clear();
            runner.postDelayed( this, 10000 );
            if (finished >= GetByID.getRequiredRep(id))
                tts.speak("Mission complete! Please stop now.", TextToSpeech.QUEUE_FLUSH, null);
            else if (finished > GetByID.getRequiredRep(id) / 2 && toSpeak == "Too Slow.")
                tts.speak("Carry on!", TextToSpeech.QUEUE_FLUSH, null);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // TODO: delete this & add a service!
        setContentView(R.layout.activity_exer2);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.instr_toolbar);
        setSupportActionBar(myToolbar);

        if(Build.VERSION.SDK_INT >= 21) {
            // set statusBar
            Window window = this.getWindow();
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(Color.parseColor("#a20000"));
        }

        tts = new TextToSpeech(this, this);

        txtStatus = (TextView) findViewById(R.id.txt_status);
        txtStatus.setText("Ready");

        Bundle b = getIntent().getExtras();
        id = b.getInt("ID");

        TextView exerName = (TextView) findViewById(R.id.exercise_name);
        exerName.setText(GetByID.getExerName(id));

        // TODO: use a DB for this if there is a huge shit of forms
        GifImageView gifImageView = (GifImageView) findViewById(R.id.exer_gif);
        if (id != 0)
            gifImageView.setImageResource(GetByID.getGIF(id));

        final DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selectSet = "SELECT reps, score FROM workout_history WHERE timestamp > ? AND formID = ?";
        String curDay = getToday();
        Cursor cursor = db.rawQuery(selectSet, new String[]{curDay, String.valueOf(id)});

        curSet = (TextView) findViewById(R.id.cur_set);
        curSetCount = cursor.getCount();
        curSet.setText(String.valueOf(curSetCount));

        curScore = (TextView) findViewById(R.id.cur_score);
        prevBest = (TextView) findViewById(R.id.prev_best_num);
        curRep = (TextView) findViewById(R.id.cur_rep);
        float maxScore = 0;
        //int curReps = 0;
        if (cursor != null)
            if (cursor.moveToFirst())
            {
                do
                {
                    maxScore = Math.max(maxScore, cursor.getFloat(cursor.getColumnIndex("score")));
                    //curReps += cursor.getInt(cursor.getColumnIndex("reps"));
                } while (cursor.moveToNext());
            }
        prevBest.setText(maxScore + "%");
        //curRep.setText(String.valueOf(curReps));
        cursor.close();

        new AccelerometerSubscriptionTask().execute();

        ImageButton stop = (ImageButton) findViewById(R.id.stop_btn);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // TODO: delete this & add a service!
                runner.removeCallbacks(testExer);
                recorder.removeCallbacks(writeArray);

                // TODO: DB update and display score
                appendToUI("" + (curSetCount + 1), String.valueOf(finished), (float) 66.88, false);

                if (finished > 0) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase(); // write access

                    ContentValues values = new ContentValues();
                    values.put("formID", id);
                    values.put("reps", finished);
                    values.put("score", 88.88); // TODO: Score
                    long rowId = db.insert("workout_history", null, values);
                }
                //Log.d( "rowId", "inserted " + rowId);
            }
        });

        ls = new ArrayList<float[]>(4096);
        recorder.postDelayed(writeArray, 1000);   // TODO: start late according to form id


        mj = getMotionJudgerById(id);

    }

    private MotionJudge getMotionJudgerById(int formId){
        if(formId==-1){
            return new MotionJudgeSelfDefinedSpeedImpl(this.minFreq,this.maxFreq);
        } else {
            return new MotionJudgeImpl(getMotionJudgeModelInputStream(id));
        }

    }

    private InputStream getMotionJudgeModelInputStream(int formId){
        if(formId==0){
            return this.getResources().openRawResource(R.raw.bench);
        } else if(formId==1){
            return this.getResources().openRawResource(R.raw.curl);
        }
        return null;
    }

    private String getToday()
    {
        Timestamp today = new Timestamp(System.currentTimeMillis());
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        return today.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.instr_toolbar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.mute)
            mute = true;
        else if (id == R.id.play_sound)
            mute = false;
        //noinspection SimplifiableIfStatement
        return true;
    }

    private class AccelerometerSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    Log.d("band", "Band is connected.\n");
                    client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS16);
                } else {
                    Log.d("band", "Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
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
                Log.d("band", exceptionMessage);

            } catch (Exception e) {
                Log.d("band", e.getMessage());
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
                Log.d("band", e.getMessage());
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

    private void appendToUI(final String set, final String rep, final float score, final boolean max) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                curSet.setText(set);
                curRep.setText(rep);
                curScore.setText(score + "%");
                if (max)
                    prevBest.setText(score + "%");
            }
        });
    }

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                Log.d("band", "Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        Log.d("band", "Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US);
            tts.setSpeechRate((float) 0.5);
            runner.postDelayed(testExer, 7000); // TODO: start late according to form id
        }
    }
}
