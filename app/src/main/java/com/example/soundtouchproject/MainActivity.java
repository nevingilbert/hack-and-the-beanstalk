package com.example.soundtouchproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static android.Manifest.permission.RECORD_AUDIO;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.button);
        final TextView textView = findViewById(R.id.textView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            textView.setText("This app needs Internet permission. Please grant it.");
        }

        if (ActivityCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO},
                    0);
        }

        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
            }
        });


    }

    private void startService() {
        startService(new Intent(getApplicationContext(), BackgroundService.class));
    }

    ;
}
