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

    public static final double ERROR_DB = -1;
    public static final double VOLUME_CHANGE = 10;

    private Context context;
    private MediaRecorder mic;

    //initial overall volume
    private double targetDecibals;

    //last set volume for speaker
    private double previousSpeakerVolume = -1;

    //TODO: current speaker volume
    private int currentSpeakerVolume;

    public VolumeThread(Context context, MediaRecorder mic, double initDB) {
        this.context = context;
        this.mic = mic;
        this.targetDecibals = initDB;
    }

    @Override
    public void run() {

        while (true) {
            Log.println(Log.DEBUG, "Decibal units or volume units",
                    Double.toString(20 * Math.log10(mic.getMaxAmplitude() / 32767)));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


//        double micVolume = 20 * Math.log10(mic.getMaxAmplitude() / 32767);
//        if (previousSpeakerVolume != -1 && previousSpeakerVolume != currentSpeakerVolume) {
//            //TODO: Restart background service
//        }
//
//
//        if (targetDecibals - micVolume > ERROR_DB) {
//            currentSpeakerVolume += VOLUME_CHANGE;
//        } else if (micVolume - targetDecibals < ERROR_DB) {
//            currentSpeakerVolume += VOLUME_CHANGE;
//        }
//
//        setSpeakerVolume(currentSpeakerVolume);
//        previousSpeakerVolume = currentSpeakerVolume;
//

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
}
