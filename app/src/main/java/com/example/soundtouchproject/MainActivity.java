package com.example.soundtouchproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.RECORD_AUDIO;
import static com.example.soundtouchproject.VolumeThread.getSpeakerVolume;


public class MainActivity extends AppCompatActivity {

    private boolean serviceStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton button = findViewById(R.id.startService);
        final SeekBar volumeInput = findViewById(R.id.volumeLevelSlider);
        final TextView volumeText = findViewById(R.id.targetVolumeLabel);

        volumeText.setText("Target Volume: Service Not Started");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "This app needs Internet permission. Please grant it.", Toast.LENGTH_LONG).show();
        }

        if (ActivityCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO},
                    0);
        }

        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!serviceStarted) {
                    startService();
                    button.setImageDrawable(getDrawable(R.drawable.logored));
                    volumeInput.setProgress(getSpeakerVolume(true));
                } else {
                    stopService();
                    button.setImageDrawable(getDrawable(R.drawable.logowhite));
                }
            }
        });

        volumeInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (serviceStarted && fromUser) {
                    volumeText.setText("Target Volume: " + progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void startService() {
        Log.println(Log.INFO, "Services", "Starting Background Service");
        serviceStarted = true;
        startService(new Intent(getApplicationContext(), BackgroundService.class));
        Log.println(Log.INFO, "Services", "Started Background Service");
    }

    private void stopService() {
        Log.println(Log.INFO, "Services", "Stopping Background Service");
        serviceStarted = false;
        stopService(new Intent(getApplicationContext(), BackgroundService.class));
        Log.println(Log.INFO, "Services", "Stopped Background Service");


    }
}
