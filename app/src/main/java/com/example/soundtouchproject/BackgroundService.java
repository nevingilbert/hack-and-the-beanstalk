package com.example.soundtouchproject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

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

public class BackgroundService extends IntentService {

    public BackgroundService(){
        super("Background Speaker Service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    public static void sendNotification(String title, String body, Context context) {
        // prepare intent which is triggered if the
        // notification is selected

        //Intent intent = new Intent(this, NotificationReceiver.class);
        //PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(body)
//                .setContentIntent(pIntent)
                .setAutoCancel(true).build();


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

    private void setSpeakerVolume(int volume){
        // Add the request to the RequestQueue.
        // Request a string response from the provided URL.
        final String requestBody = String.format(Locale.US, "<volume>%d</volume>", volume);
        final String url = "http://192.168.1.14:8090/volume";
        final RequestQueue queue = Volley.newRequestQueue(this);
        final Context context = this;

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
        }){
            @Override
            public Map<String, String> getParams(){
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
