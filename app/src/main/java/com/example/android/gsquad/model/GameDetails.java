package com.example.android.gsquad.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Raghvendra on 18-03-2017.
 */

public class GameDetails {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("aggregated_rating")
    @Expose
    private float aggregatedRating;
    @SerializedName("publishers")
    @Expose
    private List<Integer> publishers = null;
    @SerializedName("genres")
    @Expose
    private List<Integer> genres = null;
    @SerializedName("release_dates")
    @Expose
    private List<ReleaseDate> releaseDates = null;
    @SerializedName("cover")
    @Expose
    private Cover cover;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public float getAggregatedRating() {
        return aggregatedRating;
    }

    public void setAggregatedRating(float aggregatedRating) {
        this.aggregatedRating = aggregatedRating;
    }

    public List<Integer> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<Integer> publishers) {
        this.publishers = publishers;
    }

    public List<Integer> getGenres() {
        return genres;
    }

    public void setGenres(List<Integer> genres) {
        this.genres = genres;
    }

    public List<ReleaseDate> getReleaseDates() {
        return releaseDates;
    }

    public void setReleaseDates(List<ReleaseDate> releaseDates) {
        this.releaseDates = releaseDates;
    }

    public Cover getCover() {
        return cover;
    }

    public void setCover(Cover cover) {
        this.cover = cover;
    }
}
