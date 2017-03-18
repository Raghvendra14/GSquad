package com.example.android.gsquad.database;

import com.example.android.gsquad.model.GameDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Raghvendra on 18-03-2017.
 */

public class FirebaseAddGameData {
    private GameDetails mGameDetails;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mGameDatabaseReference;
    private FirebaseUser mFirebaseUser;

    public FirebaseAddGameData(GameDetails gameDetails) {
        this.mGameDetails = gameDetails;
        this.mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    public FirebaseAddGameData() {
        this.mFirebaseDatabase = FirebaseDatabase.getInstance();
        this.mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void addGameData() {
        mGameDatabaseReference = mFirebaseDatabase.getReference().child("games").child(String.valueOf(mGameDetails.getId()));
        mGameDatabaseReference.setValue(mGameDetails);
    }

    public void addUserData(int gameId) {
        mGameDatabaseReference = mFirebaseDatabase.getReference().child("games").child(String.valueOf(gameId))
                .child("users").child(mFirebaseUser.getUid());
        mGameDatabaseReference.setValue(true);
        mGameDatabaseReference = mFirebaseDatabase.getReference().child("users").child(mFirebaseUser.getUid())
                .child("games_owned").child(String.valueOf(gameId));
        mGameDatabaseReference.setValue(true);
    }
}
