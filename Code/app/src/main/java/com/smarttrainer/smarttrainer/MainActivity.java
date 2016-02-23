package com.smarttrainer.smarttrainer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*super.onCreate(savedInstanceState);
        if (ExistingUser.getUserName(MainActivity.this).length() == 0){
            Intent toLogin = new Intent();
            toLogin.setClass(MainActivity.this, LoginActivity.class);
            startActivityForResult(toLogin, 0);
        }

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String UID = data.getExtras().getString("ID");
        Toast.makeText(getApplicationContext(),
                ExistingUser.getUserName(MainActivity.this),
                Toast.LENGTH_SHORT).show();*/
        setContentView(R.layout.activity_main);
        
    }
}
