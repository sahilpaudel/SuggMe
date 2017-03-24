package com.sahilpaudel.app.suggme.foundsomethingnew;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sahilpaudel.app.suggme.R;

public class SomethingNewFragment extends Fragment {

    View view;

    public SomethingNewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_something_new, container, false);
        return view;
    }
}
