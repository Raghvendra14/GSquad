package com.example.android.gsquad.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Raghvendra on 30-03-2017.
 */

public class Data {
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("profilePicUrl")
    @Expose
    private String profilePicUrl;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
