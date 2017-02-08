package com.sahilpaudel.app.suggme;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActivityProfile extends AppCompatActivity {

    CollapsingToolbarLayout mCollapsingToolbarLayout;
    TabLayout mTabLayout;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        mCollapsingToolbarLayout.setTitle("Sahil Paudel");

        mTabLayout = (TabLayout) findViewById(R.id.tbl_profile);
        mTabLayout.addTab(mTabLayout.newTab().setText("MY DETAILS"));
        mTabLayout.addTab(mTabLayout.newTab().setText("MY ANSWERS"));
        mTabLayout.addTab(mTabLayout.newTab().setText("MY FAVORITES"));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


    }
}
