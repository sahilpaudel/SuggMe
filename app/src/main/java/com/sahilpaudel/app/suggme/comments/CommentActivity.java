package com.sahilpaudel.app.suggme.comments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sahilpaudel.app.suggme.Config;
import com.sahilpaudel.app.suggme.R;
import com.sahilpaudel.app.suggme.SharedPrefSuggMe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    ImageView imageViewClose;
    TextView txQuestionTitle;
    TextView txAnsweredOn;

    EditText ivWriteComment;

    String questionTitle;
    String answeredBy;
    String answeredOn;
    String answeredId;

    RecyclerView commentRecyclerView;
    StringRequest commentRequest;
    RequestQueue commentQueue;
    Comments comments;
    List<Comments> commentList;
    CommentAdapter commentAdapter;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

         questionTitle = getIntent().getStringExtra("QUESTION_TITLE");
         answeredBy = getIntent().getStringExtra("ANSWERED_BY");
         answeredOn = getIntent().getStringExtra("ANSWERED_ON");
         answeredId = getIntent().getStringExtra("ANSWER_ID");

        //Toast.makeText(this, answeredBy, Toast.LENGTH_SHORT).show();
        if (answeredBy.equals("Me")) {
            answeredBy = "My";
        } else {
            answeredBy = answeredBy+"\'s";
        }

        imageViewClose = (ImageView)findViewById(R.id.cross_close);
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
        ivWriteComment = (EditText)findViewById(R.id.ivWriteComment);
        ivWriteComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    createComment(ivWriteComment.getText().toString());
                    ivWriteComment.setText("");
                    handled = true;
                }
                return handled;
            }
        });
        txQuestionTitle = (TextView)findViewById(R.id.question_title);
        txAnsweredOn = (TextView)findViewById(R.id.answeredOn);

        txQuestionTitle.setText(answeredBy+" response to "+questionTitle);
        txAnsweredOn.setText(answeredOn);

        //start progress dialog
        progress = ProgressDialog.show(this, "Please wait.","Loading comments...", false, false);

        //Toast.makeText(getActivity(), tempAnswerId, Toast.LENGTH_SHORT).show();
        commentRecyclerView = (RecyclerView)findViewById(R.id.commentRecyclerView);
        commentList = new ArrayList<>();
        //volley start
        commentQueue = Volley.newRequestQueue(CommentActivity.this);
        commentAdapter = new CommentAdapter(CommentActivity.this, commentList);
        commentRequest = new StringRequest(Request.Method.POST, Config.URL_GET_COMMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        comments = new Comments();
                        JSONObject object = array.getJSONObject(i);
                        comments.userName = object.getString("first_name")+" "+object.getString("last_name");
                        comments.commentContent = object.getString("comment");
                        comments.entryOn = object.getString("entryOn");
                        comments.imageUrl = object.getString("image_url");
                        commentList.add(comments);
                    }
                    RecyclerView.LayoutManager ll = new LinearLayoutManager(CommentActivity.this);
                    commentRecyclerView.setLayoutManager(ll);
                    commentRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    commentRecyclerView.setAdapter(commentAdapter);
                    commentAdapter.notifyDataSetChanged();

                    //close the progress dialog
                    progress.dismiss();
                } catch (JSONException e) {
                    Toast.makeText(CommentActivity.this, "Exception : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CommentActivity.this, "Error : "+error, Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("answer_id", answeredId);
                return params;
            }
        };
        commentRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        commentQueue.add(commentRequest);
    }

    private void createComment(final String comment) {

        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_CREATE_COMMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("1")) {
                    //clear all the data in the list
                    commentList.clear();
                    //make adpater empty
                    commentAdapter.notifyDataSetChanged();
                    //make new volley request
                    commentQueue.add(commentRequest);
                    //call adapter class
                    commentAdapter = new CommentAdapter(CommentActivity.this, commentList);
                    //set adapter
                    commentRecyclerView.setAdapter(commentAdapter);
                    //populate the adapter;
                    commentAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(CommentActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CommentActivity.this, "Error : "+error, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ans_id", answeredId);
                params.put("comment", comment);
                params.put("user_id", SharedPrefSuggMe.getInstance(CommentActivity.this).getUserId());
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(CommentActivity.this);
        queue.add(request);
    }

}
