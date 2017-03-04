package com.sahilpaudel.app.suggme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    SignInButton gpButton;
    LoginButton fbButton;

    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;

    private static final int RC_SIGN_IN = 0511;
    private static final int FB_SIGN_IN = 1994;
    private GoogleApiClient signInApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_login);


        fbButton = (LoginButton) findViewById(R.id.bt_facebook);
        gpButton = (SignInButton) findViewById(R.id.bt_google);
        gpButton.setOnClickListener(this);


        //check whether user is logged In
        if (SharedPrefSuggMe.getInstance(this).isLoggedIn().equals("1")) {
            startActivity(new Intent(this,MainActivity.class));
            return;
        }else {
            Toast.makeText(this, "NOT LOGGED IN", Toast.LENGTH_SHORT).show();
        }

        //start of facebook Integraion
        fbButton.setReadPermissions(Arrays.asList("public_profile, email, user_friends," +
                "user_location,user_hometown"));
        callbackManager = CallbackManager.Factory.create();

        fbButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                try{

                                    String email = object.getString("email");
                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    String fname = object.getString("first_name");
                                    String lname = object.getString("last_name");
                                    String gender = object.getString("gender");
                                    String imageUrl = "https://graph.facebook.com/" + id + "/picture?type=large";

                                    SharedPrefSuggMe.getInstance(getApplicationContext()).addUserInfo(
                                            id,fname,lname,email,"F",imageUrl
                                    );
                                    //check if user exist
                                    isExist(fname, lname, email, id,"F","","",gender);

                                }catch (Exception ex){
                                    Toast.makeText(LoginActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday, first_name, last_name, location, work, education, age_range, timezone, hometown");
                request.setParameters(parameters);
                request.executeAsync();

                //Access Token to manage Logout
                accessTokenTracker = new AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                        if(currentAccessToken == null) {
                            SharedPrefSuggMe.getInstance(getApplicationContext()).deleteSession();
                        }
                    }
                };
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //end of facebook integration

        //start of google+ login
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        signInApi = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();
        gpButton.setSize(SignInButton.SIZE_STANDARD);
        gpButton.setScopes(signInOptions.getScopeArray());
        //end of google+ login

    }

    private void GoogleSignIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(signInApi);
        startActivityForResult(intent,RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if(result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();
            String first_name = account.getGivenName();
            String last_name = account.getFamilyName();
            String profileImage = account.getPhotoUrl().toString();
            String email = account.getEmail();
            String gid = account.getId();
            String token = account.getIdToken();


            //creating session
            SharedPrefSuggMe.getInstance(getApplicationContext()).addUserInfo(
                    gid,first_name,last_name,email,"G",profileImage
            );

            //check if user exist
            isExist(first_name, last_name, email, gid,"G","","","");
            //redirect user to nect page
            startActivity(new Intent(getApplicationContext(), MainActivity.class));


        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(googleSignInResult);

        }
        else
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(signInApi);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_google :
                GoogleSignIn();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //method to send user data to server to complete the registeration process using google or facebook
    private void saveUserInfo(final String first_name, final String last_name,
                              final String email, final String id, final String reg_from,
                              final String lat, final String lon, final String gender) {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_CREATE_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("1")) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("first_name",first_name);
                map.put("last_name", last_name);
                map.put("email",email);
                map.put("id",id);
                map.put("lat",lat);
                map.put("lon",lon);
                map.put("reg_from",reg_from);
                map.put("gender", gender);
                return map;
            }
        };
        queue.add(request);
    }


    private void isExist(final String first_name, final String last_name,
                         final String email, final String id, final String reg_from,
                         final String lat, final String lon, final String gender) {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_USER_ISEXIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("1")) {
                        Toast.makeText(LoginActivity.this, "Old User", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    Toast.makeText(LoginActivity.this, "New User", Toast.LENGTH_SHORT).show();
                    //if not exist create new user
                    saveUserInfo(first_name, last_name, email, id, reg_from, lat, lon, gender);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("email",email);
                map.put("id",id);
                return map;
            }
        };
        queue.add(request);
    }

}
