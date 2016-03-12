package com.smarttrainer.smarttrainer;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ExerActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private TextToSpeech tts;
    private static final int TTS_REQUEST_CODE = 903;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exer2);
        /*
        Intent ttsInstallCheck = new Intent();
        ttsInstallCheck.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(ttsInstallCheck, TTS_REQUEST_CODE);*/
    }

    @Override
    public void onInit(int status) {

    }
}
