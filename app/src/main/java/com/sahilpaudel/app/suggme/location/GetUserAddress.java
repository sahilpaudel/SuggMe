package com.sahilpaudel.app.suggme.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.sahilpaudel.app.suggme.SharedPrefSuggMe;

import java.util.List;
import java.util.Locale;

import static android.test.mock.MockPackageManager.PERMISSION_GRANTED;

/**
 * Created by Sahil Paudel on 3/22/2017.
 */

public class GetUserAddress {

    Activity context;
    GPSTracker gps;

    private String city;
    private String state;
    private String country;

    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    public GetUserAddress(Activity context) {
        this.context = context;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void executeGPS() {

        if (Build.VERSION.SDK_INT < 23) {

            gps = new GPSTracker(context);
            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                getFullAddress(latitude,longitude);
            } else {
                gps.showSettingsAlert();
            }
        } else {
            try {
                if (ActivityCompat.checkSelfPermission(context, mPermission) != PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context, new String[]{mPermission},
                            REQUEST_CODE_PERMISSION);
                    // If any permission above not allowed by user, this condition will
                    //execute every time, else your else part will work
                    return;
                }
            }catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

                gps = new GPSTracker(context);
                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    getFullAddress(latitude,longitude);
                } else {
                    gps.showSettingsAlert();
                }
        }
    }

    private void getFullAddress(double lat, double lon) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {

            List<Address> address = geocoder.getFromLocation(lat,lon,1);
            if (address.size() > 0) {
                city = address.get(0).getSubAdminArea();
                state = address.get(0).getAdminArea();
                country = address.get(0).getCountryName();
                SharedPrefSuggMe.getInstance(context).setState(state);
            }

        }catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
