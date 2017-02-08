package com.sahilpaudel.app.suggme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Sahil Paudel on 2/2/2017.
 */

public class MyListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<GetFeedItem> list;

    public MyListAdapter(Activity activity, List<GetFeedItem> list) {
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(inflater == null) {
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.user_feeds_view, null);
        }

        TextView name = (TextView)convertView.findViewById(R.id.tv_username);
        TextView timestamp = (TextView)convertView.findViewById(R.id.tv_timestamp);
        TextView question = (TextView)convertView.findViewById(R.id.tv_topRatedQuestion);
        TextView answer = (TextView)convertView.findViewById(R.id.tv_topRatedAnswer);
        Button logout = (Button)convertView.findViewById(R.id.buttonLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null) {
                    LoginManager.getInstance().logOut();
                    gotoLogin(activity);
                }
            }
        });

        GetFeedItem feedItem = list.get(position);
        name.setText(feedItem.getUserName());

        CharSequence timeSequence = DateUtils.getRelativeTimeSpanString(Long.parseLong(feedItem.getTimeStamp()),System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS);
        timestamp.setText(timeSequence);

        question.setText(feedItem.getQuestion());
        answer.setText(feedItem.getAnswer());

        return convertView;
    }

    public static void gotoLogin(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }
}
