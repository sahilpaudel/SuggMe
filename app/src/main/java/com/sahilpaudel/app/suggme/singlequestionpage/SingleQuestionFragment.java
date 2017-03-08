package com.sahilpaudel.app.suggme.singlequestionpage;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sahilpaudel.app.suggme.mainquestionpage.QuestionFeed;
import com.sahilpaudel.app.suggme.mainquestionpage.QuestionFeedAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
    TextView tvQuestion, tvAskedOn, tvAnswerCount;

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
        tvAnswerCount= (TextView)view.findViewById(R.id.totalAnswerCount);

        final String question_id = getArguments().getString("QID");
        String question_content = getArguments().getString("CONTENT");
        String question_date = getArguments().getString("DATE");
        String answer_count = getArguments().getString("ANSC");
               tvQuestion.setText(question_content);
               tvAskedOn.setText(question_date);
               tvAnswerCount.setText(answer_count+" Answers");
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        progress = ProgressDialog.show(getActivity(),"Please wait.","Feeding the feeds", false, false);
        answerFeeds = new ArrayList<>();
        //Answers
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_GET_ANSWERS, new Response.Listener<String>() {
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
                        feed.first_name = object.getString("first_name");
                        feed.last_name = object.getString("last_name");
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
        queue.add(request);
        return view;
    }

}
