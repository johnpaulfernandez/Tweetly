package com.codepath.apps.twitterclient;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.fragments.UserTimelineFragment;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {
    TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        client = TwitterApplication.getRestClient();

        // Get the account info
        client.getVerifyCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSON(response);

                // My current user account's info
                getSupportActionBar().setTitle("@" + user.getScreenName());
                populateProfileHeader(user);
            }


        });

        // Get the screen name from the activity that launches this activity
        String screenName = getIntent().getStringExtra("screenName");

        // The activity has not been ran before
        if (savedInstanceState == null) {
            // Create the user timeline fragment
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);

            // Display the user timeline fragment within this activity (dynamically)
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }
    }

    private void populateProfileHeader(User user) {

        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);

        ivProfileImage.setImageResource(android.R.color.transparent); // Clear out the old image for a recycled view and put a transparent placeholder
        Picasso.with(this).load(user.getProfileImageUrl())
                .into(ivProfileImage);        // Send an API request using Picasso library, load the imageURL, retrieve the data, and insert it into the imageView
        tvName.setText(user.getName());
        tvTagline.setText(user.getTagline());
        tvFollowers.setText(user.getFollowersCount() + " Followers");
        tvFollowing.setText(user.getFollowing() + " Following");
    }
}
