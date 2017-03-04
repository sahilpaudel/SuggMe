package com.sahilpaudel.app.suggme.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahilpaudel.app.suggme.R;
import com.sahilpaudel.app.suggme.dataproviders.MainFeed;

import java.util.List;

/**
 * Created by Sahil Paudel on 3/2/2017.
 */

public class MainFeedAdapter extends RecyclerView.Adapter<MainFeedAdapter.MyViewHolder> {

    List<MainFeed> list_main_feed;
    Context context;

    public MainFeedAdapter(Context context, List<MainFeed> list_main_feed) {
        this.context = context;
        this.list_main_feed = list_main_feed;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView topQuestions,
                answeredOn,
                userName,
                topAnswer;
        ImageView ruImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            topQuestions = (TextView)itemView.findViewById(R.id.mainQuestion);
            userName = (TextView)itemView.findViewById(R.id.responding_user_name);
            answeredOn = (TextView)itemView.findViewById(R.id.main_answer_date);
            topAnswer = (TextView)itemView.findViewById(R.id.topAnswer);
            ruImage = (ImageView)itemView.findViewById(R.id.responding_user_image);
        }
    }

    @Override
    public MainFeedAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_feed_card, parent, false);
        return new MainFeedAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainFeedAdapter.MyViewHolder holder, int position) {
           MainFeed mainFeed = list_main_feed.get(position);
            String userName = mainFeed.first_name +" "+mainFeed.last_name;
            String askedOn = mainFeed.askedOn;
            String topAnswer = mainFeed.answer_content;
            String topQuestion = mainFeed.quest_title;
            String answeredOn = mainFeed.answeredOn;
            String userId = mainFeed.answeredBy;
            String postId = mainFeed.question_id;
            if(topAnswer.equals("null")) {
                holder.ruImage.setVisibility(View.GONE);
                holder.answeredOn.setVisibility(View.GONE);
                holder.userName.setVisibility(View.GONE);
                holder.topAnswer.setVisibility(View.GONE);
            }
                holder.topQuestions.setText(topQuestion);
                holder.userName.setText(userName);
                holder.answeredOn.setText(answeredOn);
                holder.topAnswer.setText(topAnswer);

    }

    @Override
    public int getItemCount() {
        return list_main_feed.size();
    }

}
