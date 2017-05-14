package com.codepath.apps.twitterclient;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.models.Tweet;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import static android.R.attr.resource;

/**
 * Created by John on 4/7/2017.
 */

// Taking the Tweet objects and turning them into Views displayed in the list
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetsArrayAdapter(@NonNull Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
    }

    // Override and setup custom template
    // Return the view to be inserted into the list
    // Optional: Apply ViewHolderPattern to every array adapter to optimize getView function so it works faster
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // 1. Get the tweet
        Tweet tweet = getItem(position);

        // 2. Find or inflate the template
        if (convertView == null) {
            if(!TextUtils.isEmpty(tweet.getMediaImageUrl())) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweets, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweets_no_image, parent, false);
            }
        }

        // 3. Find the subviews within the convertView to fill with data in the template
        ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        TextView tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvTimeStamp = (TextView) convertView.findViewById(R.id.tvTimeStamp);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        ImageView ivImageUrl = (ImageView) convertView.findViewById(R.id.ivImageUrl);

        // 4. Populate data into the subviews
        tvScreenName.setText(tweet.getUser().getName().toString());
        tvUserName.setText("@" + tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody().toString());
        tvTimeStamp.setText(tweet.getRelativeTimeAgo(tweet.getCreatedAt()));
        ivProfileImage.setImageResource(android.R.color.transparent); // Clear out the old image for a recycled view and put a transparent placeholder
        Picasso.with(getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .into(ivProfileImage);        // Send an API request using Picasso library, load the imageURL, retrieve the data, and insert it into the imageView

        //ivImageUrl.getLayoutParams().height = getScaledHeight(getContext());

        if(!TextUtils.isEmpty(tweet.getMediaImageUrl())){
            ivImageUrl.setImageResource(android.R.color.transparent); // Clear out the old image for a recycled view and put a transparent placeholder
            Picasso.with(getContext())
                    .load(tweet.getMediaImageUrl())
                    .resize(600, 300) // resizes the image to these dimensions (in pixel)
                    .centerCrop()
                    .into(ivImageUrl);        // Send an API request using Picasso library, load the imageURL, retrieve the data, and insert it into the imageView
        }
        else{
            ivImageUrl.setVisibility(View.GONE);
        }

        // 5. Return the view to be inserted into the list
        return convertView;
    }
}
