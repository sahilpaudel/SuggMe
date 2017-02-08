package com.sahilpaudel.app.suggme;

/**
 * Created by Sahil Paudel on 2/7/2017.
 */

public class GetFeedItem {

    private int id;
    private String userName, Question, timeStamp, answer;

    public GetFeedItem() {
    }

    public GetFeedItem(String answer, int id, String question, String timeStamp, String userName) {
        this.answer = answer;
        this.id = id;
        Question = question;
        this.timeStamp = timeStamp;
        this.userName = userName;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
