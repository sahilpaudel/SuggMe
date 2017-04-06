package com.sahilpaudel.app.suggme.profile;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sahilpaudel.app.suggme.Config;
import com.sahilpaudel.app.suggme.R;
import com.sahilpaudel.app.suggme.SharedPrefSuggMe;
import com.sahilpaudel.app.suggme.profile.useranswers.UserAnswersFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by SAKSHI on 3/8/2017.
 */

public class ProfilePage extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    Fragment fragment;
    FragmentTransaction transaction;

    TextView tvQuestions;
    TextView tvAnswers;
    TextView tvUser;

    String firstName;
    String lastName;
    String gender;
    String age;
    String state;
    String city;
    String country;
    String image_url;
    String description;
    Bundle bundle;

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private TextView tvUserLocation;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private ImageView ivUserImage;


    String user_id;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_main);
        user_id = SharedPrefSuggMe.getInstance(this).getUserId();

        try{
            String temp_user_id = getIntent().getStringExtra("USER_ID");
            Toast.makeText(this, temp_user_id, Toast.LENGTH_SHORT).show();

            if (temp_user_id != null)
                user_id = temp_user_id;

        }catch (NullPointerException e){
            e.printStackTrace();
        }


        //fetch user informations
        getUserInfo(user_id);

        tvQuestions = (TextView)findViewById(R.id.tvQuestions);
        tvAnswers = (TextView)findViewById(R.id.tvAnswers);
        tvUser = (TextView)findViewById(R.id.tvProfile);
        tvQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new UserQuestionsFragment();
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentFragment, fragment);
                transaction.commit();

                findViewById(R.id.tvQuestions).setBackgroundColor(Color.parseColor("#26a69a"));
                findViewById(R.id.tvAnswers).setBackgroundColor(Color.parseColor("#4db6ac"));
                findViewById(R.id.tvProfile).setBackgroundColor(Color.parseColor("#4db6ac"));
            }
        });
        tvAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new UserAnswersFragment();
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentFragment, fragment);
                transaction.commit();

                findViewById(R.id.tvQuestions).setBackgroundColor(Color.parseColor("#4db6ac"));
                findViewById(R.id.tvAnswers).setBackgroundColor(Color.parseColor("#26a69a"));
                findViewById(R.id.tvProfile).setBackgroundColor(Color.parseColor("#4db6ac"));
            }
        });
        tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new UserInfo();
                fragment.setArguments(bundle);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentFragment, fragment);
                transaction.commit();

                findViewById(R.id.tvQuestions).setBackgroundColor(Color.parseColor("#4db6ac"));
                findViewById(R.id.tvAnswers).setBackgroundColor(Color.parseColor("#4db6ac"));
                findViewById(R.id.tvProfile).setBackgroundColor(Color.parseColor("#26a69a"));
            }
        });

        fragment = new UserInfo();
        fragment.setArguments(bundle);
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentFragment, fragment);
        transaction.commit();
        findViewById(R.id.tvQuestions).setBackgroundColor(Color.parseColor("#4db6ac"));
        findViewById(R.id.tvAnswers).setBackgroundColor(Color.parseColor("#4db6ac"));
        findViewById(R.id.tvProfile).setBackgroundColor(Color.parseColor("#26a69a"));

        bindActivity();

        mAppBarLayout.addOnOffsetChangedListener(this);

        mToolbar.inflateMenu(R.menu.menu_main);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
    }



    private void bindActivity() {
        mToolbar        = (Toolbar) findViewById(R.id.main_toolbar);
        mTitle          = (TextView) findViewById(R.id.main_textview_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout   = (AppBarLayout) findViewById(R.id.app_bar_layout);
        ivUserImage = (ImageView)findViewById(R.id.ivUserImage);
        tvUserLocation = (TextView)findViewById(R.id.userLocation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    private void getUserInfo(final String temp_user_id) {

        final ProgressDialog progress = ProgressDialog.show(ProfilePage.this, "Please wait.", "Loading User Information...", false, false);
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_GET_USER_INFO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()) {
                    try {

                        JSONArray array = new JSONArray(response);

                        JSONObject object = array.getJSONObject(0);
                         firstName = object.getString("first_name");
                         lastName = object.getString("last_name");

                         //setting user name
                         mTitle.setText(firstName+" "+lastName);

                         gender = object.getString("gender");
                         age = object.getString("age");
                         state = object.getString("state");
                         city = object.getString("city");
                         country = object.getString("country");

                         tvUserLocation.setText(city+", "+state+", "+country);
                         description = object.getString("description");
                         image_url = object.getString("image_url");

                         bundle = new Bundle();
                         bundle.putString("FIRST_NAME", firstName);
                         bundle.putString("LAST_NAME", lastName);
                         bundle.putString("GENDER", gender);
                         bundle.putString("DESCRIPTION", description);
                         bundle.putString("CITY", city);
                         bundle.putString("STATE", state);
                         bundle.putString("COUNTRY", country);

                         if (image_url != null)
                             Picasso.with(ProfilePage.this).load(image_url).into(ivUserImage);
                         else
                             Picasso.with(ProfilePage.this).load("http://weshapelife.org/wp-content/uploads/2016/03/CYHZY4IWsAA4xSD-1.jpg").into(ivUserImage);

                         bundle.putString("IMAGE_URL", image_url);
                         bundle.putString("AGE", age);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progress.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", temp_user_id);

                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ProfilePage.this);
        queue.add(request);
    }

}
