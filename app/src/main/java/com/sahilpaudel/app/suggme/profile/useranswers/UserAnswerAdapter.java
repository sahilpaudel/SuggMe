package com.sahilpaudel.app.suggme.profile.useranswers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sahilpaudel.app.suggme.R;
import com.sahilpaudel.app.suggme.SharedPrefSuggMe;
import com.sahilpaudel.app.suggme.singlequestionpage.AnswerFeed;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sahil Paudel on 4/3/2017.
 */

public class UserAnswerAdapter extends RecyclerView.Adapter<UserAnswerAdapter.MyViewHolder> {

    List<AnswerFeed> feedList;
    Context context;

    String answeredBy;

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
    public void onBindViewHolder(MyViewHolder holder, int position) {

        AnswerFeed answerFeed = feedList.get(position);

        String user_id = answerFeed.user_id;
        answeredBy = answerFeed.first_name +" "+answerFeed.last_name;

        if (SharedPrefSuggMe.getInstance(context).getUserId().equals(user_id)) {
            answeredBy = "Me";
        }

        String answer = answerFeed.answer_content;
        String answeredOn = answerFeed.entryOn;
        String image_url = answerFeed.image_url;
        String answer_id = answerFeed.answer_id;
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
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvAnsweredBy, tvAnsweredOn, tvAnswer, tvQuestionTitle, tvQuestionDate;
        Button bt_upvote, bt_comment;
        ImageView imageView;
        ImageView ivShowMore;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvQuestionTitle = (TextView)itemView.findViewById(R.id.tvQuestionTitle);
            tvQuestionDate = (TextView)itemView.findViewById(R.id.tvQuestionDate);
            tvAnsweredBy = (TextView)itemView.findViewById(R.id.tvAnsweredByName);
            tvAnsweredOn = (TextView)itemView.findViewById(R.id.tvAnsweredOn);
            tvAnswer = (TextView)itemView.findViewById(R.id.tvAnswerContent);
            bt_upvote = (Button)itemView.findViewById(R.id.bt_upvote);
            bt_comment = (Button)itemView.findViewById(R.id.bt_comment);
            imageView = (ImageView)itemView.findViewById(R.id.ivAnsweredByImage);
            ivShowMore = (ImageView)itemView.findViewById(R.id.ivShowMore);

        }
    }
}
