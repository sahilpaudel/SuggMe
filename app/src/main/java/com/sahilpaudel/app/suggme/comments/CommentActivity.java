package com.sahilpaudel.app.suggme.comments;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahilpaudel.app.suggme.R;

public class CommentActivity extends AppCompatActivity {

    ImageView imageViewClose;
    TextView txQuestionTitle;
    TextView txAnsweredBy;
    TextView txAnsweredOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        imageViewClose = (ImageView)findViewById(R.id.cross_close);
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txQuestionTitle = (TextView)findViewById(R.id.question_title);
        txAnsweredBy = (TextView)findViewById(R.id.answeredByName);
        txAnsweredOn = (TextView)findViewById(R.id.answeredOn);

        String answeredBy = getIntent().getStringExtra("ANSWERED_BY");
        String questionTitle = getIntent().getStringExtra("QUESTION_TITLE");
        String answeredOn = getIntent().getStringExtra("ANSWERED_ON");

        txQuestionTitle.setText(questionTitle);
        txAnsweredBy.setText("Answered By: "+answeredBy);
        txAnsweredOn.setText(answeredOn);

    }
}
