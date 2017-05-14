package com.codepath.apps.twitterclient.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;

/**
 * Created by John on 5/12/2017.
 */

class ViewHolderWithImage extends RecyclerView.ViewHolder{

    ImageView ivProfileImage;
    TextView tvScreenName;
    TextView tvUserName;
    TextView tvTimeStamp;
    TextView tvBody;
    ImageView ivImageUrl;

    public ViewHolderWithImage(View v) {
        super(v) ;

        // Set item views based on your views and data model
        ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
        tvScreenName = (TextView) v.findViewById(R.id.tvScreenName);
        tvUserName = (TextView) v.findViewById(R.id.tvUserName);
        tvTimeStamp = (TextView) v.findViewById(R.id.tvTimeStamp);
        tvBody = (TextView) v.findViewById(R.id.tvBody);
        ivImageUrl = (ImageView) v.findViewById(R.id.ivImageUrl);
    }

    public ImageView getIvProfileImage() {
        return ivProfileImage;
    }

    public void setIvProfileImage(ImageView ivProfileImage) {
        this.ivProfileImage = ivProfileImage;
    }

    public TextView getTvScreenName() {
        return tvScreenName;
    }

    public void setTvScreenName(TextView tvScreenName) {
        this.tvScreenName = tvScreenName;
    }

    public TextView getTvUserName() {
        return tvUserName;
    }

    public void setTvUserName(TextView tvUserName) {
        this.tvUserName = tvUserName;
    }

    public TextView getTvTimeStamp() {
        return tvTimeStamp;
    }

    public void setTvTimeStamp(TextView tvTimeStamp) {
        this.tvTimeStamp = tvTimeStamp;
    }

    public TextView getTvBody() {
        return tvBody;
    }

    public void setTvBody(TextView tvBody) {
        this.tvBody = tvBody;
    }

    public ImageView getIvImageUrl() {
        return ivImageUrl;
    }

    public void setIvImageUrl(ImageView ivImageUrl) {
        this.ivImageUrl = ivImageUrl;
    }
}
