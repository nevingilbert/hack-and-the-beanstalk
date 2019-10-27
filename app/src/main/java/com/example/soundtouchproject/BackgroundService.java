package com.example.soundtouchproject;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaRecorder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

public class BackgroundService extends IntentService {

    private MediaRecorder mic = null;
    private double mEMA = 0.0;

    public BackgroundService() {
        super("Background Speaker Service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mic == null) {
            mic = new MediaRecorder();
            mic.setAudioSource(MediaRecorder.AudioSource.MIC);
            mic.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mic.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mic.setOutputFile("/dev/null/");

            try {
                mic.prepare();
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
            }
            mic.start();
            mEMA = 0.0;
        }

        double initIntensity = 0;
        while (initIntensity == 0) {
            initIntensity = mic.getMaxAmplitude();
        }
        VolumeThread thread = new VolumeThread(this, mic, initIntensity);

        Log.println(Log.DEBUG, "BACKGROUND SERVICE STARTED", "SERVICE STARTED");

        new Thread(thread).start();
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public boolean stopService(Intent name) {
        if (mic != null) {
            mic.stop();
            mic.release();
            mic = null;
        }

        return super.stopService(name);
    }
}
