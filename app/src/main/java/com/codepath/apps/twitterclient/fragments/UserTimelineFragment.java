package com.codepath.apps.twitterclient.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.twitterclient.EndlessRecyclerViewScrollListener;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TweetDetailsActivity;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.TwitterClient;
import com.codepath.apps.twitterclient.adapters.TweetsRecyclerViewAdapter;
import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.twitterclient.R.id.rvTweets;

/**
 * Created by John on 9/14/2017.
 */

public class UserTimelineFragment extends TweetsListFragment {
    protected TwitterClient client;
    private SwipeRefreshLayout swipeContainer;



    // To pass information from the activity to this fragment, setup a constructor that accepts arguments
    // for later use in this fragment when populating the user timeline i.e., getUserTimeline()
    // UserTimelineFragment.newInstance("jpvfernandez");
    // If screenName is null, will default to the user currently logged-in
    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screenName", screenName);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup layout manager for items with orientation
        // Also supports `LinearLayoutManager.HORIZONTAL`
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        // Optionally customize the position you want to default scroll to
        layoutManager.scrollToPosition(0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);

        // Lookup the recyclerview in activity layout
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweets);

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        // Attach the adapter to the recyclerview to populate items
        rvTweets.setAdapter(adapter);

        rvTweets.setItemAnimator(new DefaultItemAnimator());

        // Attach layout manager to the RecyclerView
        rvTweets.setLayoutManager(layoutManager);

        client = TwitterApplication.getRestClient();  // Singleton client instance

        //since_id = 1;
        //max_id = 0;
        page = 1;
        populateTimeline();

        setupEndlessScroll();
        launchTweetDetailsActivity();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if (isNetworkAvailable()) {
                    fetchTimelineAsync(0);
                } else {
                    Toast.makeText(getActivity(), "Cannot retrieve new tweets at this time.\nNetwork unavailable.", Toast.LENGTH_SHORT).show();
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                }
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // Send an API request to get the timeline JSON
    // Fill the listview by creating the tweet objects from the JSON
    public void populateTimeline() {

        if (isNetworkAvailable()) {
            // Pull out the screen name from the activity that creates this fragment
            String screenName = getArguments().getString("screenName");

            client.getUserTimeline(screenName, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    Log.d("DEBUG", "onSuccess: " + jsonArray.toString());

                    // Deserialize JSON
                    // Create models and add them to the adapter
                    ArrayList<Tweet> jsonTweets = Tweet.convertJSONArraytoTweets(jsonArray);
                    addAll(jsonTweets);

                    dbFlowSave();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.d("DEBUG", "onFailure: " + errorResponse.toString());
                }
            }, page++);
        } else {
            Toast.makeText(getActivity(), "Cannot retrieve new tweets at this time.\nNetwork unavailable.", Toast.LENGTH_SHORT).show();
            List<Tweet> tweets = SQLite.select().from(Tweet.class).queryList();
            addAll(tweets);
        }
    }

    // Launch Movie Details Activity
    public void launchTweetDetailsActivity() {

        Log.d("DEBUG", "launchTweetDetailsActivity");

        adapter.setOnItemClickListener(new TweetsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Log.d("DEBUG", "launchTweetDetailsActivity Clicked");
                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                // Get the data item for this position
                Tweet tweet = tweets.get(position);

                // Launch Movie Details Activity
                Intent i = new Intent(getActivity(), TweetDetailsActivity.class);
                startActivity(i);
            }
        });
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.

        client.getHomeTimeline(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {

                // Remember to CLEAR OUT old items before appending in the new ones
                tweets.clear();

                // ...the data has come back, add new items to your adapter...
                // Deserialize JSON
                // Create models and add them to the adapter
                // Load the model data into ListView
                ArrayList<Tweet> jsonTweets = Tweet.convertJSONArraytoTweets(jsonArray);
                addAll(jsonTweets);

                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

                dbFlowSave();
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", "Fetch timeline error: " + errorResponse.toString());
            }
        }, page++);
    }

    public void setupEndlessScroll() {
        // Attach the listener to the AdapterView onCreate
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);
            }
        };

        // Attach the listener to the AdapterView onCreate
        rvTweets.addOnScrollListener(scrollListener);
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int page) {
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
