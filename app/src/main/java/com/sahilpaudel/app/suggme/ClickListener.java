package com.sahilpaudel.app.suggme;

import android.view.View;

/**
 * Created by Sahil Paudel on 2/11/2017.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
