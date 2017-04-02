package com.sahilpaudel.app.suggme;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Sahil Paudel on 4/2/2017.
 */


public class CustomProgressDialog {

    FragmentManager manager;
    Fragment fragment;
    Context context;
    FragmentTransaction transaction;
    public CustomProgressDialog(Context context, FragmentManager manager) {
        this.context = context;
        this.manager = manager;
        fragment = new LoadingFragment();
        transaction = manager.beginTransaction();
    }

    public void show() {
        transaction.add(R.id.contentFragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void dismiss() {
        manager.popBackStack();
    }

}
