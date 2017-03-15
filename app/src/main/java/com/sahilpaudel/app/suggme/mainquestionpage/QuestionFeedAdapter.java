package com.sahilpaudel.app.suggme.mainquestionpage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sahilpaudel.app.suggme.R;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sahil Paudel on 3/2/2017.
 */

public class QuestionFeedAdapter extends RecyclerView.Adapter<QuestionFeedAdapter.MyViewHolder> {

    List<QuestionFeed> list_main_feed;
    Context context;

    public QuestionFeedAdapter(Context context, List<QuestionFeed> list_main_feed) {
        this.context = context;
        this.list_main_feed = list_main_feed;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView topQuestions;
        TextView totalResponse;
        TextView askedOn;

        public MyViewHolder(View itemView) {
            super(itemView);
            topQuestions = (TextView)itemView.findViewById(R.id.mainQuestion);
            totalResponse = (TextView)itemView.findViewById(R.id.total_answer);
            askedOn = (TextView)itemView.findViewById(R.id.main_question_date);
        }
    }

    @Override
    public QuestionFeedAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_feed_card, parent, false);
        return new QuestionFeedAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuestionFeedAdapter.MyViewHolder holder, int position) {
        QuestionFeed questionFeed = list_main_feed.get(position);
        String questionId = questionFeed.question_id;
        String askedOn = questionFeed.askedOn;
        String askedBy = questionFeed.askedBy;
        String topQuestion = questionFeed.quest_title;
        String category_id = questionFeed.category_id;
        String answerCount = questionFeed.answerCount;

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date time =  df.parse(askedOn);
            PrettyTime p = new PrettyTime();
            askedOn = "Asked "+p.format(time);
        } catch (ParseException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        holder.topQuestions.setText(topQuestion);
        holder.askedOn.setText(askedOn);
        holder.totalResponse.setText(answerCount + " Response");
    }
        @Override
    public int getItemCount() {
        return list_main_feed.size();
    }

}
