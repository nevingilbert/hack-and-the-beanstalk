package com.example.soundtouchproject;

import android.content.Context;
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
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VolumeThread extends Thread {

    public static final double ERROR_INTENSITY = 10;
    public static final double VOLUME_CHANGE = 5;
    public static final int FRAME_GAP = 150;

    private Context context;
    private MediaRecorder mic;

    //initial overall volume
    private double targetIntensity;

    private final AtomicBoolean loop = new AtomicBoolean(false);
    public volatile boolean loopPrim = false;
    private int currentSpeakerVolume;

    private int MIN, MAX;

    public VolumeThread(Context context, MediaRecorder mic, double initIntensity, int min, int max) {
        this.context = context;
        this.mic = mic;
        this.targetIntensity = initIntensity;
        this.currentSpeakerVolume = getSpeakerVolume(false);

        this.MIN = min;
        this.MAX = max;
    }

    @Override
    public void run() {
        loop.set(true);
        loopPrim = true;
        while (!Thread.currentThread().isInterrupted() && loop.get() && loopPrim) {
            double micIntensity = mic.getIntensity();
            Log.println(Log.DEBUG, "Current Speaker Volume", Double.toString(currentSpeakerVolume));
            Log.println(Log.DEBUG, "Target Intensity", Double.toString(targetIntensity));
            Log.println(Log.DEBUG, "Current Intensity", Double.toString(micIntensity));

            if (!loop.get() || !loopPrim) {
                return;
            }

            //TODO: Implement a feature so that if the user changes the volume manually, then we assume they have a found a new comfortable sound, and normalize
//            if (currentSpeakerVolume != getSpeakerVolume(true)) {
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                targetIntensity = micIntensity;
//                currentSpeakerVolume = getSpeakerVolume(true);
//                Log.println(Log.DEBUG, "Feature", "Normalized Target");
//            }

            if (targetIntensity - micIntensity > ERROR_INTENSITY && currentSpeakerVolume <= MAX - VOLUME_CHANGE) {
                currentSpeakerVolume += VOLUME_CHANGE;
                setSpeakerVolume(currentSpeakerVolume);
            } else if (micIntensity - targetIntensity > ERROR_INTENSITY && currentSpeakerVolume >= MIN + VOLUME_CHANGE) {
                currentSpeakerVolume -= VOLUME_CHANGE;
                setSpeakerVolume(currentSpeakerVolume);
            }

            try {
                Thread.sleep(FRAME_GAP);
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

    public static int getSpeakerVolume(boolean target) {
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

        int start = 0;
        int end = 0;

        if (target) {
            start = responseString.indexOf("<targetvolume>") + "<targetvolume>".length();
            end = responseString.indexOf("</targetvolume>");
        } else {
            start = responseString.indexOf("<actualvolume>") + "<actualvolume>".length();
            end = responseString.indexOf("</actualvolume>");
        }

        if (start < 0 || end > responseString.length() || responseString.length() == 0) {
            return -1;
        }
        try {
            return Integer.parseInt(responseString.substring(start, end));
        } catch (Exception e) {
            throw new RuntimeException("A response was expected but nothing was received. Please check your internet connection.");
        }
    }

    public void setLoop(boolean in) {
        Log.println(Log.ERROR, "Thread", "Trying to terminate volume Thread");
        loop.set(in);
        loopPrim = in;
    }
}
