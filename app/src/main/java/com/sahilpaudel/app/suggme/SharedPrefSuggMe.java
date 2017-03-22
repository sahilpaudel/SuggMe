package com.sahilpaudel.app.suggme;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sahil Paudel on 2/22/2017.
 */

public class SharedPrefSuggMe {

    private static Context context;
    private static SharedPrefSuggMe mInstance;

    private static final String USER_PREF = "SharedPrefs";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private SharedPrefSuggMe(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(USER_PREF,Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefSuggMe getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new SharedPrefSuggMe(context);
        }
        return mInstance;
    }

    public boolean addUserInfo(String id, String firstName, String lastName, String email, String status, String imageUrl) {

        editor = preferences.edit();
        editor.putString("ID",id);
        editor.putString("FIRST_NAME",firstName);
        editor.putString("LAST_NAME",lastName);
        editor.putString("EMAIL",email);
        editor.putString("STATUS",status);
        editor.putString("IMAGE_URL",imageUrl);
        editor.putString("ISLOGGEDIN","1");
        editor.apply();
        return true;
    }

    public String getImageUrl() {
        return preferences.getString("IMAGE_URL","");
    }
    public String isLoggedIn() {
        return preferences.getString("ISLOGGEDIN","0");
    }
    public String getUserId() {
        return preferences.getString("USER_ID","0");
    }

    public boolean setUserId(String id) {
        editor = preferences.edit();
        editor.putString("USER_ID",id);
        editor.apply();
        return true;
    }
    public boolean setState(String state) {
        editor = preferences.edit();
        editor.putString("STATE",state);
        editor.apply();
        return true;
    }

    public String getState() {
        return preferences.getString("STATE","");
    }

    public String getUserName() {
        return preferences.getString("FIRST_NAME","") +" "+preferences.getString("LAST_NAME","");
    }

    public String getProvider() {
        return preferences.getString("STATUS","A");
    }

    public void deleteSession() {
        preferences.edit().clear();
    }
}
