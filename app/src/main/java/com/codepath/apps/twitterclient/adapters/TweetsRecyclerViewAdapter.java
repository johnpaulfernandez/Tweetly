package com.codepath.apps.twitterclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.codepath.apps.twitterclient.R.id.ivImageUrl;
import static com.codepath.apps.twitterclient.R.id.ivProfileImage;
import static com.codepath.apps.twitterclient.R.id.tvBody;
import static com.codepath.apps.twitterclient.R.id.tvScreenName;
import static com.codepath.apps.twitterclient.R.id.tvTimeStamp;
import static com.codepath.apps.twitterclient.R.id.tvUserName;
import static com.codepath.apps.twitterclient.R.string.tweet;

/**
 * Created by John on 3/25/2017.
 */

public class TweetsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Store a member variable for the tweets
    private List<Tweet> mTweets;
    // Store the context for easy access
    private Context mContext;

    private final int NO_IMAGE = 0, WITH_IMAGE = 1;


    /***** Creating OnItemClickListener *****/
    // Define listener member variable
    public static OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Pass in the tweets array into the constructor
    public TweetsRecyclerViewAdapter(Context context, List<Tweet> tweets) {
        mTweets = tweets;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        // Return a new holder instance
        switch (viewType) {
            case NO_IMAGE:
                View v1 = inflater.inflate(R.layout.item_tweets_no_image, parent, false);
                viewHolder = new ViewHolderNoImage(v1);
                break;
            case WITH_IMAGE:
                View v2 = inflater.inflate(R.layout.item_tweets, parent, false);
                viewHolder = new ViewHolderWithImage(v2);
                break;
            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new ViewHolderNoImage(v);
                break;
        }
        return viewHolder;
    }

    /**
     * This method internally calls onBindViewHolder(ViewHolder, int) to update the
     * RecyclerView.ViewHolder contents with the item at the given position
     * and also sets up some private fields to be used by RecyclerView.
     *
     * @param viewHolder The type of RecyclerView.ViewHolder to populate
     * @param position Item position in the viewgroup.
     */
    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case NO_IMAGE:
                ViewHolderNoImage vh1 = (ViewHolderNoImage) viewHolder;
                configureViewHolderNoImage(vh1, position);
                break;
            case WITH_IMAGE:
                ViewHolderWithImage vh2 = (ViewHolderWithImage) viewHolder;
                configureViewHolderWithImage(vh2, position);
                break;
        }

        // Setup the click listener
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Triggers click upwards to the adapter on click
                if (listener != null) {
                    int position = viewHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(viewHolder.itemView, position);
                    }
                }
            }
        });
    }


    private void configureViewHolderNoImage(ViewHolderNoImage vh1, int position) {
        // Get the data model based on position
        Tweet tweet = mTweets.get(position);

        if (tweet != null) {

            vh1.getTvScreenName().setText(tweet.getUser().getName().toString());
            vh1.getTvUserName().setText("@" + tweet.getUser().getScreenName());
            vh1.getTvBody().setText(tweet.getBody().toString());
            vh1.getTvTimeStamp().setText(tweet.getRelativeTimeAgo(tweet.getCreatedAt()));

            vh1.getIvProfileImage().setImageResource(android.R.color.transparent); // Clear out the old image for a recycled view and put a transparent placeholder
            Picasso.with(getContext())
                    .load(tweet.getUser().getProfileImageUrl())
                    .into(vh1.getIvProfileImage());        // Send an API request using Picasso library, load the imageURL, retrieve the data, and insert it into the imageView

        }
    }

    private void configureViewHolderWithImage(ViewHolderWithImage vh2, int position) {
        // Get the data model based on position
        Tweet tweet = mTweets.get(position);

        if (tweet != null) {
            vh2.getTvScreenName().setText(tweet.getUser().getName());
            vh2.getTvUserName().setText("@" + tweet.getUser().getScreenName());
            vh2.getTvBody().setText(tweet.getBody().toString());
            vh2.getTvTimeStamp().setText(tweet.getRelativeTimeAgo(tweet.getCreatedAt()));
            vh2.getIvProfileImage().setImageResource(android.R.color.transparent); // Clear out the old image for a recycled view and put a transparent placeholder
            Picasso.with(getContext())
                    .load(tweet.getUser().getProfileImageUrl())
                    .into(vh2.getIvProfileImage());        // Send an API request using Picasso library, load the imageURL, retrieve the data, and insert it into the imageView

            vh2.getIvImageUrl().setImageResource(android.R.color.transparent); // Clear out the old image for a recycled view and put a transparent placeholder

            Picasso.with(getContext())
                    .load(tweet.getMediaImageUrl() + ":large")
                    .resize(600, 300) // resizes the image to these dimensions (in pixel)
                    .centerCrop()
                    .into(vh2.getIvImageUrl());        // Send an API request using Picasso library, load the imageURL, retrieve the data, and insert it into the imageView
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    @Override
    public int getItemViewType(int position) {

        Log.d("DEBUG", "mediaimage = " + mTweets.get(position).getMediaImageUrl());
        if (mTweets.get(position).getMediaImageUrl() != null) {
            return WITH_IMAGE;
        } else {
            return NO_IMAGE;
        }
    }

}
