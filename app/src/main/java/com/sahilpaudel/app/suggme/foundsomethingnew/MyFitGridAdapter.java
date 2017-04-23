package com.sahilpaudel.app.suggme.foundsomethingnew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahilpaudel.app.suggme.R;

/**
 * Created by Sahil Paudel on 4/17/2017.
 */

public class MyFitGridAdapter extends BaseAdapter {

    Context context;
    private int[] chars;
    private String[] title;

    public MyFitGridAdapter(String[] title, Context context) {
        this.context = context;
        this.title = title;
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public Object getItem(int i) {
        return title[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {

            grid = inflater.inflate(R.layout.something_new_gridview, null);
            TextView mTitle = (TextView) grid.findViewById(R.id.tv_NewTitle);
            TextView mLandmark = (TextView) grid.findViewById(R.id.tv_NewLandMark);
            ImageView mImage = (ImageView)grid.findViewById(R.id.iv_NewImage);
            mTitle.setText(title[position]);
        } else {
            grid = view;
        }

        return grid;
    }
}
