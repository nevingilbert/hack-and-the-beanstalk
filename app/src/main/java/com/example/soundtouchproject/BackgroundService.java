package com.example.soundtouchproject;

import android.app.IntentService;
import android.content.Intent;
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
            mic = new MediaRecorder(new android.media.MediaRecorder());
            mic.getMic().setAudioSource(android.media.MediaRecorder.AudioSource.MIC);
            mic.getMic().setOutputFormat(android.media.MediaRecorder.OutputFormat.THREE_GPP);
            mic.getMic().setAudioEncoder(android.media.MediaRecorder.AudioEncoder.AMR_NB);
            mic.getMic().setOutputFile("/dev/null/");

            try {
                mic.getMic().prepare();
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
            }
            mic.getMic().start();
            mEMA = 0.0;
        }

        double initIntensity = 0;
        while (initIntensity == 0) {
            initIntensity = mic.getIntensity();
        }
        VolumeThread thread = new VolumeThread(this, mic, initIntensity, 20, 70);

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
            mic.getMic().stop();
            mic.getMic().release();
            mic = null;
        }

        return super.stopService(name);
    }
}
