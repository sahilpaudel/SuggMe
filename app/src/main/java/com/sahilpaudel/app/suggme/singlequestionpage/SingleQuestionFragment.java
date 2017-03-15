package com.sahilpaudel.app.suggme.singlequestionpage;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sahilpaudel.app.suggme.ClickListener;
import com.sahilpaudel.app.suggme.Config;
import com.sahilpaudel.app.suggme.R;
import com.sahilpaudel.app.suggme.RecyclerTouchListener;
import com.sahilpaudel.app.suggme.SharedPrefSuggMe;
import com.sahilpaudel.app.suggme.mainquestionpage.QuestionFeedAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;
import org.w3c.dom.Text;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleQuestionFragment extends Fragment {

    View view;
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

    public SingleQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_single_question, container, false);
        recyclerViewAnswer = (RecyclerView)view.findViewById(R.id.answerFeedRecycler);
        tvQuestion = (TextView)view.findViewById(R.id.questionContent);
        tvAskedOn = (TextView)view.findViewById(R.id.askedDate);
        tvAnswerCount = (TextView)view.findViewById(R.id.totalAnswerCount);
        btWriteAnswer = (Button)view.findViewById(R.id.writeAnswer);
        alreadyAnsweredCard = (CardView)view.findViewById(R.id.ifAlreadyWrittenCard);
        tvalreadyAnswered = (TextView)view.findViewById(R.id.ifAlreadyWritten);

        //edit  answer
        tvalreadyAnswered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetAnswerByUserId();
            }
        });

        //get current userId
        currentUserId = SharedPrefSuggMe.getInstance(getActivity()).getUserId();
        //get current system time
        currentTime = timestamp.toString();

        btWriteAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnswerDialog();
            }
        });

        final String question_id = getArguments().getString("QID");
        String question_content = getArguments().getString("CONTENT");
        String question_date = getArguments().getString("DATE");
        String answer_count = getArguments().getString("ANSC");

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

        //beautifying the time display
                try {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    Date time =  df.parse(question_date);
                    PrettyTime p = new PrettyTime();
                    question_date = "Asked "+p.format(time);
                } catch (ParseException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
        //end of beautifying the time display

                tvQuestion.setText(question_content);
                tvAskedOn.setText(question_date);
                tvAnswerCount.setText(answer_count+" Answers");

        getAnswerQueue = Volley.newRequestQueue(getActivity());
        progress = ProgressDialog.show(getActivity(),"Please wait.","Feeding the feeds", false, false);
        answerFeeds = new ArrayList<>();
        //Answers
        getAnswerRequest = new StringRequest(Request.Method.POST, Config.URL_GET_ANSWERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                    answerFeedAdapter = new AnswerFeedAdapter(getActivity(),answerFeeds);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerViewAnswer.setLayoutManager(layoutManager);
                    recyclerViewAnswer.setItemAnimator(new DefaultItemAnimator());
                    recyclerViewAnswer.setAdapter(answerFeedAdapter);
                    answerFeedAdapter.notifyDataSetChanged();

                    recyclerViewAnswer.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerViewAnswer, new ClickListener() {
                        @Override
                        public void onClick(View view, int position) {

                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
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
        return view;
    }

    //alert dialog to write answer
    private void showAnswerDialog() {

        final AlertDialog.Builder writeAnswerDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
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
                String userid = SharedPrefSuggMe.getInstance(getActivity()).getUserId();
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
                Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
                b.dismiss();
            }
        });
        b.show();
    }

    //to create answers
    private void createAnswer (final String content, final String user_id) {

        progress = ProgressDialog.show(getActivity(),"Please wait.","Feeding the feeds", false, false);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final StringRequest request = new StringRequest(Request.Method.POST, Config.URL_CREATE_ANSWERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                if(response.equals("1")) {
                    //clear all the data in the list
                    answerFeeds.clear();
                    //make adpater empty
                    answerFeedAdapter.notifyDataSetChanged();
                    //make new volley request
                    getAnswerQueue.add(getAnswerRequest);
                    //call adapter class
                    answerFeedAdapter = new AnswerFeedAdapter(getActivity(),answerFeeds);
                    //set adapter
                    recyclerViewAnswer.setAdapter(answerFeedAdapter);
                    //populate the adapter;
                    answerFeedAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                    progress.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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

        Toast.makeText(getActivity(), "Update Dialog", Toast.LENGTH_SHORT).show();
        final AlertDialog.Builder writeAnswerDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
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
                Toast.makeText(getActivity(), updatedAnswer, Toast.LENGTH_SHORT).show();
                updateAnswer(tempAnswerID, updatedAnswer, "0","1");
                b.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
                b.dismiss();
            }
        });
        b.show();
    }


    private void updateAnswer (final String ans_id, final String content,final String isAnonymous, final String isActive) {

        progress = ProgressDialog.show(getActivity(),"Please wait.","Feeding the feeds", false, false);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
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
                    answerFeedAdapter = new AnswerFeedAdapter(getActivity(),answerFeeds);
                    //set adapter
                    recyclerViewAnswer.setAdapter(answerFeedAdapter);
                    //populate the adapter;
                    answerFeedAdapter.notifyDataSetChanged();
                    //progress.dismiss();
                }else{
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                    progress.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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


    private void GetAnswerByUserId() {

        progress = ProgressDialog.show(getActivity(),"Please wait.","Feeding the updater", false, false);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final StringRequest request = new StringRequest(Request.Method.POST, Config.URL_GET_ANSWERBY_USERID, new Response.Listener<String>() {
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
                    Toast.makeText(getActivity(), "At GETANSWERBYID : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("GETBYID",e.getMessage());
            }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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

}
