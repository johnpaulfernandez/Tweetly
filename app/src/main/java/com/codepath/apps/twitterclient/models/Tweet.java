package com.codepath.apps.twitterclient.models;

import android.text.format.DateUtils;

import com.codepath.apps.twitterclient.TimelineActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * Created by John on 4/7/2017.
 */

// Parse the JSON + Store the data, encapsulate state logic or display logic
public class Tweet {

    private String body;
    private long uid;   // Unique id for the tweet
    private String createdAt;
    private User user;

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    // Turn JSON objects into Tweet models
    // Tweet.fromJSON("{...}") => <Tweet>
    public static Tweet deserializeJSONObject (JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    // Pass in the array of json objects
    // Tweet.convertJSONArraytoTweets([{...}, {...}]) => List<Tweet>
    public static ArrayList<Tweet> convertJSONArraytoTweets(JSONArray jsonArray) {
        ArrayList<Tweet> tweetsList = new ArrayList<>();

        // Iterate the JSONArray and create the list of tweets model
        for (int i = 0; i < jsonArray.length() ; i++) {
            try {
                JSONObject tweetJSONobject = jsonArray.getJSONObject(i);
                Tweet tweet = deserializeJSONObject(tweetJSONobject);

                if (tweet != null) {
                    tweetsList.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();

                // Continue to process other tweets even if error is encountered in one of the tweets
                continue;
            }
        }
        return tweetsList;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
