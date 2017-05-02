package com.codepath.apps.twitterclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.apps.twitterclient.models.User;
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
    EditText etNewTweet;
    TextView tvCounter;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compose_tweet);

        tvName = (TextView) findViewById(R.id.tvName);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        etNewTweet = (EditText) findViewById(R.id.etNewTweet);
        tvCounter = (TextView) findViewById(R.id.tvCounter);


        client = TwitterApplication.getRestClient();  // Singleton client instance

        user = new User();
        getCurrentUser();
        tweetListener();
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

                try {
                    // Deserialize JSON
                    user.setName(jsonObject.getString("name"));
                    user.setScreenName(jsonObject.getString("screen_name"));
                    user.setProfileImageUrl(jsonObject.getString("profile_image_url"));

                    // Set values into views
                    tvName.setText(user.getName());
                    tvScreenName.setText(user.getScreenName());
                    ivProfileImage.setImageResource(android.R.color.transparent); // Clear out the old image for a recycled view and put a transparent placeholder
                    Picasso.with(getContext())
                            .load(user.getProfileImageUrl())
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

    public void onTweet(View view) {

        String tweet = etNewTweet.getText().toString();

        client.postUpdate(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", "onSuccess: " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "onFailure: " + errorResponse.toString());
            }
        }, tweet);

        // Prepare data intent
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("tweet", tweet);
        data.putExtra("user", user);
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }

    private void tweetListener() {

        etNewTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // this will show characters remaining
                tvCounter.setText(140 - s.toString().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
