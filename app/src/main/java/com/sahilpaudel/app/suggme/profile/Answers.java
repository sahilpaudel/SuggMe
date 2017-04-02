package com.sahilpaudel.app.suggme.profile;

/**
 * Created by Sahil Paudel on 4/2/2017.
 */

public class Answers {

    public String answer_id;
    public String question_id;
    public String answer_content;
    public String user_id;
    public String first_name;
    public String last_name;
    public String entryOn;
    public String image_url;
    public String isActive;
    public String isUpdated;
    public String isAnonymous;
    public String like;

    @Override
    public String toString() {
        return answer_id+" "+like;
    }
}
