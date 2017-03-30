package com.example.android.gsquad.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Raghvendra on 30-03-2017.
 */

public class Notification {

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("to")
    @Expose
    private String to;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
