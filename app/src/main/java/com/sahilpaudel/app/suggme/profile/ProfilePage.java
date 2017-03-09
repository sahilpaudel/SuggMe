package com.sahilpaudel.app.suggme.profile;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.sahilpaudel.app.suggme.MainActivity;
import com.sahilpaudel.app.suggme.R;


/**
 * Created by SAKSHI on 3/8/2017.
 */

public class ProfilePage extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new FragmentProfileAdapter(getSupportFragmentManager(),
                ProfilePage.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }
}
