package com.example.android.gsquad.database;

import com.example.android.gsquad.model.GameDetails;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Raghvendra on 18-03-2017.
 */

public class FirebaseAddGameData {
    private GameDetails mGameDetails;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mGameDatabaseReference;

    public FirebaseAddGameData(GameDetails gameDetails) {
        this.mGameDetails = gameDetails;
        this.mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    public void addGameData() {
        mGameDatabaseReference = mFirebaseDatabase.getReference().child("games").child(String.valueOf(mGameDetails.getId()));
        mGameDatabaseReference.setValue(mGameDetails);
    }
}
