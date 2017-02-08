package com.sahilpaudel.app.suggme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button bt_signin, bt_next;
    LoginButton fbButton;
    EditText etUserName, etPassword;
    ImageView iv_pf_image;

    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_login);

        iv_pf_image = (ImageView) findViewById(R.id.iv_pf_image);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);

        fbButton = (LoginButton) findViewById(R.id.bt_facebook);
        bt_signin = (Button) findViewById(R.id.btLogin);
        bt_next = (Button) findViewById(R.id.bt_next);

        bt_signin.setOnClickListener(this);
        bt_next.setOnClickListener(this);

        if(isLogin()) {
            bt_next.setVisibility(View.VISIBLE);
            bt_signin.setVisibility(View.INVISIBLE);
        }

        fbButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends," +
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
                                    String birthday = object.getString("birthday");
                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    String fname = object.getString("first_name");
                                    String lname = object.getString("last_name");
                                    String gender = object.getString("gender");

                                    JSONObject location = object.getJSONObject("location");
                                    String userLocation = location.getString("name");

                                    JSONObject hometown = object.getJSONObject("hometown");
                                    String homeLocation = hometown.getString("name");

                                    String imageUrl = "https://graph.facebook.com/" + id + "/picture?type=large";

                                    bt_next.setVisibility(View.VISIBLE);
                                    bt_signin.setVisibility(View.INVISIBLE);

                                }catch (Exception ex){

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
                            iv_pf_image.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
                            bt_next.setVisibility(View.INVISIBLE);
                            bt_signin.setVisibility(View.VISIBLE);
                        }
                    }
                };
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btLogin :
                break;
            case R.id.bt_next :
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}
