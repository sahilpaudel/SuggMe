package com.sahilpaudel.app.suggme.singlequestionpage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.sahilpaudel.app.suggme.comments.CommentActivity;
import com.sahilpaudel.app.suggme.comments.CommentViewFragment;
import com.sahilpaudel.app.suggme.upvotemanager.Upvote;
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
 * Created by Sahil Paudel on 3/7/2017.
 */

public class AnswerFeedAdapter extends RecyclerView.Adapter<AnswerFeedAdapter.MyViewHolder> {

    List<AnswerFeed> answerFeeds;
    Context context;
    String answeredBy;
    FragmentTransaction transaction;

    String question_id,question_title, question_date;
    int total_like;


    public AnswerFeedAdapter(Context context, List<AnswerFeed> answerFeeds, FragmentTransaction transaction,
                             String question_id, String question_title, String question_date ) {
        this.answerFeeds = answerFeeds;
        this.context = context;
        this.transaction = transaction;

        this.question_id = question_id;
        this.question_title = question_title;
        this.question_date = question_date;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.answer_feed_card, parent, false);
        return new AnswerFeedAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        AnswerFeed answerFeed = answerFeeds.get(position);
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
        String isActive = answerFeed.isActive;
        String isAnonymous = answerFeed.isAnonymous;
        String isUpdated = answerFeed.isUpdated;
        final String question_title = answerFeed.question_title;

        //to get like count
        getLike(answer_id, holder);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date time = null;
        try {
            time = df.parse(answeredOn);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        PrettyTime p = new PrettyTime();
        answeredOn = "Written "+p.format(time);

        holder.tvAnsweredBy.setText(answeredBy);
        holder.tvAnsweredOn.setText(answeredOn);
        holder.tvAnswer.setText(answer);
        Picasso.with(context).load(image_url).into(holder.imageView);
        holder.ivShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptions(view, answer_id, user_id);
            }
        });

        //set upvotes
        holder.bt_upvote.setText(total_like+" UPVOTES");
        //create likes
        holder.bt_upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createLike(answer_id);
                int i = Integer.parseInt(holder.bt_upvote.getText().charAt(0)+"") + 1;
                holder.bt_upvote.setText(i+" UPVOTES");
                holder.bt_upvote.setClickable(false);
            }
        });

        //to access in anonymous class of listener
        final String finalAnsweredOn = answeredOn;
        holder.bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("QUESTION_TITLE",question_title);
                intent.putExtra("ANSWERED_BY", answeredBy);
                intent.putExtra("ANSWERED_ON", finalAnsweredOn);
                intent.putExtra("ANSWER_ID",answer_id);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return answerFeeds.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvAnsweredBy, tvAnsweredOn, tvAnswer;
        Button bt_upvote, bt_comment;
        ImageView imageView;
        ImageView ivShowMore;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvAnsweredBy = (TextView)itemView.findViewById(R.id.answeredByName);
            tvAnsweredOn = (TextView)itemView.findViewById(R.id.answeredOn);
            tvAnswer = (TextView)itemView.findViewById(R.id.answers);
            bt_upvote = (Button)itemView.findViewById(R.id.like);
            bt_comment = (Button)itemView.findViewById(R.id.suggest);
            imageView = (ImageView)itemView.findViewById(R.id.answeredByImage);
            ivShowMore = (ImageView)itemView.findViewById(R.id.ivShowMore);
        }
    }

    //show additional options
    private void showOptions(View view, final String answer_id, String user_id) {
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

    ProgressDialog progress;
    //delete answer by setting isActive = 0
    private void deleteAnswer(final String answer_id) {
        progress = ProgressDialog.show(context,"Please wait.", "Deleting the answer...", false, false);
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_DELETE_ANSWER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("1")) {

                    Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
                    progress.dismiss();

                    Intent intent = new Intent(context, AnswerActivity.class);
                    intent.putExtra("QID",question_id);
                    intent.putExtra("CONTENT",question_title);
                    intent.putExtra("DATE",question_date);
                    context.startActivity(intent);
                    ((Activity) context).finish();
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

    private void createLike(final String answer_id) {
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_CREATE_LIKE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("1")) {
                    Toast.makeText(context, "upvoted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Oops! try again.", Toast.LENGTH_SHORT).show();
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


    private void getLike(final String answer_id, final MyViewHolder holder) {
        progress = ProgressDialog.show(context,"Please wait.", "Loading upvotes....", false, false);
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_GETLIKE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                try {
                    JSONArray array = new JSONArray(response);
                    total_like = array.length();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);

                        if (object.getString("user_id").equals(SharedPrefSuggMe.getInstance(context).getUserId())) {
                            Toast.makeText(context, "Already liked", Toast.LENGTH_SHORT).show();
                            holder.bt_upvote.setText(total_like+" UPVOTES");
                            holder.bt_upvote.setClickable(false);
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "LIKE : "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("user_id", SharedPrefSuggMe.getInstance(context).getUserId());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }
}
