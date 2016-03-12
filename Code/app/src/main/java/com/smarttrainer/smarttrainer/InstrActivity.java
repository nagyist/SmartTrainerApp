package com.smarttrainer.smarttrainer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Locale;

public class InstrActivity extends AppCompatActivity {
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instr);
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

        Button start = (Button) findViewById(R.id.start_btn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toExer = new Intent();
                toExer.setClass(InstrActivity.this, ExerActivity.class);
                startActivity(toExer);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.instr_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.play_sound){
            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.US);
                        String toSpeak = "Lie on your back with your feet flat on the floor. If" +
                                "your feet don’t reach the floor, use a stable board to" +
                                "accommodate size. Grasp the barbell with a wider" +
                                "than shoulder-width grip, wrapping thumbs around" +
                                "the bar. Hold the barbell at arm’s length above your" +
                                "upper-chest area.";
                        tts.setSpeechRate((float) 0.7);
                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });
        }
        //noinspection SimplifiableIfStatement
        return true;
    }
}
