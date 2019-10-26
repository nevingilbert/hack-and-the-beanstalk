package com.example.soundtouchproject;

import android.content.Context;
import android.media.MediaRecorder;

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

import static com.example.soundtouchproject.BackgroundService.sendNotification;

public class VolumeThread implements Runnable {

    private Context context;
    private MediaRecorder mic;

    public VolumeThread(Context context, MediaRecorder mic) {
        this.context = context;
        this.mic = mic;
    }

    @Override
    public void run() {

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
                sendNotification("Network Request error", error.getLocalizedMessage(), context);
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
