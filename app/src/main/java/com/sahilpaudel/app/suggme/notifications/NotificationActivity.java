package com.sahilpaudel.app.suggme.notifications;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sahilpaudel.app.suggme.ClickListener;
import com.sahilpaudel.app.suggme.Config;
import com.sahilpaudel.app.suggme.R;
import com.sahilpaudel.app.suggme.RecyclerTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerViewNotification;

    List<NotificationInfo> mNotificationList;
    NotificationAdapter mNotificationAdapter;
    ProgressDialog mProgressDialog;
    StringRequest mNotificationRequest;
    RequestQueue mNotificationQueue;
    NotificationInfo mNotificationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        recyclerViewNotification = (RecyclerView)findViewById(R.id.recyclerViewNotification);
        mNotificationList = new ArrayList<>();

        mProgressDialog = ProgressDialog.show(NotificationActivity.this, "Please wait...", "Loading Notifications...", false, false);
        //send request for notification data
        mNotificationRequest = new StringRequest(Request.Method.POST, Config.URL_GET_NOTIFICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        mNotificationInfo = new NotificationInfo();

                        mNotificationInfo.NotificationContent = object.getString("notification_content");
                        mNotificationInfo.NotificationID = object.getString("notification_id");
                        mNotificationInfo.NotificationImage = object.getString("notification_image");
                        mNotificationInfo.NotificationTime = object.getString("notificationTime");
                        mNotificationInfo.NotificationStatus = object.getString("notification_status");

                        mNotificationList.add(mNotificationInfo);
                    }
                    mNotificationAdapter = new NotificationAdapter(NotificationActivity.this, mNotificationList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(NotificationActivity.this);
                    recyclerViewNotification.setLayoutManager(layoutManager);
                    recyclerViewNotification.setItemAnimator(new DefaultItemAnimator());
                    recyclerViewNotification.setAdapter(mNotificationAdapter);
                    mNotificationAdapter.notifyDataSetChanged();

                    recyclerViewNotification.addOnItemTouchListener(new RecyclerTouchListener(NotificationActivity.this, recyclerViewNotification, new ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            NotificationInfo info = mNotificationList.get(position);
                            setNotificationRead(info.NotificationID);
                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mProgressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Log.d("Notification ", "Error : "+error);
            }
        }){

        };
        mNotificationRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mNotificationQueue = Volley.newRequestQueue(NotificationActivity.this);
        mNotificationQueue.add(mNotificationRequest);
    }

    private void setNotificationRead (final String notification_id) {

        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_SET_READ_NOTIFICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("notification_id", notification_id);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(NotificationActivity.this);
        queue.add(request);
    }
}
