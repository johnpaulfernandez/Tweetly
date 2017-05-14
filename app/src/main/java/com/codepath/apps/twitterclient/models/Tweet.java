package com.codepath.apps.twitterclient.models;

import android.text.format.DateUtils;

import com.codepath.apps.twitterclient.MyDatabase;
import com.codepath.apps.twitterclient.TimelineActivity;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

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
@Table(database = MyDatabase.class)
public class Tweet extends BaseModel {

    @PrimaryKey
    @Column
    private long uid;   // Unique id for the tweet

    // Define table fields
    @Column
    private String body;
    @Column
    private String createdAt;
    @Column
    @ForeignKey(saveForeignKeyModel = false)
    private User user;
    @Column
    private String mediaImageUrl;

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

    public void setBody(String body) {
        this.body = body;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMediaImageUrl() {
        return mediaImageUrl + ":large";
    }

    public void setMediaImageUrl(String mediaImageUrl) {
        this.mediaImageUrl = mediaImageUrl;
    }

    public Tweet() {
    }

    // Turn JSON objects into Tweet models
    // Tweet.fromJSON("{...}") => <Tweet>
    public static Tweet deserializeJSONObject (JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        try {
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.uid = jsonObject.getLong("id");
            tweet.body = jsonObject.getString("text");
            tweet.createdAt = jsonObject.getString("created_at");

            JSONObject entities  = jsonObject.getJSONObject("entities");
            if(entities.has("media")) {
                tweet.mediaImageUrl = entities.getJSONArray("media").getJSONObject(0)
                        .getString("media_url");
            }

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
