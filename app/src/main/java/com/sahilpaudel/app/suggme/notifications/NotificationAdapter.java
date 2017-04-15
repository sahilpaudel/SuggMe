package com.sahilpaudel.app.suggme.notifications;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by Sahil Paudel on 4/14/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>{

    private List<NotificationInfo> list;
    private Context context;

    public NotificationAdapter(Context context, List<NotificationInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_view, parent, false);
        return new NotificationAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NotificationInfo notification = list.get(position);
        String content = notification.NotificationContent;
        String image = notification.NotificationImage;
        String time = notification.NotificationTime;
        String status = notification.NotificationStatus;

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date date =  df.parse(time);
            PrettyTime p = new PrettyTime();
            time = p.format(date);
        } catch (ParseException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (!image.equals("")){
            Picasso.with(context).load(image).into(holder.NotificationImage);
        } else {
            Picasso.with(context).load(R.drawable.com_facebook_profile_picture_blank_square).into(holder.NotificationImage);
        }

        if (status.equals("1"))
            holder.NotificationStatus.setVisibility(View.VISIBLE);
        else
            holder.NotificationStatus.setVisibility(View.INVISIBLE);

        holder.NotificationInfo.setText(content);
        holder.NotificationTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView NotificationImage;
        ImageView NotificationStatus;
        TextView NotificationTime;
        TextView NotificationInfo;

        public MyViewHolder(View itemView) {
            super(itemView);
            NotificationImage = (ImageView)itemView.findViewById(R.id.notificationImage);
            NotificationStatus = (ImageView)itemView.findViewById(R.id.ivNewNotification);
            NotificationTime = (TextView)itemView.findViewById(R.id.notificationTime);
            NotificationInfo = (TextView)itemView.findViewById(R.id.notification_content);
        }
    }
}
