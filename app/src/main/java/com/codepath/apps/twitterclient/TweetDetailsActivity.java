package com.codepath.apps.twitterclient;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class TweetDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        Toast.makeText(TweetDetailsActivity.this, "Details", Toast.LENGTH_SHORT).show();

//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        Toast.makeText(TweetDetailsActivity.this, "Details -", Toast.LENGTH_SHORT).show();
    }

//    public boolean onOptionsItemSelected(MenuItem item){
//        Intent myIntent = new Intent(getApplicationContext(), TimelineActivity.class);
//        startActivityForResult(myIntent, 0);
//        return true;
//
//    }
}
