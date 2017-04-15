package com.sahilpaudel.app.suggme.notifications;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.sahilpaudel.app.suggme.Config;
import com.sahilpaudel.app.suggme.SharedPrefSuggMe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sahil Paudel on 4/12/2017.
 */

public class NotificationApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.WARN);

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new MyNotificationOpenedHandler())
                .setNotificationReceivedHandler(new MyNotificationReceivedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .autoPromptLocation(true)
                .init();

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                SharedPrefSuggMe.getInstance(getApplicationContext()).saveDeviceToken(userId);
                if (SharedPrefSuggMe.getInstance(getApplicationContext()).getDeviceToken() != null)
                    saveDeviceToken(SharedPrefSuggMe.getInstance(getApplicationContext()).getDeviceToken());
            }
        });
    }

    private void saveDeviceToken (final String deviceToken) {

        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_SAVE_DEVICE_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(NotificationApplication.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error : ", "DEVICE TOKEN : "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token_id", deviceToken);
                params.put("user_id", SharedPrefSuggMe.getInstance(getApplicationContext()).getUserId());
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {

            JSONObject data = result.notification.payload.additionalData;
            Log.d("Notification ","data : "+data.toString());

                 // The following can be used to open an Activity of your choice.
                 Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                 getApplicationContext().startActivity(intent);

        }
    }

    //This will be called when a notification is received while your app is running.
    public class MyNotificationReceivedHandler  implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            JSONObject data = notification.payload.additionalData;
            String body = notification.payload.body;

            if (data != null) {
                try {
                    String imageUrl = data.getString("image_url");
                    //save notification data
                    saveNotification(body, imageUrl);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //save notification to database
    private void saveNotification(final String content, final String imageurl) {

        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_SAVE_NOTIFICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response : "," "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error : "," "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("content", content);
                params.put("image_url", imageurl);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
}
