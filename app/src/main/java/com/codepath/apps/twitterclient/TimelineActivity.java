package com.codepath.apps.twitterclient;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter adapter;
    private ListView lvTweets;
    private static Context context;
    private static int since_id;
    private static int max_id;
    private static int page;
    EndlessScrollListener scrollListener;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context mContext) {
        TimelineActivity.context = mContext;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Find the ListView
        lvTweets = (ListView) findViewById(R.id.lvTweets);

        // Create the arraylist (data source)
        tweets = new ArrayList<>();

        // Construct the adapter from data source
        adapter = new TweetsArrayAdapter(this, tweets);

        // Connect the adapter to the ListView
        lvTweets.setAdapter(adapter);

        client = TwitterApplication.getRestClient();  // Singleton client instance

        //since_id = 1;
        //max_id = 0;
        page = 1;
        populateTimeline();

//        // 2. Notify the adapter of the update
//        adapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
//        // 3. Reset endless scroll listener when performing a new search
//        scrollListener.resetState();

        setupEndlessScroll();
    }

    // Send an API request to get the timeline JSON
    // Fill the listview by creating the tweet objects from the JSON
    private void populateTimeline() {

        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                Log.d("DEBUG", "onSuccess: " + jsonArray.toString());

                // Deserialize JSON
                // Create models and add them to the adapter
                // Load the model data into ListView
                ArrayList<Tweet> tweets = Tweet.convertJSONArraytoTweets(jsonArray);
                adapter.addAll(tweets);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", "onFailure: " + errorResponse.toString());
            }
        }, page++);
    }


    private void setupEndlessScroll() {
        // Attach the listener to the AdapterView onCreate
        scrollListener = new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        };

        // Attach the listener to the AdapterView onCreate
        lvTweets.setOnScrollListener(scrollListener);
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    private void loadNextDataFromApi(int page) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyDataSetChanged()`
        //since_id += 25;
        //max_id = since_id + 25 - 1;
        populateTimeline();
    }
}
