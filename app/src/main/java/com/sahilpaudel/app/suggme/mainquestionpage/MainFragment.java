package com.sahilpaudel.app.suggme.mainquestionpage;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.sahilpaudel.app.suggme.singlequestionpage.SingleQuestionFragment;

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
    QuestionFeedAdapter questionFeedAdapter;
    List<QuestionFeed> question_feed;
    ProgressDialog progress;
    Paint p = new Paint();
    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerViewFeed = (RecyclerView)view.findViewById(R.id.mainFeedRecycler);
        question_feed = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        progress = ProgressDialog.show(getActivity(),"Please wait.","Feeding the feeds", false, false);

        //get question
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_GET_QUESTIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                try {
                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        QuestionFeed feed = new QuestionFeed();
                        feed.question_id = object.getString("question_id");
                        feed.quest_title = object.getString("quest_title");
                        feed.category_id = object.getString("category_id");
                        feed.askedOn = object.getString("entryOn");
                        feed.askedBy = object.getString("user_id");
                        feed.answerCount = object.getString("ansc");
                        //Toast.makeText(getActivity(),object.getString("question_id"), Toast.LENGTH_SHORT).show();
                        question_feed.add(feed);
                    }
                    questionFeedAdapter = new QuestionFeedAdapter(getActivity(),question_feed);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerViewFeed.setLayoutManager(layoutManager);
                    recyclerViewFeed.setItemAnimator(new DefaultItemAnimator());
                    recyclerViewFeed.setAdapter(questionFeedAdapter);
                    questionFeedAdapter.notifyDataSetChanged();

                    recyclerViewFeed.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerViewFeed, new ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            //fragment to display single feed in one page
                            Fragment fragment = new SingleQuestionFragment();
                            QuestionFeed feeds = question_feed.get(position);
                            String question = feeds.quest_title;
                            String question_id = feeds.question_id;
                            String askedOn = feeds.askedOn;
                            String answercount = feeds.answerCount;
                            Bundle args = new Bundle();
                            args.putString("QID",question_id);
                            args.putString("CONTENT",question);
                            args.putString("DATE",askedOn);
                            args.putString("ANSC",answercount);

                            fragment.setArguments(args);

                            if(fragment != null) {
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.contentFragment, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
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
        }){};
        queue.add(request);
        return view;
    }

}
