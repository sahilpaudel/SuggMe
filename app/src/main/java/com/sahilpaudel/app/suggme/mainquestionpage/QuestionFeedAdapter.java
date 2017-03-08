package com.sahilpaudel.app.suggme.mainquestionpage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sahilpaudel.app.suggme.R;

import java.util.List;

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

                holder.topQuestions.setText(topQuestion);
                holder.askedOn.setText(askedOn);
                holder.totalResponse.setText(answerCount+" Response");

    }

    @Override
    public int getItemCount() {
        return list_main_feed.size();
    }

}
