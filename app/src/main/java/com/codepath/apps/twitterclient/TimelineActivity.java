package com.codepath.apps.twitterclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.twitterclient.adapters.TweetsRecyclerViewAdapter;
import com.codepath.apps.twitterclient.fragments.HomeTimelineFragment;
import com.codepath.apps.twitterclient.fragments.MentionsTimelineFragment;
import com.codepath.apps.twitterclient.fragments.TweetsListFragment;
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

import static android.os.Build.VERSION_CODES.M;
import static com.codepath.apps.twitterclient.R.id.rvTweets;
import static com.codepath.apps.twitterclient.R.id.swipeContainer;


public class TimelineActivity extends AppCompatActivity {

    HomeTimelineFragment homeTimelineFragment;
    private static final int NEW_TWEET_REQUEST_CODE = 20;


    private static Context context;
    private static int since_id;
    private static int max_id;

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

        // Access the fragment if savedInstanceState is null
        // If savedInstanceState is not null, then no need to get another reference to the fragment
        // because the activity had been inflated before in the past and the fragment is most likely already in memory
//        if(savedInstanceState == null) {
//            tweetsListFragment = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
//        }
        homeTimelineFragment = (HomeTimelineFragment) getSupportFragmentManager().findFragmentById(0);

        // Get the viewpager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        // Set the viewpager adapter for the pager so that it can display items
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the tabstrip to the viewpager
        tabsStrip.setViewPager(vpPager);
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

            homeTimelineFragment.add(0, newTweet);
        }
    }

    public void onProfileView(MenuItem item) {
        // Launch the profile view
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    // Return the order of the fragments in the ViewPager
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;

        public String tabTitles[] = {"Home", "Mentions"};

        // fragmentManager is passed in from the activity
        // The fragmentManager that will be used to control the fragments
        // Used to insert and remove fragments from the page/activity
        public TweetsPagerAdapter (FragmentManager fm) {
            super(fm);
        }

        // Controls the order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            } else {
                return null;
            }
        }

        // Return the tab title at the top
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        // Returns how many fragments there are to swipe between
        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
