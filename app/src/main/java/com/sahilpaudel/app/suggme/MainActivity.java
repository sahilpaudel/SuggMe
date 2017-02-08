package com.sahilpaudel.app.suggme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    ListView mListView;
    List<GetFeedItem> list;
    MyListAdapter mListAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<GetFeedItem>();
        mListAdapter = new MyListAdapter(this,list);
        mListView = (ListView) findViewById(R.id.list_feeds);
        mListView.setAdapter(mListAdapter);


        GetFeedItem feedItem = new GetFeedItem();
        feedItem.setId(11303588);
        feedItem.setUserName("The High Paudel");
        feedItem.setQuestion("What is the capital of Czechoslovakia ?");
        feedItem.setAnswer("Prague");
        feedItem.setTimeStamp("1403375851930");
        list.add(feedItem);
    }

}
