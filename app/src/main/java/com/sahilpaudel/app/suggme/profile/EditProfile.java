package com.sahilpaudel.app.suggme.profile;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sahilpaudel.app.suggme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfile extends Fragment {


    public EditProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        return view;
    }

}
