package com.codepath.apps.twitterclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.adapters.TweetsRecyclerViewAdapter;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class TimelineActivity extends AppCompatActivity {

    private static final int NEW_TWEET_REQUEST_CODE = 20;
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsRecyclerViewAdapter adapter;
    private RecyclerView rvTweets;
    private static Context context;
    private static int since_id;
    private static int max_id;
    private static int page;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeContainer;
    LinearLayoutManager layoutManager;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context mContext) {
        TimelineActivity.context = mContext;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timeline);

        // Create the arraylist (data source)
        tweets = new ArrayList<>();

        // Create adapter passing in the articles
        adapter = new TweetsRecyclerViewAdapter(this, tweets);

        // Lookup the recyclerview in activity layout
        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);

        // Attach the adapter to the recyclerview to populate items
        rvTweets.setAdapter(adapter);


        // Setup layout manager for items with orientation
        // Also supports `LinearLayoutManager.HORIZONTAL`
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // Optionally customize the position you want to default scroll to
        layoutManager.scrollToPosition(0);

        rvTweets.setItemAnimator(new DefaultItemAnimator());

        // Attach layout manager to the RecyclerView
        rvTweets.setLayoutManager(layoutManager);



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
        launchTweetDetailsActivity();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

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
                    Toast.makeText(TimelineActivity.this, "Cannot retrieve new tweets at this time.\nNetwork unavailable.", Toast.LENGTH_SHORT).show();
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
    private void populateTimeline() {

        if (isNetworkAvailable()) {
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    Log.d("DEBUG", "onSuccess: " + jsonArray.toString());

                    // Deserialize JSON
                    // Create models and add them to the adapter
                    ArrayList<Tweet> jsonTweets = Tweet.convertJSONArraytoTweets(jsonArray);
                    tweets.addAll(jsonTweets);
                    adapter.notifyDataSetChanged();
                    dbFlowSave();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.d("DEBUG", "onFailure: " + errorResponse.toString());
                }
            }, page++);
        } else {
            Toast.makeText(TimelineActivity.this, "Cannot retrieve new tweets at this time.\nNetwork unavailable.", Toast.LENGTH_SHORT).show();
            List<Tweet> tweets = SQLite.select().from(Tweet.class).queryList();
            tweets.addAll(tweets);
            adapter.notifyDataSetChanged();
        }
    }


    private void setupEndlessScroll() {
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

    public void onComposeAction(MenuItem item) {
        // Launch Compose New Tweet Activity
        Intent i = new Intent(TimelineActivity.this, ComposeTweetActivity.class);
        startActivityForResult(i, NEW_TWEET_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Tweet newTweet = new Tweet();
        User user;

        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == NEW_TWEET_REQUEST_CODE) {
            // Extract tweet value from result extras
            newTweet.setBody(data.getExtras().getString("tweet"));
            user = (User) data.getSerializableExtra("user");
            newTweet.setUser(user);
            newTweet.setCreatedAt("");

            // Toast the name to display temporarily on screen
            //Toast.makeText(this, tweet, Toast.LENGTH_SHORT).show();

            // Toast the name to display temporarily on screen
            //Toast.makeText(this, user.getScreenName(), Toast.LENGTH_SHORT).show();

            tweets.add(0, newTweet);
            adapter.notifyItemInserted(0);
        }
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
                tweets.addAll(jsonTweets);
                adapter.notifyDataSetChanged();

                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

                dbFlowSave();
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", "Fetch timeline error: " + errorResponse.toString());
            }
        }, page++);
    }

    public void dbFlowSave() {

        try {
            // Save Tweets table
            FlowManager.getDatabase(MyDatabase.class).beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                    new ProcessModelTransaction.ProcessModel<Tweet>() {
                        @Override
                        public void processModel(Tweet tweet, DatabaseWrapper wrapper) {
                            tweet.save();
                        }
                    }).addAll(tweets).build())
                    .error(new Transaction.Error() {
                        @Override
                        public void onError(Transaction transaction, Throwable error) {
                            Log.e("ERROR", "DBFlow error");
                        }
                    })
                    .success(new Transaction.Success() {
                        @Override
                        public void onSuccess(Transaction transaction) {
                            Log.d("DEBUG", "DBFlow Success");
                        }
                    }).build().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    // Launch Movie Details Activity
    public void launchTweetDetailsActivity() {

        Log.d("DEBUG", "launchTweetDetailsActivity");

        adapter.setOnItemClickListener(new TweetsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Log.d("DEBUG", "launchTweetDetailsActivity Clicked");
                Toast.makeText(TimelineActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                // Get the data item for this position
                Tweet tweet = tweets.get(position);

                // Launch Movie Details Activity
                Intent i = new Intent(TimelineActivity.this, TweetDetailsActivity.class);
                startActivity(i);
            }
        });
    }
}
