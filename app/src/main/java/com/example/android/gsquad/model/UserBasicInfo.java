package com.example.android.gsquad.model;

import java.util.List;

/**
 * Created by Raghvendra on 20-03-2017.
 */

public class UserBasicInfo {

    private String id;
    private Coordinates coordinates;
    private String email;
    private List<Integer> gamesOwned;
    private String name;
    private String photoUrl;

    public UserBasicInfo () {
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public Coordinates getCoordinates() { return coordinates; }

    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public List<Integer> getGamesOwned() { return gamesOwned; }

    public void setGamesOwned(List<Integer> gamesOwned) { this.gamesOwned = gamesOwned; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPhotoUrl() { return photoUrl; }

    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

}
