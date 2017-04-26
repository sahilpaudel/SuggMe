package com.sahilpaudel.app.suggme.foundsomethingnew;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sahil Paudel on 4/26/2017.
 */

public class Found {
    private int avatar;
    private String nickname;
    private String description;
    private String entryOn;
    private String yes;
    private String no;

    public Found(int avatar, String nickname,String description, String entryOn, String yes, String no) {
        this.avatar = avatar;
        this.nickname = nickname;
        this.description = description;
        this.entryOn = entryOn;
        this.yes = yes;
        this.no = no;
    }

    public int getAvatar() {
        return avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public String getDescription() {
        return description;
    }

    public String getEntryOn() {
        return entryOn;
    }

    public String getNo() {
        return no;
    }

    public String getYes() {
        return yes;
    }

}