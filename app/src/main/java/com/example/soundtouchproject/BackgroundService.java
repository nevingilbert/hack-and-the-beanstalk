package com.example.soundtouchproject;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

public class BackgroundService extends IntentService {

    public final int MIN = 20;
    public final int MAX = 70;

    private VolumeThread thread;

    private MediaRecorder mic = null;

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
        }

        long startTime = System.currentTimeMillis();

        int n = 0;
        double sum = 0;

        while (System.currentTimeMillis() < startTime + 2000) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sum += mic.getIntensity();
            n++;
        }

        thread = new VolumeThread(getApplicationContext(), mic, 1.1 * sum / n, MIN, MAX);
        thread.start();
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

        Log.println(Log.INFO, "Services", "Stopping Background Service");
        thread.interrupt();
        thread.setLoop(false);
        thread.loopPrim = false;
        Log.println(Log.INFO, "Services", "Stopped Background Service");

        return super.stopService(name);
    }
}
