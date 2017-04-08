package com.sahilpaudel.app.suggme.profile;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.sahilpaudel.app.suggme.location.GetUserAddress;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfo extends Fragment {

    View view;
    TextView tvUserName, tvGender, tvAddress, tvWeb, tvDescription;
    ImageView ivEditProfile;

    String mUserId, mFirstName, mLastName, mGender, mWeb, mDescription, mAddress;
    String mCity, mState, mCountry;

    public UserInfo() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_info, container, false);

        GetUserAddress gua = new GetUserAddress(getActivity());
        gua.executeGPS();
        mCity = gua.getCity();
        mState = gua.getState();
        mCountry = gua.getCountry();

        try{
            mUserId = getArguments().getString("USER_ID");
            mFirstName = getArguments().getString("FIRST_NAME");
            mLastName = getArguments().getString("LAST_NAME");
            mGender = getArguments().getString("GENDER");
            mWeb = getArguments().getString("WEB");
            mDescription = getArguments().getString("DESCRIPTION");
            mAddress = getArguments().getString("ADDRESS");

        } catch (NullPointerException e){
            e.printStackTrace();
        }

        tvUserName = (TextView)view.findViewById(R.id.tvUserName);
        tvGender = (TextView)view.findViewById(R.id.tvGender);
        tvAddress = (TextView)view.findViewById(R.id.tvAddress);
        tvWeb = (TextView)view.findViewById(R.id.tvWeb);
        tvDescription = (TextView)view.findViewById(R.id.tvDescription);

        ivEditProfile = (ImageView)view.findViewById(R.id.ivEditProfile);
        if (SharedPrefSuggMe.getInstance(getActivity()).getUserId().equals(mUserId)) {
            ivEditProfile.setVisibility(View.VISIBLE);
        }

        try {
            tvUserName.setText(mFirstName + " " + mLastName);
            if (mGender == null)
                mGender = "No Gender Specified";
            if (Objects.equals(mGender, "M"))
                mGender = "Male";
            if (Objects.equals(mGender, "F"))
                mGender = "Female";
            if (Objects.equals(mGender, "O"))
                mGender = "Not Specified";
            tvGender.setText(mGender);

            if (Objects.equals(mAddress, null + ", " + null + ", " + null))
                mAddress = "No Address";
            tvAddress.setText(mAddress);

            if (mWeb == null)
                mWeb = "No Website";
            tvWeb.setText(mWeb);

            if (mDescription == null)
                mDescription = "Write Your Bio";
            tvDescription.setText(mDescription);

        }catch (NullPointerException e) {
            e.printStackTrace();
        }

        ivEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileDialog(mFirstName, mLastName,mGender, mWeb, mDescription);
            }
        });

        return view;
    }

    private void showEditProfileDialog(String fname, String lname, String gender, String web, String description) {

        final EditText etFirstName, etLastName, etEmail, etGender, etPhone, etBlogAddress, etShortBio, etCity, etState, etCountry;

        final AlertDialog.Builder updateUserInfo = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_profile,null);
        updateUserInfo.setView(dialogView);

        etFirstName = (EditText)dialogView.findViewById(R.id.et_FirstName);
        etLastName = (EditText)dialogView.findViewById(R.id.et_LastName);
        etEmail = (EditText)dialogView.findViewById(R.id.et_Email);
        etGender = (EditText)dialogView.findViewById(R.id.et_Gender);
        etPhone = (EditText)dialogView.findViewById(R.id.et_Phone);
        etBlogAddress = (EditText)dialogView.findViewById(R.id.et_Web);
        etShortBio = (EditText)dialogView.findViewById(R.id.et_Description);
        etCity = (EditText)dialogView.findViewById(R.id.et_City);
        etState = (EditText)dialogView.findViewById(R.id.et_State);
        etCountry = (EditText)dialogView.findViewById(R.id.et_Country);

        etFirstName.setText(fname);
        etLastName.setText(lname);
        etBlogAddress.setText(web);
        etShortBio.setText(description);
        etGender.setText(gender);
        etCity.setText(mCity);
        etState.setText(mState);
        etCountry.setText(mCountry);

        final Button updateButton = (Button)dialogView.findViewById(R.id.dialog_update_button);
        final Button cancelButton = (Button)dialogView.findViewById(R.id.dialog_cancel_button);
        final AlertDialog b = updateUserInfo.create();
        b.setCanceledOnTouchOutside(false);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo(etFirstName.getText().toString(), etLastName.getText().toString(),
                               etEmail.getText().toString(), etGender.getText().toString(),
                               etPhone.getText().toString(), etBlogAddress.getText().toString(),
                               etShortBio.getText().toString(),etCity.getText().toString(), etState.getText().toString(),
                               etCountry.getText().toString(), b);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });
        b.show();
    }

    private void updateUserInfo(final String firstName, final String lastName, final String Email,
                                final String Gender, final String Phone, final String web, final String shortBio,
                                final String city, final String state, final String country, final AlertDialog b) {
        final ProgressDialog progress = ProgressDialog.show(getActivity(), "Please wait...", "Updating Informations...", false, false);
        StringRequest request  = new StringRequest(Request.Method.POST, Config.URL_UPDATE_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("1")) {
                    b.dismiss();
                    Intent intent = new Intent(getActivity(), ProfilePage.class);
                    intent.putExtra("USER_ID", SharedPrefSuggMe.getInstance(getActivity()).getUserId());
                    startActivity(intent);
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefSuggMe.getInstance(getActivity()).getUserId());
                params.put("first_name", firstName);
                params.put("last_name", lastName);
                params.put("email", Email);
                params.put("gender", Gender);
                params.put("contact", Phone);
                params.put("web", web);
                params.put("short_bio", shortBio);
                params.put("city", city);
                params.put("state", state);
                params.put("country", country);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }
}
