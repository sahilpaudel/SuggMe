package com.sahilpaudel.app.suggme.singlequestionpage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.sahilpaudel.app.suggme.ClickListener;
import com.sahilpaudel.app.suggme.Config;
import com.sahilpaudel.app.suggme.CustomProgressDialog;
import com.sahilpaudel.app.suggme.LoginActivity;
import com.sahilpaudel.app.suggme.R;
import com.sahilpaudel.app.suggme.RecyclerTouchListener;
import com.sahilpaudel.app.suggme.SharedPrefSuggMe;
import com.sahilpaudel.app.suggme.location.GetUserAddress;
import com.sahilpaudel.app.suggme.mainquestionpage.QuestionFeed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.R.attr.country;

public class AnswerActivity extends AppCompatActivity {

    ProgressDialog progress;
    ArrayList<AnswerFeed> answerFeeds;
    AnswerFeedAdapter answerFeedAdapter;
    RecyclerView recyclerViewAnswer;
    TextView tvQuestion, tvAskedOn, tvAnswerCount, tvalreadyAnswered;
    CardView alreadyAnsweredCard;
    Button btWriteAnswer;

    //to check whether user has already anwered or not
    boolean isAlreadyAnswered = false;

    //current timestamp
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String currentTime;

    String tempQuestionID;
    String tempQuestionContent;
    String tempAnswerContent;
    String tempAnswerID;

    RequestQueue getAnswerQueue;
    StringRequest getAnswerRequest;

    //application user_id
    String currentUserId;

    String question_id;
    String question_content;
    String question_date;

    //refresher
    SwipeRefreshLayout swipeRefreshLayoutAnswer;

    private final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    String questionAddress;
    TextView currentLocation;
    GetUserAddress getUserAddress;

    ImageView imageViewClose;
    ImageView imageViewEditQuestion;
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    FragmentManager manager;
    //CustomProgressDialog p;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);


        getUserAddress = new GetUserAddress(this);
        getUserAddress.executeGPS();

        String city = getUserAddress.getCity();
        String state = getUserAddress.getState();
        String country = getUserAddress.getCountry();
        questionAddress = city+", "+state+", "+country;

