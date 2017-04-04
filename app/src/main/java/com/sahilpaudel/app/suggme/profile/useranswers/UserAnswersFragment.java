package com.sahilpaudel.app.suggme.profile.useranswers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.sahilpaudel.app.suggme.Config;
import com.sahilpaudel.app.suggme.CustomProgressDialog;
import com.sahilpaudel.app.suggme.R;
import com.sahilpaudel.app.suggme.SharedPrefSuggMe;
import com.sahilpaudel.app.suggme.singlequestionpage.AnswerActivity;
import com.sahilpaudel.app.suggme.singlequestionpage.AnswerFeed;
import com.sahilpaudel.app.suggme.singlequestionpage.AnswerFeedAdapter;

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
public class UserAnswersFragment extends Fragment {

    View view;
    List<AnswerFeed> list;

    FragmentManager manager;
    CustomProgressDialog p;

    RecyclerView recyclerViewAnswerByUser;
    UserAnswerAdapter userAnswerAdapter;

    public UserAnswersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_answers, container, false);
        recyclerViewAnswerByUser = (RecyclerView)view.findViewById(R.id.recyclerViewAnswer);
        list = new ArrayList<>();
        manager = getActivity().getSupportFragmentManager();
        p = new CustomProgressDialog(getActivity(), manager);
        p.show();
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_GET_ANSWER_BY_USER_ID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                p.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array1 = object.getJSONArray("ANSWERS");
                    JSONArray array2 = object.getJSONArray("USERS");
                    for (int i = 0; i < array1.length(); i++) {

                        JSONObject answer = array1.getJSONObject(i);
                        AnswerFeed feed = new AnswerFeed();
                        feed.question_id = answer.getString("question_id");
                        feed.question_title = answer.getString("quest_title");
                        feed.answer_id = answer.getString("answer_id");
                        feed.answer_content = answer.getString("answer_content");
                        feed.entryOn = answer.getString("answeredOn");
                        feed.askedOn = answer.getString("askedOn");
                        feed.user_id = answer.getString("answeredBy");
                        String answeredBy = answer.getString("answeredBy");

                        for (int j = 0; j < array2.length(); j++) {
                            JSONObject user = array2.getJSONObject(j);
                            if (answeredBy.equals(user.getString("user_id"))) {
                                feed.first_name = user.getString("first_name");
                                feed.last_name = user.getString("last_name");
                                feed.first_name = user.getString("first_name");
                                feed.first_name = user.getString("first_name");
                                feed.image_url = user.getString("image_url");
                            }
                        }
                        list.add(feed);
                    }

                    userAnswerAdapter = new UserAnswerAdapter(getActivity(),list);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerViewAnswerByUser.setLayoutManager(layoutManager);
                    recyclerViewAnswerByUser.setItemAnimator(new DefaultItemAnimator());
                    recyclerViewAnswerByUser.setAdapter(userAnswerAdapter);
                    userAnswerAdapter.notifyDataSetChanged();

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
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("user_id", SharedPrefSuggMe.getInstance(getActivity()).getUserId());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);

        return view;
    }

}
