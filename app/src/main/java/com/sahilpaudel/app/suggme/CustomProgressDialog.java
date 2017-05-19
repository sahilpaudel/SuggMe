package com.sahilpaudel.app.suggme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.sahilpaudel.app.suggme.profile.useranswers.UserAnswerAdapter;

/**
 * Created by Sahil Paudel on 4/2/2017.
 */


public class CustomProgressDialog {

    Context context;

    public CustomProgressDialog(Context context) {
      this.context = context;
    }

    public void show() {
       context.startActivity(new Intent(context, MyLoadingActivity.class));
    }

    public void dismiss() {
        ((Activity)context).finish();

    }

}
