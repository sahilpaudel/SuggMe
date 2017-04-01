package com.sahilpaudel.app.suggme.comments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahilpaudel.app.suggme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentViewFragment extends Fragment {

    View view;

    ImageView imageViewEdit;
    ImageView imageViewClose;
    TextView txQuestionTitle;
    TextView txAnsweredBy;
    TextView txAnsweredOn;

    public CommentViewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_comment_view, container, false);

        imageViewEdit = (ImageView)view.findViewById(R.id.ivWriteComment);
        imageViewClose = (ImageView)view.findViewById(R.id.cross_close);
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        String questionTitle = getArguments().getString("QUESTION_TITLE");
        String answeredBy = getArguments().getString("ANSWERED_BY");
        String answeredOn = getArguments().getString("ANSWERED_ON");

        txQuestionTitle = (TextView)view.findViewById(R.id.question_title);
        txAnsweredBy = (TextView)view.findViewById(R.id.answeredByName);
        txAnsweredOn = (TextView)view.findViewById(R.id.answeredOn);

        txQuestionTitle.setText(questionTitle);
        txAnsweredBy.setText(answeredBy);
        txAnsweredOn.setText(answeredOn);

        return view;
    }

}
