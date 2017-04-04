package com.sahilpaudel.app.suggme.profile;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sahilpaudel.app.suggme.CustomProgressDialog;
import com.sahilpaudel.app.suggme.R;
import com.sahilpaudel.app.suggme.RecyclerTouchListener;
import com.sahilpaudel.app.suggme.SharedPrefSuggMe;
import com.sahilpaudel.app.suggme.mainquestionpage.QuestionFeed;
import com.sahilpaudel.app.suggme.mainquestionpage.QuestionFeedAdapter;
import com.sahilpaudel.app.suggme.singlequestionpage.AnswerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserQuestionsFragment extends Fragment {

    View view;
    RecyclerView recyclerViewQuestions;
    QuestionFeedAdapter questionFeedAdapter;
    StringRequest requestQuestion;
    RequestQueue queue;
    List<QuestionFeed> question_feed;

    FragmentManager manager;
    CustomProgressDialog customProgress;

    public UserQuestionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_user_questions, container, false);
         recyclerViewQuestions = (RecyclerView)view.findViewById(R.id.recyclerViewQuestion);
         //getting questions that are asked by the current user
         question_feed = new ArrayList<>();

         //progress dialog
         manager = getActivity().getSupportFragmentManager();
         customProgress = new CustomProgressDialog(getActivity(), manager);
         customProgress.show();
         queue = Volley.newRequestQueue(getActivity());
         requestQuestion = new StringRequest(Request.Method.POST, Config.URL_GET_QUESTION_BY_USER_ID, new Response.Listener<String>() {
             @Override
             public void onResponse(String response) {
                 try {
                     customProgress.dismiss();
                     JSONArray array = new JSONArray(response);
                     for (int i = 0; i < array.length(); i++) {
                         JSONObject object = array.getJSONObject(i);
                         QuestionFeed feed = new QuestionFeed();
                         feed.question_id = object.getString("question_id");
                         feed.quest_title = object.getString("quest_title");
                         feed.askedOn = object.getString("entryOn");
                         feed.askedBy = object.getString("user_id");
                         feed.answerCount = object.getString("ansc");
                         question_feed.add(feed);
                     }

                     questionFeedAdapter = new QuestionFeedAdapter(getActivity(),question_feed);
                     RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                     recyclerViewQuestions.setLayoutManager(layoutManager);
                     recyclerViewQuestions.setItemAnimator(new DefaultItemAnimator());
                     recyclerViewQuestions.setAdapter(questionFeedAdapter);
                     questionFeedAdapter.notifyDataSetChanged();

                     recyclerViewQuestions.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerViewQuestions, new ClickListener() {
                         @Override
                         public void onClick(View view, int position) {
                             //fragment to display single feed in one page
                             //Fragment fragment = new SingleQuestionFragment();
                             QuestionFeed feeds = question_feed.get(position);
                             String question = feeds.quest_title;
                             String question_id = feeds.question_id;
                             String askedOn = feeds.askedOn;

                             Intent intent = new Intent(getActivity(), AnswerActivity.class);
                             intent.putExtra("QID",question_id);
                             intent.putExtra("CONTENT",question);
                             intent.putExtra("DATE",askedOn);
                             startActivity(intent);

                         }
                         @Override
                         public void onLongClick(View view, int position) {

                         }
                     }));

                 } catch (JSONException e) {
                     Toast.makeText(getActivity(), "JSONException : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                 }
             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {
                 Toast.makeText(getActivity(), "Error UQ : "+error, Toast.LENGTH_SHORT).show();
             }
         }){
             @Override
             protected Map<String, String> getParams() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("user_id", SharedPrefSuggMe.getInstance(getActivity()).getUserId());
                 return params;
             }
         };
        queue.add(requestQuestion);
        return view;
    }

}
