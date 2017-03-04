package com.sahilpaudel.app.suggme;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.sahilpaudel.app.suggme.adapter.MainFeedAdapter;
import com.sahilpaudel.app.suggme.dataproviders.MainFeed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    View view;
    RecyclerView recyclerViewFeed;
    MainFeedAdapter mainFeedAdapter;
    List<MainFeed> main_feed;
    ProgressDialog progress;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerViewFeed = (RecyclerView)view.findViewById(R.id.mainFeedRecycler);
        main_feed = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        progress = ProgressDialog.show(getActivity(),"Please wait.","Mixing the Cement", false, false);
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_GET_QUESTIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        MainFeed feed = new MainFeed();
                        feed.question_id = object.getString("question_id");
                        feed.quest_title = object.getString("quest_title");
                        feed.category_id = object.getString("category_id");
                        feed.askedOn = object.getString("askedOn");
                        feed.answer_id = object.getString("answer_id");
                        feed.answer_content = object.getString("answer_content");
                        feed.answeredOn = object.getString("answeredOn");
                        feed.isAnonymous = object.getString("isAnonymous");
                        feed.answeredBy = object.getString("answeredBy");
                        feed.first_name = object.getString("first_name");
                        feed.last_name = object.getString("last_name");
                        main_feed.add(feed);
                    }
                    mainFeedAdapter = new MainFeedAdapter(getActivity(),main_feed);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerViewFeed.setLayoutManager(layoutManager);
                    recyclerViewFeed.setItemAnimator(new DefaultItemAnimator());
                    recyclerViewFeed.setAdapter(mainFeedAdapter);
                    mainFeedAdapter.notifyDataSetChanged();
                    progress.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
            }
        }){};

        queue.add(request);
        return view;
    }

}
