package com.example.soundtouchproject;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VolumeThread implements Runnable {

    public static final double ERROR_INTENSITY = 2000;
    public static final double VOLUME_CHANGE = 10;

    private Context context;
    private MediaRecorder mic;

    //initial overall volume
    private double targetIntenstiy;
    private boolean loop;

    //last set volume for speaker
    private double previousSpeakerVolume = -1;

    private int currentSpeakerVolume;

    public VolumeThread(Context context, MediaRecorder mic, double initIntensity) {
        this.context = context;
        this.mic = mic;
        this.targetIntenstiy = initIntensity;
        this.currentSpeakerVolume = getSpeakerVolume();
    }

    @Override
    public void run() {

        while (true) {
            double micIntensity = mic.getMaxAmplitude();
            Log.println(Log.DEBUG, "Current Speaker Volume",  Double.toString(currentSpeakerVolume));
            Log.println(Log.DEBUG, "Target Intensity", Double.toString(targetIntenstiy));
            Log.println(Log.DEBUG, "Current Intensity", Double.toString(micIntensity));

            if (previousSpeakerVolume != -1 && previousSpeakerVolume != currentSpeakerVolume) {
                Log.println(Log.DEBUG, "Feature", "No impl.");
            }

            if (targetIntenstiy - micIntensity > ERROR_INTENSITY && currentSpeakerVolume <= 100) {
                currentSpeakerVolume += VOLUME_CHANGE;
            } else if (micIntensity - targetIntenstiy > ERROR_INTENSITY && currentSpeakerVolume >= 0) {
                currentSpeakerVolume -= VOLUME_CHANGE;
            }

            setSpeakerVolume(currentSpeakerVolume);
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

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //CODE HERE
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.println(Log.ERROR, "Netowork error", error.getMessage());
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
        // Add the request to the RequestQueue.
        // Request a string response from the provided URL.
        final String url = "http://192.168.1.14:8090/volume";
        final RequestQueue queue = Volley.newRequestQueue(context);
        final StringBuilder rawResponse = new StringBuilder();

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        rawResponse.append(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.println(Log.ERROR, "Netowork error", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getParams() {
                HashMap<String, String> out = new HashMap<>();
                out.put("Content-Type", "application/xml");
                return out;
            }
        };
        queue.add(stringRequest);
        loop = true;
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                loop = false;
            }
        });
        while (loop) ;
        int start = rawResponse.indexOf("<actualvolume>") + "<actualvolume>".length();
        int end = rawResponse.indexOf("</actualvolume>");
        int out = Integer.parseInt(rawResponse.substring(start, end));
        return out;
    }
}
