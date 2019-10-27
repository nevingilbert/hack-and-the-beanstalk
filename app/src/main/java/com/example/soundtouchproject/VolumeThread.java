package com.example.soundtouchproject;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VolumeThread implements Runnable {

    public static final double ERROR_INTENSITY = 2000;
    public static final double VOLUME_CHANGE = 10;

    private Context context;
    private MediaRecorder mic;

    //initial overall volume
    private double targetIntenstiy;

    //last set volume for speaker
    private double previousSpeakerVolume = -1;

    private int currentSpeakerVolume;

    private int MIN, MAX;

    public VolumeThread(Context context, MediaRecorder mic, double initIntensity, int min, int max) {
        this.context = context;
        this.mic = mic;
        this.targetIntenstiy = initIntensity;
        int a = getSpeakerVolume();
        setSpeakerVolume(25);
        Log.println(Log.DEBUG, "Speaker volume test", Integer.toString(a));
        this.currentSpeakerVolume = getSpeakerVolume();

        this.MIN = min;
        this.MAX = max;
    }

    @Override
    public void run() {

        while (true) {
            double micIntensity = mic.getIntensity();
            Log.println(Log.DEBUG, "Current Speaker Volume", Double.toString(currentSpeakerVolume));
            Log.println(Log.DEBUG, "Target Intensity", Double.toString(targetIntenstiy));
            Log.println(Log.DEBUG, "Current Intensity", Double.toString(micIntensity));

            if (previousSpeakerVolume != -1 && previousSpeakerVolume != currentSpeakerVolume) {
                Log.println(Log.DEBUG, "Feature", "No impl.");
            }

            if (targetIntenstiy - micIntensity > ERROR_INTENSITY && currentSpeakerVolume <= MAX - VOLUME_CHANGE) {
                currentSpeakerVolume += VOLUME_CHANGE;
                setSpeakerVolume(currentSpeakerVolume);
            } else if (micIntensity - targetIntenstiy > ERROR_INTENSITY && currentSpeakerVolume >= MIN + VOLUME_CHANGE) {
                currentSpeakerVolume -= VOLUME_CHANGE;
                setSpeakerVolume(currentSpeakerVolume);
            }

            previousSpeakerVolume = currentSpeakerVolume;

            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setSpeakerVolume(int volume) {
        // Add the request to the RequestQueue.
        // Request a string response from the provided URL.
        final String requestBody = String.format(Locale.US, "<volume>%d</volume>", volume);
        final String url = "http://192.168.1.14:8090/volume";
        final RequestQueue queue = Volley.newRequestQueue(context);

        final StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //CODE HERE
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.println(Log.ERROR, "Network error", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getParams() {
                HashMap<String, String> out = new HashMap<>();
                out.put("Content-Type", "application/xml");
                return out;
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }
        };
        queue.add(stringRequest);
    }

    private int getSpeakerVolume() {
        OkHttpClient client = new OkHttpClient();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        Request request = new Request.Builder()
                .url("http://192.168.1.14:8090/volume")
                .get()
                .addHeader("Content-Type", "application/xml")
                .addHeader("Accept", "*/*")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Host", "http://192.168.1.14:8090")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Connection", "keep-alive")
                .addHeader("cache-control", "no-cache")
                .build();

        String responseString = "";
        try {
            Response response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int start = responseString.indexOf("<actualvolume>") + "<actualvolume>".length();
        int end = responseString.indexOf("</actualvolume>");

        Log.println(Log.DEBUG, "SubstringStart", Integer.toString(start));
        Log.println(Log.DEBUG, "SubstringEnd", Integer.toString(end));

        if (start < 0 || end > responseString.length()) {
            return -1;
        }

        return Integer.parseInt(responseString.substring(start, end));
    }
}
