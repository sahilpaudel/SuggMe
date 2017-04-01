package com.sahilpaudel.app.suggme;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessTokenTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.sahilpaudel.app.suggme.location.GetUserAddress;
import com.sahilpaudel.app.suggme.mainquestionpage.MainFragment;
import com.sahilpaudel.app.suggme.notifications.NotificationFragment;
import com.sahilpaudel.app.suggme.profile.FragmentProfile;
import com.sahilpaudel.app.suggme.profile.ProfilePage;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView userNavigationPic;
    TextView userName,showAddress;

    GetUserAddress getUserAddress;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserAddress = new GetUserAddress(this);
        getUserAddress.executeGPS();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment mainFragment = new MainFragment();
        if (mainFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentFragment,mainFragment);
            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        View navHeader;
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);

        userNavigationPic = (ImageView)navHeader.findViewById(R.id.imageView);
        userName = (TextView)navHeader.findViewById(R.id.userName);
        showAddress = (TextView)navHeader.findViewById(R.id.showAddress);

        userName.setText(SharedPrefSuggMe.getInstance(MainActivity.this).getUserName());
        showAddress.setText(getUserAddress.getCity()+", "+getUserAddress.getState()+", "+getUserAddress.getCountry());
        Picasso.with(this).load(SharedPrefSuggMe.getInstance(this).getImageUrl()).into(userNavigationPic);

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        FragmentTransaction transaction;

        if (id == R.id.nav_home) {
            fragment = new MainFragment();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentFragment, fragment);
            transaction.addToBackStack("main_fragment");
            transaction.commit();
        } else if (id == R.id.nav_profile) {
//            fragment = new FragmentProfile();
//            transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.contentFragment, fragment);
//            transaction.addToBackStack("user_profile");
//            transaction.commit();
            startActivity(new Intent(this, ProfilePage.class));
        } else if (id == R.id.nav_notifications) {
            fragment = new NotificationFragment();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentFragment, fragment);
            transaction.addToBackStack("notification");
            transaction.commit();
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            logout();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        if (SharedPrefSuggMe.getInstance(this).getProvider().equals("F")) {
            LoginManager.getInstance().logOut();
            SharedPrefSuggMe.getInstance(this).deleteSession();
            startActivity(new Intent(this, LoginActivity.class));
        } else if (SharedPrefSuggMe.getInstance(this).getProvider().equals("G")){

        } else {
            SharedPrefSuggMe.getInstance(this).deleteSession();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


}
