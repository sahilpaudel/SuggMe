package com.sahilpaudel.app.suggme.profile.useranswers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.sahilpaudel.app.suggme.singlequestionpage.AnswerActivity;
import com.sahilpaudel.app.suggme.singlequestionpage.AnswerFeed;
import com.sahilpaudel.app.suggme.singlequestionpage.AnswerFeedAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Sahil Paudel on 4/3/2017.
 */

public class UserAnswerAdapter extends RecyclerView.Adapter<UserAnswerAdapter.MyViewHolder> {

    List<AnswerFeed> feedList;
    Context context;

    String answeredBy;

    ProgressDialog progress;

    boolean isUpvoted;

    public UserAnswerAdapter(Context context, List<AnswerFeed> feedList) {
        this.context = context;
        this.feedList = feedList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.answer_view_profile, parent, false);
        return new UserAnswerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        AnswerFeed answerFeed = feedList.get(position);

        final String user_id = answerFeed.user_id;
        answeredBy = answerFeed.first_name +" "+answerFeed.last_name;

        if (SharedPrefSuggMe.getInstance(context).getUserId().equals(user_id)) {
            answeredBy = "Me";
        }

        String answer = answerFeed.answer_content;
        String answeredOn = answerFeed.entryOn;
        String image_url = answerFeed.image_url;
        final String answer_id = answerFeed.answer_id;
        String question_id = answerFeed.question_id;
        String question_title = answerFeed.question_title;
        String askedOn = answerFeed.askedOn;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date time1 = null;
        Date time2 = null;
        try {
            time1 = df.parse(answeredOn);
            time2 = df.parse(askedOn);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //to get like count
        getLike(answer_id, holder);

        holder.bt_upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.bt_upvote.isChecked()) {
                    createLike(answer_id, holder);
                } else {
                    deleteLike(answer_id, holder);
                }
                getLike(answer_id, holder);
            }
        });

        PrettyTime p = new PrettyTime();
        answeredOn = "Written "+p.format(time1);
        askedOn = "Asked "+p.format(time2);

        holder.tvAnsweredBy.setText(answeredBy);
        holder.tvAnsweredOn.setText(answeredOn);
        holder.tvQuestionTitle.setText(question_title);
        holder.tvQuestionDate.setText(askedOn);
        holder.tvAnswer.setText(answer);
        if (!image_url.isEmpty())
                 Picasso.with(context).load(image_url).into(holder.imageView);

        holder.ivShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptions(view, answer_id, user_id, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvAnsweredBy, tvAnsweredOn, tvAnswer, tvQuestionTitle, tvQuestionDate;
        Button bt_comment;
        ToggleButton bt_upvote;
        ImageView imageView;
        ImageView ivShowMore;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvQuestionTitle = (TextView)itemView.findViewById(R.id.tvQuestionTitle);
            tvQuestionDate = (TextView)itemView.findViewById(R.id.tvQuestionDate);
            tvAnsweredBy = (TextView)itemView.findViewById(R.id.tvAnsweredByName);
            tvAnsweredOn = (TextView)itemView.findViewById(R.id.tvAnsweredOn);
            tvAnswer = (TextView)itemView.findViewById(R.id.tvAnswerContent);
            bt_upvote = (ToggleButton)itemView.findViewById(R.id.bt_upvote);
            bt_comment = (Button)itemView.findViewById(R.id.bt_comment);
            imageView = (ImageView)itemView.findViewById(R.id.ivAnsweredByImage);
            ivShowMore = (ImageView)itemView.findViewById(R.id.ivShowMore);

        }
    }
     int total_like;

    private void getLike(final String answer_id, final MyViewHolder holder) {

        //progress = ProgressDialog.show(context,"Please wait.", "Loading upvotes....", false, true);
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_GET_UPVOTE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    total_like = array.length();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);

                        if (object.getString("user_id").equals(SharedPrefSuggMe.getInstance(context).getUserId())) {
                            //holder.bt_upvote.setText(total_like+" UPVOTED");
                            holder.bt_upvote.setChecked(true);
                        } else {
                            holder.bt_upvote.setChecked(false);
                        }
                        //progress.dismiss();
                    }

                } catch (JSONException e) {
                    Toast.makeText(context, "LIKE : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    //progress.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error : "+error, Toast.LENGTH_SHORT).show();
                //progress.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("answer_id", answer_id);
                params.put("user_id", SharedPrefSuggMe.getInstance(context).getUserId());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    private void createLike(final String answer_id, final MyViewHolder holder) {
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_CREATE_UPVOTE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("1")) {
                    Toast.makeText(context, "upvoted", Toast.LENGTH_SHORT).show();
                    holder.bt_upvote.setChecked(true);
                }
                else {
                    //Toast.makeText(context, "Oops! try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error : "+error, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("answer_id", answer_id);
                params.put("user_id", SharedPrefSuggMe.getInstance(context).getUserId());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }

    private void deleteLike(final String answer_id, final MyViewHolder holder) {
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_DELETE_UPVOTE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("1")) {
                    Toast.makeText(context, "downvoted", Toast.LENGTH_SHORT).show();
                    holder.bt_upvote.setChecked(false);
                }
                else {
                    //Toast.makeText(context, "Oops! try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error : "+error, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("answer_id", answer_id);
                params.put("user_id", SharedPrefSuggMe.getInstance(context).getUserId());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }

    private void deleteAnswer(final String answer_id) {
        progress = ProgressDialog.show(context,"Please wait.", "Deleting the answer...", false, false);
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_DELETE_ANSWER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("1")) {

                    Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
                    progress.dismiss();

                }
                else {
                    Toast.makeText(context, "Oops! try again.", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error : "+error, Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("answer_id", answer_id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    //show additional options
    private void showOptions(View view, final String answer_id, String user_id, final MyViewHolder holder) {
        PopupMenu menu = new PopupMenu(context,view);

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_delete) {
                    deleteAnswer(answer_id);
                    return true;
                }
                else if (item.getItemId() == R.id.action_report) {
                    Toast.makeText(context, "Report", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.answer_menu_item,menu.getMenu());

        //show delete if the answer is written by the user.
        MenuItem delete = menu.getMenu().findItem(R.id.action_delete);
        if (SharedPrefSuggMe.getInstance(context).getUserId().equals(user_id)) {
            delete.setVisible(true);
        }

        menu.show();
    }

}
