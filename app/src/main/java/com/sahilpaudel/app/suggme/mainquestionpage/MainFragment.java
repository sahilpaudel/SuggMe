package com.sahilpaudel.app.suggme.mainquestionpage;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.sahilpaudel.app.suggme.LoadingFragment;
import com.sahilpaudel.app.suggme.R;
import com.sahilpaudel.app.suggme.RecyclerTouchListener;
import com.sahilpaudel.app.suggme.SharedPrefSuggMe;
import com.sahilpaudel.app.suggme.location.GetUserAddress;
import com.sahilpaudel.app.suggme.singlequestionpage.AnswerActivity;
import com.sahilpaudel.app.suggme.singlequestionpage.AnswerFeed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    View view;
    RecyclerView recyclerViewFeed;
    QuestionFeedAdapter questionFeedAdapter;
    List<QuestionFeed> question_feed;
    ProgressDialog progress;

    TextView tvUserName;
    EditText etWriteQuestion;

    Intent intent;

    ArrayAdapter hintAdapter;


    //current timestamp
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String currentTime;

    RequestQueue getQuestionQueue;
    StringRequest getQuestionRequest;

    SwipeRefreshLayout swipeToRefreshQuestion;

    GetUserAddress getUserAddress;

    String city;
    String state;
    String country;

    String questionAddress;
    TextView currentLocation;

    private final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    FragmentManager manager;
    CustomProgressDialog p;

    public MainFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);

        getUserAddress = new GetUserAddress(getActivity());
        getUserAddress.executeGPS();

        city = getUserAddress.getCity();
        state = getUserAddress.getState();
        country = getUserAddress.getCountry();
        questionAddress = city+", "+state+", "+country;

        swipeToRefreshQuestion = (SwipeRefreshLayout)view.findViewById(R.id.swipeToRefreshQuestion);

        recyclerViewFeed = (RecyclerView)view.findViewById(R.id.mainFeedRecycler);
        tvUserName = (TextView)view.findViewById(R.id.userName);
        etWriteQuestion = (EditText) view.findViewById(R.id.askQuestion);
        question_feed = new ArrayList<>();

        //setting username on the top
        tvUserName.setText(SharedPrefSuggMe.getInstance(getActivity()).getUserName());
        //get current system time
        currentTime = timestamp.toString();

        //trigger alert box when user click to write an answer
        etWriteQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuestionDialog();
            }
        });

        getQuestionQueue = Volley.newRequestQueue(getActivity());

        progress = ProgressDialog.show(getActivity(),"Please wait.","Feeding the feeds", false, false);

        //get question
        getQuestionRequest = new StringRequest(Request.Method.POST, Config.URL_GET_QUESTIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //set refresh stop when we get the data
                swipeToRefreshQuestion.setRefreshing(false);
                progress.dismiss();
                Log.d("RESPONSE",response);
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
                        question_feed.add(feed);
                    }


                    questionFeedAdapter = new QuestionFeedAdapter(getActivity(),question_feed);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerViewFeed.setLayoutManager(layoutManager);
                    recyclerViewFeed.setItemAnimator(new DefaultItemAnimator());
                    recyclerViewFeed.setAdapter(questionFeedAdapter);
                    questionFeedAdapter.notifyDataSetChanged();
                    swipeToRefreshQuestion.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            //clear the list
                            question_feed.clear();
                            //clear adapter
                            questionFeedAdapter.notifyDataSetChanged();
                            //make new request
                            getQuestionQueue.add(getQuestionRequest);

                            //call adapter
                            questionFeedAdapter = new QuestionFeedAdapter(getActivity(),question_feed);
                            //set adapter
                            recyclerViewFeed.setAdapter(questionFeedAdapter);
                            //notify adapter
                            questionFeedAdapter.notifyDataSetChanged();
                        }
                    });

                    // Configure the refreshing colors
                    swipeToRefreshQuestion.setColorSchemeResources(
                            android.R.color.holo_blue_bright,
                            android.R.color.holo_green_light,
                            android.R.color.holo_orange_light,
                            android.R.color.holo_red_light);

                    recyclerViewFeed.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerViewFeed, new ClickListener() {
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
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    //progress.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progress.dismiss();
                Log.e("VOLLEY ERROR",""+error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("STATE",state);
                return params;
            }
        };
        getQuestionQueue.add(getQuestionRequest);
        return view;
    }


    private void showQuestionDialog() {

        final AlertDialog.Builder writeQuestionDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.ask_question_dialog,null);
        writeQuestionDialog.setView(dialogView);
        final AlertDialog b = writeQuestionDialog.create();

        //place autocomplete
        try {
            intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(getActivity());
            //startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        }catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //fetching old questions
        final EditText askQuestion = (EditText)dialogView.findViewById(R.id.ask_question_dialog);
        int n = question_feed.size();
        final String quest[] = new String[n];
        final String quest_id[] = new String[n];
        final List list = new ArrayList();

        for (int i = 0; i < n; i++) {
            quest[i] = question_feed.get(i).quest_title;
            quest_id[i] = question_feed.get(i).question_id;
            list.add(question_feed.get(i).quest_title);
        }

        //listView to show hints while typing
        final ListView listView = (ListView)dialogView.findViewById(R.id.question_hint);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object object = adapterView.getItemAtPosition(position);
                String value = object.toString();
                int index = list.indexOf(value);
                String question_id = quest_id[index];
                getQuestionById(question_id, b);
            }
        });
        //using default adapter
          hintAdapter = new ArrayAdapter<>(getActivity(),R.layout.list_item, R.id.question_title, list);
          listView.setAdapter(hintAdapter);

        //adding listener for hints while typing
        askQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    listView.setVisibility(View.VISIBLE);
                    hintAdapter.getFilter().filter(charSequence);
                    if (askQuestion.getText().toString().equals("")) {
                        listView.setVisibility(View.GONE);
                    }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //select location
        currentLocation = (TextView)dialogView.findViewById(R.id.currentLocation);
                 currentLocation.setText(questionAddress);
        Button changeLocation = (Button)dialogView.findViewById(R.id.changeLocation);
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
                    createQuestion(askQuestion.getText().toString(), "0");
                    b.dismiss();
                }
            }
        });

        b.show();
    }

    // to send data for single view
    private void getQuestionById(final String question_id, final AlertDialog b) {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        progress = ProgressDialog.show(getActivity(),"Please wait.","Feeding the question", false, false);
        //get question
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_GET_QUESTIONBYID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                try {
                    JSONArray array = new JSONArray(response);
                        JSONObject object = array.getJSONObject(0);

                        String question_id = object.getString("question_id");
                        String quest_title = object.getString("quest_title");
                        String category_id = object.getString("category_id");
                        String askedOn = object.getString("entryOn");
                        String askedBy = object.getString("user_id");
                        String answerCount = object.getString("ansc");

                    Intent intent = new Intent(getActivity(), AnswerActivity.class);
                    intent.putExtra("QID",question_id);
                    intent.putExtra("CONTENT",quest_title);
                    intent.putExtra("DATE",askedOn);
                    startActivity(intent);
                    b.dismiss();
                } catch (JSONException e) {
                    //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("EXCEPTION : ",""+e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("EXCEPTION : ",""+error.getMessage());
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
    }

    private void createQuestion(final String content, final String isAnonymous) {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        progress = ProgressDialog.show(getActivity(),"Please wait.","Feeding the question", false, false);
        //get question
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_CREATE_QUESTION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()) {
                    //clear all the data in the list
                    question_feed.clear();
                    //make adpater empty
                    questionFeedAdapter.notifyDataSetChanged();
                    //make new volley request
                    getQuestionQueue.add(getQuestionRequest);
                    //call adapter class
                    questionFeedAdapter = new QuestionFeedAdapter(getActivity(),question_feed);
                    //set adapter
                    recyclerViewFeed.setAdapter(questionFeedAdapter);
                    //populate the adapter;
                    questionFeedAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Volley Error : "+error, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("content",content);
                params.put("cat_id","1");
                params.put("user_id",SharedPrefSuggMe.getInstance(getActivity()).getUserId());
                params.put("entryOn",currentTime);
                params.put("is_anonymous",isAnonymous);
                params.put("question_address",questionAddress);
                return params;
            }
        };

        queue.add(request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                questionAddress = place.getAddress().toString();
                currentLocation.setText(questionAddress);
                Log.i("PLACE", questionAddress);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i("PLACE", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }



}
