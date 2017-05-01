package com.codepath.apps.twitterclient;

import android.content.Context;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.twitterclient.R.string.tweet;

/**
 * Created by John on 4/30/2017.
 */

public class ComposeTweetActivity extends AppCompatActivity{

    private TwitterClient client;
    private static Context context;
    TextView tvName;
    TextView tvScreenName;
    ImageView ivProfileImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compose_tweet);

        tvName = (TextView) findViewById(R.id.tvName);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);

        client = TwitterApplication.getRestClient();  // Singleton client instance

        getCurrentUser();
    }

    public static void setContext(Context mContext) {
        ComposeTweetActivity.context = mContext;
    }

    public static Context getContext() {
        return context;
    }

    // Send an API request to get the current user's account details
    private void getCurrentUser() {

        client.getVerifyCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                Log.d("DEBUG", "onSuccess: " + jsonObject.toString());

                // Deserialize JSON
                try {
                    tvName.setText(jsonObject.getString("name"));
                    tvScreenName.setText("@" + jsonObject.getString("screen_name"));

                    ivProfileImage.setImageResource(android.R.color.transparent); // Clear out the old image for a recycled view and put a transparent placeholder
                    Picasso.with(getContext())
                            .load(jsonObject.getString("profile_image_url"))
                            .into(ivProfileImage);        // Send an API request using Picasso library, load the imageURL, retrieve the data, and insert it into the imageView
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "onFailure: " + errorResponse.toString());
            }
        });
    }
}
