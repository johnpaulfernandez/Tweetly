package com.codepath.apps.twitterclient.models;

import com.codepath.apps.twitterclient.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by John on 4/7/2017.
 */

@Table(database = MyDatabase.class)
public class User extends BaseModel implements Serializable{

    @PrimaryKey
    @Column
    private long uid;
    @Column
    private String name;
    @Column
    private String screenName;
    @Column
    private String profileImageUrl;

    public User() {
    }

    public static User fromJSON (JSONObject jsonObject) {
        User user = new User();

        try {
            user.uid = jsonObject.getLong("id");
            user.name = jsonObject.getString("name");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return user;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }


}
