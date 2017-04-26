package com.sahilpaudel.app.suggme.foundsomethingnew;

/**
 * Created by Sahil Paudel on 4/26/2017.
 */

import com.sahilpaudel.app.suggme.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yalantis
 */
public class Utils {
    public static final List<Found> friends = new ArrayList<>();

    static {
        friends.add(new Found(R.drawable.avatar_icon, "ANASTASIA", "Sport", "Literature", "Music", "Art"));
        friends.add(new Found(R.drawable.myprofile, "IRENE", "Travelling", "Flights", "Books", "Painting"));
        friends.add(new Found(R.drawable.avatar_icon, "KATE", "Sales", "Pets", "Skiing", "Hairstyles"));
        friends.add(new Found(R.drawable.myprofile, "PAUL", "Android", "Development", "Design", "Wearables"));
        friends.add(new Found(R.drawable.avatar_icon, "DARIA", "Design", "Fitness", "Healthcare", "UI/UX"));
        friends.add(new Found(R.drawable.myprofile, "KIRILL", "Development", "Android", "Healthcare", "Sport"));
        friends.add(new Found(R.drawable.avatar_icon, "JULIA", "Cinema", "Music", "Tatoo", "Animals"));
        friends.add(new Found(R.drawable.myprofile, "YALANTIS", "Android", "IOS", "Application", "Development"));
    }
}
