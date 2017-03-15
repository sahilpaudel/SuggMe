package com.sahilpaudel.app.suggme.singlequestionpage;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahilpaudel.app.suggme.R;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sahil Paudel on 3/7/2017.
 */

public class AnswerFeedAdapter extends RecyclerView.Adapter<AnswerFeedAdapter.MyViewHolder> {

    List<AnswerFeed> answerFeeds;
    Context context;

    public AnswerFeedAdapter(Context context, List<AnswerFeed> answerFeeds) {
        this.answerFeeds = answerFeeds;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.answer_feed_card, parent, false);
        return new AnswerFeedAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
           AnswerFeed answerFeed = answerFeeds.get(position);

        String answeredBy = answerFeed.first_name +" "+answerFeed.last_name;
        String answer = answerFeed.answer_content;
        String answeredOn = answerFeed.entryOn;
        String image_url = answerFeed.image_url;
        String answer_id = answerFeed.answer_id;
        String question_id = answerFeed.question_id;
        String isActive = answerFeed.isActive;
        String isAnonymous = answerFeed.isAnonymous;
        String isUpdated = answerFeed.isUpdated;

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
        holder.tvFollowers.setText("0 follow");
        holder.tvAnswer.setText(answer);
        Picasso.with(context).load(image_url).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return answerFeeds.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvAnsweredBy, tvAnsweredOn, tvFollowers, tvAnswer;
        Button bt_upvote, bt_comment;
        ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);

            tvAnsweredBy = (TextView)itemView.findViewById(R.id.answeredByName);
            tvAnsweredOn = (TextView)itemView.findViewById(R.id.answeredOn);
            tvFollowers = (TextView)itemView.findViewById(R.id.follow);
            tvAnswer = (TextView)itemView.findViewById(R.id.answers);
            bt_upvote = (Button)itemView.findViewById(R.id.like);
            bt_comment = (Button)itemView.findViewById(R.id.suggest);
            imageView = (ImageView)itemView.findViewById(R.id.answeredByImage);
        }
    }
}
