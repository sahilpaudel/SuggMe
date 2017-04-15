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
    private static final String TAG_TOKEN = "TOKEN_ID";

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

    public boolean addUserInfo(String id, String firstName, String lastName, String email, String status, String imageUrl, String gender) {

        editor = preferences.edit();
        editor.putString("ID",id);
        editor.putString("FIRST_NAME",firstName);
        editor.putString("LAST_NAME",lastName);
        editor.putString("EMAIL",email);
        editor.putString("STATUS",status);
        editor.putString("GENDER",gender);
        editor.putString("IMAGE_URL",imageUrl);
        editor.putString("ISLOGGEDIN","1");
        editor.apply();
        return true;
    }

    public String getImageUrl() {
        return preferences.getString("IMAGE_URL","null.jpg");
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
        return preferences.getString("STATUS","D");
    }


    public void deleteSession() {
        preferences.edit().clear().apply();
    }


    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        editor = preferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        return  preferences.getString(TAG_TOKEN, null);
    }

    public boolean setRead(String str) {

        editor = preferences.edit();
        editor.putString("UNREAD", str);
        editor.apply();
        return true;
    }

    public String getReadStatus(){
        return  preferences.getString("UNREAD", "1");
    }
}