        //implement close activity on click
        imageViewClose = (ImageView)findViewById(R.id.closeActivity);
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                finish();
            }
        });

        imageViewEditQuestion = (ImageView)findViewById(R.id.ivEditQuestion);

        //to refresh
        swipeRefreshLayoutAnswer = (SwipeRefreshLayout)findViewById(R.id.swipeToRefreshAnswer);
        recyclerViewAnswer = (RecyclerView)findViewById(R.id.answerFeedRecycler);
        tvQuestion = (TextView)findViewById(R.id.questionContent);
        tvAskedOn = (TextView)findViewById(R.id.askedDate);
        tvAnswerCount = (TextView)findViewById(R.id.totalAnswerCount);
        btWriteAnswer = (Button)findViewById(R.id.writeAnswer);
        alreadyAnsweredCard = (CardView)findViewById(R.id.ifAlreadyWrittenCard);
        tvalreadyAnswered = (TextView)findViewById(R.id.ifAlreadyWritten);

        //edit  answer
        tvalreadyAnswered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetAnswerByQuestionUserId();
            }
        });

        //get current userId
        currentUserId = SharedPrefSuggMe.getInstance(AnswerActivity.this).getUserId();
        //get current system time
        currentTime = timestamp.toString();

        btWriteAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnswerDialog();
            }
        });

        question_id = getIntent().getStringExtra("QID");
        question_content = getIntent().getStringExtra("CONTENT");
        question_date = getIntent().getStringExtra("DATE");

        //assign question id to temp variable to write answer for this
        //question id
        tempQuestionID = question_id;
        tempQuestionContent = question_content;

        //when user clicks on answer button a pop-up appears where the user can
        //write their answer.
        btWriteAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnswerDialog();
            }
        });

        //update questions
        imageViewEditQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateQuestionDialog();
            }
        });

        //beautifying the time display
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date time =  df.parse(question_date);
            PrettyTime p = new PrettyTime();
            question_date = "Asked "+p.format(time);
        } catch (ParseException e) {
            //Toast.makeText(AnswerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //end of beautifying the time display

        getAnswerQueue = Volley.newRequestQueue(AnswerActivity.this);
        progress = ProgressDialog.show(AnswerActivity.this,"Please wait.","Feeding the answers...", false, false);

        answerFeeds = new ArrayList<>();
        //to get Answers which are active
        getAnswerRequest = new StringRequest(Request.Method.POST, Config.URL_GET_ANSWERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //stop refreshing when we get the data
                swipeRefreshLayoutAnswer.setRefreshing(false);
                //progress.dismiss();
                progress.dismiss();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);
                        AnswerFeed feed = new AnswerFeed();
                        feed.question_id = object.getString("question_id");
                        feed.answer_id = object.getString("answer_id");
                        feed.answer_content = object.getString("answer_content");
                        feed.entryOn = object.getString("entryOn");
                        feed.user_id = object.getString("user_id");
                        feed.question_title = tempQuestionContent;

                        //check if user has already answered the query
                        if (feed.user_id.equals(currentUserId)) {
                            //already answered
                            isAlreadyAnswered = true;
                            btWriteAnswer.setVisibility(View.INVISIBLE);
                            alreadyAnsweredCard.setVisibility(View.VISIBLE);
                        }
                        feed.first_name = object.getString("first_name");
                        feed.last_name = object.getString("last_name");
                        feed.image_url = object.getString("image_url");
                        feed.isActive = object.getString("isActive");
                        feed.isAnonymous = object.getString("isAnonymous");
                        feed.isUpdated = object.getString("isUpdated");
                        answerFeeds.add(feed);
                    }

                    answerFeedAdapter = new AnswerFeedAdapter(AnswerActivity.this,answerFeeds,transaction, question_id, question_content, question_date);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AnswerActivity.this);
                    recyclerViewAnswer.setLayoutManager(layoutManager);
                    recyclerViewAnswer.setItemAnimator(new DefaultItemAnimator());
                    recyclerViewAnswer.setAdapter(answerFeedAdapter);
                    answerFeedAdapter.notifyDataSetChanged();

                    swipeRefreshLayoutAnswer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            //clear old data
                            answerFeeds.clear();
                            answerFeedAdapter.notifyDataSetChanged();
                            //make new request
                            getAnswerQueue.add(getAnswerRequest);
                            //fil with new data
                            answerFeedAdapter = new AnswerFeedAdapter(AnswerActivity.this,answerFeeds,transaction, question_id, question_content, question_date);
                            recyclerViewAnswer.setAdapter(answerFeedAdapter);
                            answerFeedAdapter.notifyDataSetChanged();
                        }
                    });

                    // Configure the refreshing colors
                    swipeRefreshLayoutAnswer.setColorSchemeResources(android.R.color.holo_blue_bright,
                            android.R.color.holo_green_light,
                            android.R.color.holo_orange_light,
                            android.R.color.holo_red_light);

                    recyclerViewAnswer.addOnItemTouchListener(new RecyclerTouchListener(AnswerActivity.this, recyclerViewAnswer, new ClickListener() {
                        @Override
                        public void onClick(View view, int position) {

                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));

                } catch (JSONException e) {
                    Toast.makeText(AnswerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    //progress.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progress.dismiss();
                Log.e("ERROR : ",""+error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("question_id",question_id);
                return params;
            }
        };
        getAnswerQueue.add(getAnswerRequest);

        tvQuestion.setText(question_content);
        tvAskedOn.setText(question_date);
        tvAnswerCount.setText("Answers");
        
    }


    //alert dialog to write answer
    private void showAnswerDialog() {

        final AlertDialog.Builder writeAnswerDialog = new AlertDialog.Builder(AnswerActivity.this);
        LayoutInflater inflater = AnswerActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.write_answer_dialog,null);
        writeAnswerDialog.setView(dialogView);
        final TextView showQuestion = (TextView)dialogView.findViewById(R.id.dialog_question_tag);
        showQuestion.setText(tempQuestionContent);
        final EditText writeAnswer = (EditText)dialogView.findViewById(R.id.dialog_write_answer);
        final Button askButton = (Button)dialogView.findViewById(R.id.dialog_answer_button);
        final Button cancelButton = (Button)dialogView.findViewById(R.id.dialog_cancel_button);
        final AlertDialog b = writeAnswerDialog.create();
        b.setCanceledOnTouchOutside(false);

        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = writeAnswer.getText().toString();
                String userid = SharedPrefSuggMe.getInstance(AnswerActivity.this).getUserId();
                //check if content is blank.
                if (content.isEmpty()) {
                    writeAnswer.setError("It cannot be empty");
                }else{
                    b.dismiss();
                    createAnswer(content,userid);
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AnswerActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                b.dismiss();
            }
        });
        b.show();
    }

    //to create answers
    private void createAnswer (final String content, final String user_id) {

        progress = ProgressDialog.show(AnswerActivity.this,"Please wait.","Responding to need ones...", false, false);

        RequestQueue queue = Volley.newRequestQueue(AnswerActivity.this);
        final StringRequest request = new StringRequest(Request.Method.POST, Config.URL_CREATE_ANSWERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //progress.dismiss();
                progress.dismiss();
                if(response.equals("1")) {
                    //clear all the data in the list
                    answerFeeds.clear();
                    //make adpater empty
                    answerFeedAdapter.notifyDataSetChanged();
                    //make new volley request
                    getAnswerQueue.add(getAnswerRequest);
                    //call adapter class
                    answerFeedAdapter = new AnswerFeedAdapter(AnswerActivity.this,answerFeeds,transaction, question_id, question_content, question_date);
                    //set adapter
                    recyclerViewAnswer.setAdapter(answerFeedAdapter);
                    //populate the adapter;
                    answerFeedAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(AnswerActivity.this, response, Toast.LENGTH_LONG).show();
                    //progress.dismiss();
                    progress.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();

                Toast.makeText(AnswerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("content",content);
                params.put("quest_id",tempQuestionID);
                params.put("user_id",user_id);
                params.put("entryOn", currentTime);
                params.put("isActive","1");
                params.put("is_anonymous","0");
                return params;
            }
        };
        queue.add(request);
    }



    //to edit answers
    private void showUpdateDialog() {

        Toast.makeText(AnswerActivity.this, "Update Dialog", Toast.LENGTH_SHORT).show();
        final AlertDialog.Builder writeAnswerDialog = new AlertDialog.Builder(AnswerActivity.this);
        LayoutInflater inflater = AnswerActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.write_answer_dialog,null);
        writeAnswerDialog.setView(dialogView);
        final TextView showQuestion = (TextView)dialogView.findViewById(R.id.dialog_question_tag);
        showQuestion.setText(tempQuestionContent);
        final EditText writeAnswer = (EditText)dialogView.findViewById(R.id.dialog_write_answer);
        //set with old content, before user can edit
        writeAnswer.setText(tempAnswerContent);
        final Button askButton = (Button)dialogView.findViewById(R.id.dialog_answer_button);
        final Button cancelButton = (Button)dialogView.findViewById(R.id.dialog_cancel_button);
        final AlertDialog b = writeAnswerDialog.create();
        b.setCanceledOnTouchOutside(false);
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update the string
                String updatedAnswer = writeAnswer.getText().toString();
                //Toast.makeText(AnswerActivity.this, updatedAnswer, Toast.LENGTH_SHORT).show();
                updateAnswer(tempAnswerID, updatedAnswer, "0","1");
                b.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AnswerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                b.dismiss();
            }
        });
        b.show();
    }

    //updating the answer
    private void updateAnswer (final String ans_id, final String content,final String isAnonymous, final String isActive) {

        progress = ProgressDialog.show(AnswerActivity.this,"Please wait.","Updating the answer...", false, false);

        RequestQueue queue = Volley.newRequestQueue(AnswerActivity.this);
        final StringRequest request = new StringRequest(Request.Method.POST, Config.URL_UPDATE_ANSWERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("1")) {
                    //clear all the data in the list
                    answerFeeds.clear();
                    //make adpater empty
                    answerFeedAdapter.notifyDataSetChanged();
                    //make new volley request
                    getAnswerQueue.add(getAnswerRequest);
                    //call adapter class
                    answerFeedAdapter = new AnswerFeedAdapter(AnswerActivity.this,answerFeeds,transaction, question_id, question_content, question_date);
                    //set adapter
                    recyclerViewAnswer.setAdapter(answerFeedAdapter);
                    //populate the adapter;
                    answerFeedAdapter.notifyDataSetChanged();
                    progress.dismiss();
                }else{
                    Toast.makeText(AnswerActivity.this, response, Toast.LENGTH_LONG).show();
                    progress.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progress.dismiss();
                Toast.makeText(AnswerActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("content",content);
                params.put("ans_id",ans_id);
                params.put("entryOn", currentTime);
                params.put("isActive",isActive);
                params.put("is_anonymous",isAnonymous);
                return params;
            }
        };
        queue.add(request);
    }

    // // TODO: 4/3/2017 to show the hints
    private void GetAnswerByQuestionUserId() {

        progress = ProgressDialog.show(AnswerActivity.this,"Please wait.","Loading the updater", false, false);
        RequestQueue queue = Volley.newRequestQueue(AnswerActivity.this);
        final StringRequest request = new StringRequest(Request.Method.POST, Config.URL_GET_ANSWER_BY_QUESTION_USER_ID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    Log.d("RESPONSE",response);
                    JSONArray array = new JSONArray(response);
                    JSONObject object = array.getJSONObject(0);
                    tempAnswerContent = object.getString("answer_content");
                    tempAnswerID = object.getString("answer_id");
                    progress.dismiss();
                    showUpdateDialog();
                }catch (Exception e){
                    progress.dismiss();
                    Toast.makeText(AnswerActivity.this, "At GETANSWERBYID : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("GETBYID",e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progress.dismiss();
                Toast.makeText(AnswerActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("quest_id",tempQuestionID);
                params.put("user_id",currentUserId);
                return params;
            }
        };
        queue.add(request);
    }

    private void updateQuestion(final String question_id, final String question_data) {
        final ProgressDialog progressQuestionUpdate = ProgressDialog.show(this, "Please wait...", "Updating Question..", false, false);
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_UPDATE_QUESTIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressQuestionUpdate.dismiss();
                Intent intent = new Intent(getApplicationContext(), AnswerActivity.class);
                intent.putExtra("QID",question_id);
                intent.putExtra("CONTENT",response);
                intent.putExtra("DATE",question_date);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR : ", error+"");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("quest_id",question_id);
                params.put("content",question_data);
                params.put("question_address", questionAddress);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    Intent intent;
    //update Question Dialog
    private void showUpdateQuestionDialog() {

        Toast.makeText(AnswerActivity.this, "Update Dialog", Toast.LENGTH_SHORT).show();
        final AlertDialog.Builder writeQuestionDialog = new AlertDialog.Builder(AnswerActivity.this);
        LayoutInflater inflater = AnswerActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.ask_question_dialog, null);
        writeQuestionDialog.setView(dialogView);
        final AlertDialog b = writeQuestionDialog.create();

        //place autocomplete
        try {
            intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(this);
            //startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        }catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        final EditText askQuestion = (EditText)dialogView.findViewById(R.id.ask_question_dialog);
        askQuestion.setText(question_content);
        //select location
        currentLocation = (TextView)dialogView.findViewById(R.id.currentLocation);
        currentLocation.setText(questionAddress);
        Button changeLocation = (Button)dialogView.findViewById(R.id.changeLocation);

        //to show location option
        changeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        });
        Button btAskQuestion = (Button)dialogView.findViewById(R.id.ask_question_button);
        btAskQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (askQuestion.getText().toString().isEmpty()) {
                    askQuestion.setError("It cannot be empty");
                }else if(questionAddress == null){
                    askQuestion.setError("Select location");
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                }else {
                    updateQuestion(question_id,askQuestion.getText().toString());
                    b.dismiss();
                }
            }
        });

        b.setCanceledOnTouchOutside(false);
        b.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                questionAddress = place.getAddress().toString();
                currentLocation.setText(questionAddress);
                Log.i("PLACE", questionAddress);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("PLACE", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

}
