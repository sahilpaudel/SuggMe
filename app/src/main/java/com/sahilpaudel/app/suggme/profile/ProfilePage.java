package com.sahilpaudel.app.suggme.profile;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sahilpaudel.app.suggme.MainActivity;
import com.sahilpaudel.app.suggme.R;
import com.sahilpaudel.app.suggme.SharedPrefSuggMe;


/**
 * Created by SAKSHI on 3/8/2017.
 */

public class ProfilePage extends AppCompatActivity implements View.OnClickListener {

    Fragment fragment;
    FragmentTransaction transaction;

    TextView tvQuestions;
    TextView tvAnswers;
    TextView tvVisited;

    TextView tvUserName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_main);
        tvQuestions = (TextView)findViewById(R.id.userQuestions);
        tvAnswers = (TextView)findViewById(R.id.userAnswers);
        tvVisited = (TextView)findViewById(R.id.userVisited);

        tvUserName = (TextView)findViewById(R.id.userName);
        tvUserName.setText(
                SharedPrefSuggMe.getInstance(this).getUserName()
        );
        tvQuestions.setOnClickListener(this);
        tvAnswers.setOnClickListener(this);
        tvVisited.setOnClickListener(this);

        fragment = new UserQuestionsFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentFragment, fragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.userQuestions : {
                    fragment = new UserQuestionsFragment();
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.contentFragment, fragment);
                    transaction.commit();
                }
                break;
            case R.id.userAnswers : {
                    fragment = new UserAnswersFragment();
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.contentFragment, fragment);
                    transaction.commit();
                }
                break;
            case R.id.userVisited : {
                    fragment = new UserVisitedPlaces();
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.contentFragment, fragment);
                    transaction.commit();
                }
                break;
        }
    }
}
