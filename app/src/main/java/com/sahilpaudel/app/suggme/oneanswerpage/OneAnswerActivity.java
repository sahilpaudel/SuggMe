package com.sahilpaudel.app.suggme.oneanswerpage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sahilpaudel.app.suggme.R;

public class OneAnswerActivity extends AppCompatActivity {

    ImageView imageViewClose;
    TextView tvAnsweredBy, tvAnsweredOn, tvAnswer;
    Button bt_comment;
    ToggleButton bt_upvote;
    ImageView imageView;
    ImageView ivShowMore;

    TextView tvQuestion, tvAskedOn, tvalreadyAnswered;
    CardView alreadyAnsweredCard;
    Button btWriteAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_answer);

        //implement close activity on click
        imageViewClose = (ImageView)findViewById(R.id.closeActivity);
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                finish();
            }
        });

        tvAnsweredBy = (TextView)findViewById(R.id.answeredByName);
        tvAnsweredOn = (TextView)findViewById(R.id.answeredOn);
        tvAnswer = (TextView)findViewById(R.id.answers);
        bt_upvote = (ToggleButton)findViewById(R.id.like);
        bt_comment = (Button)findViewById(R.id.suggest);
        imageView = (ImageView)findViewById(R.id.answeredByImage);
        ivShowMore = (ImageView)findViewById(R.id.ivShowMore);
        tvQuestion = (TextView)findViewById(R.id.questionContent);
        tvAskedOn = (TextView)findViewById(R.id.askedDate);
        btWriteAnswer = (Button)findViewById(R.id.writeAnswer);
        alreadyAnsweredCard = (CardView)findViewById(R.id.ifAlreadyWrittenCard);
        tvalreadyAnswered = (TextView)findViewById(R.id.ifAlreadyWritten);


    }
}
