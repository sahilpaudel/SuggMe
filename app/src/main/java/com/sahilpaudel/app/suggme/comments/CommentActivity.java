package com.sahilpaudel.app.suggme.comments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends Activity implements FragmentManager.OnBackStackChangedListener {

    //initially we are showing front of the card
    private boolean mShowingBack = false;
    ImageView imageViewClose;
    TextView txQuestionTitle;
    TextView txAnsweredBy;
    TextView txAnsweredOn;

    ImageView ivWriteComment;

    Fragment fragment_f;
    Fragment fragment_b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        String questionTitle = getIntent().getStringExtra("QUESTION_TITLE");
        String answeredBy = getIntent().getStringExtra("ANSWERED_BY");
        String answeredOn = getIntent().getStringExtra("ANSWERED_ON");
        String answeredId = getIntent().getStringExtra("ANSWER_ID");

        //send answer id to fragment so that we can fetch comments/ write comments for that answer id
        fragment_f = new CommentFrontFragment();
        fragment_b = new CommentBackFragment();

        Bundle bundle = new Bundle();
        bundle.putString("ANSWER_ID", answeredId);
        fragment_f.setArguments(bundle);
        fragment_b.setArguments(bundle);

        imageViewClose = (ImageView)findViewById(R.id.cross_close);
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
        txQuestionTitle = (TextView)findViewById(R.id.question_title);
        txAnsweredBy = (TextView)findViewById(R.id.answeredByName);
        txAnsweredOn = (TextView)findViewById(R.id.answeredOn);

        txQuestionTitle.setText(questionTitle);
        txAnsweredBy.setText(answeredBy);
        txAnsweredOn.setText(answeredOn);

        ivWriteComment = (ImageView)findViewById(R.id.ivWriteComment);
        ivWriteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipCard();
            }
        });
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.commentFragment, fragment_f)
                    .commit();
        } else {
            mShowingBack = (getFragmentManager()
                    .getBackStackEntryCount() > 0);
        }
        getFragmentManager().addOnBackStackChangedListener(this);
    }


    @Override
    public void onBackStackChanged() {
        mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
    }

    private void flipCard() {
        if (mShowingBack) {
            getFragmentManager().popBackStack();
            return;
        }
        mShowingBack = true;
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                .replace(R.id.commentFragment, fragment_b)
                .addToBackStack(null)
                .commit();
    }

    public static class CommentFrontFragment extends Fragment {

        View view;
        RecyclerView commentRecyclerView;
        StringRequest commentRequest;
        RequestQueue commentQueue;
        Comments comments;
        List<Comments> commentList;
        CommentAdapter commentAdapter;

        String tempAnswerId;
        ProgressDialog progress;

        public CommentFrontFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.comment_view, container, false);

            //start progress dialog
            progress = ProgressDialog.show(getActivity(), "Please wait.","Loading comments...", false, false);
            //get answer to fetch comments corresponding
            tempAnswerId = getArguments().getString("ANSWER_ID");
            //Toast.makeText(getActivity(), tempAnswerId, Toast.LENGTH_SHORT).show();
            commentRecyclerView = (RecyclerView)view.findViewById(R.id.commentRecyclerView);
            commentList = new ArrayList<>();
            //volley start
            commentQueue = Volley.newRequestQueue(getActivity());
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
                            commentList.add(comments);
                         //   Toast.makeText(getActivity(), object.getString("comment"), Toast.LENGTH_SHORT).show();
                        }
                        commentAdapter = new CommentAdapter(getActivity(), commentList);
                        RecyclerView.LayoutManager ll = new LinearLayoutManager(getActivity());
                        commentRecyclerView.setLayoutManager(ll);
                        commentRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        commentRecyclerView.setAdapter(commentAdapter);
                        commentAdapter.notifyDataSetChanged();

                        //close the progress dialog
                        progress.dismiss();
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "Exception : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                       Toast.makeText(getActivity(), "Error : "+error, Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("answer_id", tempAnswerId);
                    return params;
                }
            };
            commentQueue.add(commentRequest);
            return view;
        }
    }

    public static class CommentBackFragment extends Fragment {

        View view;
        EditText editTextWriteComment;
        Button buttonSendComment;
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String currTime;

        String tempAnswerId;
        String user_id;

        public CommentBackFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.comment_write, container, false);

            //content of comment
            editTextWriteComment = (EditText)view.findViewById(R.id.commentBox);
            //user_id of commet
            user_id = SharedPrefSuggMe.getInstance(getActivity()).getUserId();
            //date of comment
            currTime = ts.toString();
            //answer_id to comment
            tempAnswerId = getArguments().getString("ANSWER_ID");

            buttonSendComment = (Button)view.findViewById(R.id.buttonComment);
            buttonSendComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendComment(editTextWriteComment.getText().toString());
                }
            });
            return view;
        }

        private void sendComment(final String comment) {

            StringRequest request = new StringRequest(Request.Method.POST, Config.URL_CREATE_COMMENTS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("1")) {
                        getFragmentManager().popBackStack();
                    }else{
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Error : "+error, Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("ans_id", tempAnswerId);
                    params.put("comment", comment);
                    params.put("entryOn", currTime);
                    params.put("user_id", user_id);
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            queue.add(request);
        }
    }
}
