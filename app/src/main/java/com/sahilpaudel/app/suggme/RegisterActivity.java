package com.sahilpaudel.app.suggme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText etFirstName;
    EditText etLastName;
    EditText etEmail;
    EditText etPassword;
    EditText etGender;
    Button   btRegister;

    String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = (EditText)findViewById(R.id.et_FirstName);
        etLastName = (EditText)findViewById(R.id.et_LastName);
        etEmail = (EditText)findViewById(R.id.et_Email);
        etGender = (EditText)findViewById(R.id.et_Gender);
        etPassword = (EditText)findViewById(R.id.et_Password);
        btRegister = (Button)findViewById(R.id.bt_Register);

        etFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!(Pattern.matches("^[\\p{L} .'-]+$", charSequence))) {
                    etFirstName.setError("Only Characters allowed");
                    btRegister.setEnabled(false);
                } else {
                    btRegister.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!(Pattern.matches("^[\\p{L} .'-]+$", charSequence))) {
                    etLastName.setError("Only Characters allowed");
                    btRegister.setEnabled(false);
                } else {
                    btRegister.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String email = etEmail.getText().toString();
                String passWord = etPassword.getText().toString();
                String gender = etGender.getText().toString();

                if (firstName.isEmpty()) {
                    etFirstName.setError("Cannot be empty");
                    return;
                }else
                if (lastName.isEmpty()) {
                    etLastName.setError("Cannot be empty");
                    return;
                }else
                if (email.isEmpty() || !email.matches(emailRegex)) {
                    etEmail.setError("Invalid Email");
                    return;
                }else
                if (gender.isEmpty()) {
                    etGender.setError("Cannot be empty");
                    return;
                }else
                if (!gender.equals("M")) {
                    if (!gender.equals("F")) {
                        if (!gender.equals("O")) {
                            etGender.setError("Invalid Input");
                        }
                    }
                    return;
                }else
                if (passWord.isEmpty() && passWord.length() > 8) {
                    etPassword.setError("Length must be 8 characters");
                    return;
                }else
                if (!passWord.matches("^[a-zA-Z0-9_]*$")) {
                    etPassword.setError("Only 0-9 and a-z & A-Z are allowed");
                    return;
                }else{
                    isExist(firstName, lastName, email, passWord, gender);
                }

            }
        });

    }

    private void createUser(final String firstName, final String lastName, final String email, final String passWord, final String gender) {

        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_CREATE_USER_NORMAL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("1"))
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                else
                    Toast.makeText(RegisterActivity.this, "Unable To Create User", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, "Error : "+error, Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", firstName);
                params.put("last_name", lastName);
                params.put("email", email);
                params.put("password", passWord);
                params.put("gender", gender);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void isExist(final String firstName, final String lastName, final String email, final String passWord, final String gender) {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_USER_ISEXIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.equals("0")) {
                    Toast.makeText(RegisterActivity.this, "User Already Exist", Toast.LENGTH_SHORT).show();
                }else{
                    //if not exist create new user
                    createUser(firstName, lastName, email, passWord, gender);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("email",email);
                map.put("id","123456");
                return map;
            }
        };
        queue.add(request);
    }
}
