package com.sahilpaudel.app.suggme.foundsomethingnew;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sahilpaudel.app.suggme.R;

public class SomethingNewFragment extends Fragment {

    View view;
    GridView gridView;
    MyFitGridAdapter fitGridAdapter;

    private String[] title = {
            "Title 1","Title 2","Title 3","Title 4",
            "Title 5","Title 6","Title 7","Title 8",
            "Title 9","Title 10","Title 11","Title 12",
            "Title 13","Title 14","Title 15","Title 16",
    };

    public SomethingNewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_something_new, container, false);
        gridView = (GridView) view.findViewById(R.id.gridView);
        fitGridAdapter = new MyFitGridAdapter(title,getActivity());
        gridView.setAdapter(fitGridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                someNewAlertBox();
            }
        });
        return view;
    }

    private void someNewAlertBox() {

        final AlertDialog.Builder singleSomethingNew = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.single_something_new_view,null);
        singleSomethingNew.setView(dialogView);
        final AlertDialog b = singleSomethingNew.create();
        b.show();
    }

}
