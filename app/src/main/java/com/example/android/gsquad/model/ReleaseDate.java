package com.example.android.gsquad.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Raghvendra on 18-03-2017.
 */

public class ReleaseDate {

    @SerializedName("human")
    @Expose
    private String human;


    public String getHuman() {
        return human;
    }

    public void setHuman(String human) {
        this.human = human;
    }



}
