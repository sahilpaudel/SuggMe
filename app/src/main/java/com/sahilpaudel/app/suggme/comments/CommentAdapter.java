package com.sahilpaudel.app.suggme.comments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sahil Paudel on 3/29/2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {


    List<Comments> list_comment;
    Context context;

    public CommentAdapter(Context context, List<Comments> list_comment) {
        this.context = context;
        this.list_comment = list_comment;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvCommentBy;
        TextView tvCommentOn;
        TextView tvCommentText;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvCommentBy = (TextView)itemView.findViewById(R.id.commenterName);
            tvCommentOn = (TextView)itemView.findViewById(R.id.commentDate);
            tvCommentText = (TextView)itemView.findViewById(R.id.commentContent);
        }
    }

    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comments_content, parent, false);
        return new CommentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.MyViewHolder holder, int position) {

        Comments comments = list_comment.get(position);
        String userName = comments.userName;
        String entryOn = comments.entryOn;
        String comment = comments.commentContent;
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date time =  df.parse(entryOn);
            PrettyTime p = new PrettyTime();
            entryOn = "written "+p.format(time);
        } catch (ParseException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        holder.tvCommentBy.setText(userName);
        holder.tvCommentOn.setText(entryOn);
        holder.tvCommentText.setText(comment);
    }
    @Override
    public int getItemCount() {
        return list_comment.size();
    }

}
