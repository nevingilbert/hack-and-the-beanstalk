package com.example.soundtouchproject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.support.annotation.Nullable;

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

        double initDB = 20 * Math.log10(mic.getMaxAmplitude() / 32767);
        VolumeThread thread = new VolumeThread(this, mic, initDB);


        new Thread(thread).start();
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }


    public static void sendNotification(String title, String body, Context context) {
        // prepare intent which is triggered if the
        // notification is selected

        //Intent intent = new Intent(this, NotificationReceiver.class);
        //PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(body)
//                .setContentIntent(pIntent)
                .setAutoCancel(true).build();


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
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
