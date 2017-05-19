package com.sahilpaudel.app.suggme;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

public class MyLoadingActivity extends AppCompatActivity {

    int screenWidth;
    private ObjectAnimator waveOneAnimator;
    private ObjectAnimator waveTwoAnimator;
    private ObjectAnimator waveThreeAnimator;

    private TextView hangoutTvOne;
    private TextView hangoutTvTwo;
    private TextView hangoutTvThree;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_loading);

        //********* hangout ************
        hangoutTvOne = (TextView) findViewById(R.id.hangoutTvOne);
        hangoutTvTwo = (TextView) findViewById(R.id.hangoutTvTwo);
        hangoutTvThree = (TextView) findViewById(R.id.hangoutTvThree);

        hangoutTvOne.setVisibility(View.GONE);
        hangoutTvTwo.setVisibility(View.GONE);
        hangoutTvThree.setVisibility(View.GONE);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;

        hangoutTvOne.setVisibility(View.VISIBLE);
        hangoutTvTwo.setVisibility(View.VISIBLE);
        hangoutTvThree.setVisibility(View.VISIBLE);
        waveAnimation();

    }

    private void waveAnimation() {

        PropertyValuesHolder tvOne_Y = PropertyValuesHolder.ofFloat(hangoutTvOne.TRANSLATION_Y, -40.0f);
        PropertyValuesHolder tvOne_X = PropertyValuesHolder.ofFloat(hangoutTvOne.TRANSLATION_X, 0);
        waveOneAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvOne, tvOne_X, tvOne_Y);
        waveOneAnimator.setRepeatCount(-1);
        waveOneAnimator.setRepeatMode(ValueAnimator.REVERSE);
        waveOneAnimator.setDuration(300);
        waveOneAnimator.start();

        PropertyValuesHolder tvTwo_Y = PropertyValuesHolder.ofFloat(hangoutTvTwo.TRANSLATION_Y, -40.0f);
        PropertyValuesHolder tvTwo_X = PropertyValuesHolder.ofFloat(hangoutTvTwo.TRANSLATION_X, 0);
        waveTwoAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvTwo, tvTwo_X, tvTwo_Y);
        waveTwoAnimator.setRepeatCount(-1);
        waveTwoAnimator.setRepeatMode(ValueAnimator.REVERSE);
        waveTwoAnimator.setDuration(300);
        waveTwoAnimator.setStartDelay(100);
        waveTwoAnimator.start();

        PropertyValuesHolder tvThree_Y = PropertyValuesHolder.ofFloat(hangoutTvThree.TRANSLATION_Y, -40.0f);
        PropertyValuesHolder tvThree_X = PropertyValuesHolder.ofFloat(hangoutTvThree.TRANSLATION_X, 0);
        waveThreeAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvThree, tvThree_X, tvThree_Y);
        waveThreeAnimator.setRepeatCount(-1);
        waveThreeAnimator.setRepeatMode(ValueAnimator.REVERSE);
        waveThreeAnimator.setDuration(300);
        waveThreeAnimator.setStartDelay(200);
        waveThreeAnimator.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        if (waveOneAnimator != null && waveTwoAnimator != null && waveThreeAnimator != null) {
            waveOneAnimator.cancel();
            waveOneAnimator.removeAllListeners();

            waveTwoAnimator.cancel();
            waveTwoAnimator.removeAllListeners();

            waveThreeAnimator.cancel();
            waveThreeAnimator.removeAllListeners();
        }

    }
}
