package com.sahilpaudel.app.suggme.profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sahilpaudel.app.suggme.Config;
import com.sahilpaudel.app.suggme.R;
import com.sahilpaudel.app.suggme.singlequestionpage.AnswerFeed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserAnswersFragment extends Fragment {

    View view;
    List<Answers> list;
    public UserAnswersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_answers, container, false);
        list = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_DEMO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("ANSWERS");
                    JSONArray array2 = object.getJSONArray("LIKE");
                    for (int i = 0; i < array.length(); i++) {
                        Answers answers = new Answers();
                        JSONObject json1 = array.getJSONObject(i);
                        String question_id = json1.getString("question_id");
                        String answer_id = json1.getString("answer_id");
                        String answer_content = json1.getString("answer_content");
                        String entryOn = json1.getString("entryOn");
                        String user_id = json1.getString("user_id");
                        String first_name = json1.getString("first_name");
                        String last_name = json1.getString("last_name");
                        String image_url = json1.getString("image_url");
                        String isActive = json1.getString("isActive");
                        String isAnonymous = json1.getString("isAnonymous");
                        String isUpdated = json1.getString("isUpdated");

                        for(int j = 0; j < array2.length(); j++) {
                            JSONObject json2 = array2.getJSONObject(j);
                            String likes = json2.getString("likes");
                            String answer_id2 = json2.getString("answer_id");

                            if (answer_id.equals(answer_id2)){
                                answers.like = likes;
                            }
                        }
                        answers.first_name = first_name;
                        answers.last_name = last_name;
                        answers.image_url = image_url;
                        answers.isActive = isActive;
                        answers.isAnonymous = isAnonymous;
                        answers.isUpdated = isUpdated;
                        answers.question_id = question_id;
                        answers.answer_content = answer_content;
                        answers.entryOn = entryOn;
                        answers.user_id = user_id;
                        answers.answer_id = answer_id;
                        list.add(answers);
                    }
                    Toast.makeText(getActivity(), list.get(0).toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), list.get(1).toString(), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error+"", Toast.LENGTH_SHORT).show();
            }
        }){

        };
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);

        return view;
    }

}
