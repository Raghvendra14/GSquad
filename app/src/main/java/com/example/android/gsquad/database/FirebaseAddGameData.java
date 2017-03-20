package com.example.android.gsquad.database;

import android.util.Log;

import com.example.android.gsquad.model.GameDetails;
import com.example.android.gsquad.model.UserBasicInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Raghvendra on 18-03-2017.
 */

public class FirebaseAddGameData {
    private GameDetails mGameDetails;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mGameDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mCountDataReference;
    private FirebaseUser mFirebaseUser;
    private long mCount = 0;

    public FirebaseAddGameData(GameDetails gameDetails) {
        this.mGameDetails = gameDetails;
        this.mFirebaseDatabase = FirebaseDatabase.getInstance();
        this.mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void addGameData() {
        mGameDatabaseReference = mFirebaseDatabase.getReference().child("games");
        mGameDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                boolean isGameAlreadyAdded = false;
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    GameDetails gameDetail = dataSnapshotChild.getValue(GameDetails.class);
                    if (gameDetail.getId() == mGameDetails.getId()) {
                        isGameAlreadyAdded = true;
                        break;
                    }
                }
                if (!isGameAlreadyAdded) {
                    mGameDetails.setUsers(null);
                    mGameDatabaseReference.child(String.valueOf(mGameDetails.getId())).setValue(mGameDetails);
                }
                addUserDataInGame();
                addGameDataInUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void addGameDataInUser() {
        Log.d("From outside : ", String.valueOf(mGameDetails.getId()));
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users")
            .child(mFirebaseUser.getUid());
        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserBasicInfo userBasicInfo = dataSnapshot.getValue(UserBasicInfo.class);
                List<Integer> gameIdList = new ArrayList<Integer>();
                Log.d("From inside : ", String.valueOf(mGameDetails.getId()));
                if (userBasicInfo.getGamesOwned() == null) {
                    gameIdList = new ArrayList<Integer>();
                    gameIdList.add(mGameDetails.getId());
                } else {
                    gameIdList = userBasicInfo.getGamesOwned();
                    if (!gameIdList.contains(mGameDetails.getId())) {
                        gameIdList.add(mGameDetails.getId());
                    }
                }

                mUserDatabaseReference.child("gamesOwned").setValue(gameIdList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addUserDataInGame() {
        mCountDataReference = mFirebaseDatabase.getReference().child("games");
        mCountDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                boolean isNoGamersAvailable = false;
                GameDetails gamerAvailabilityDetail = null;
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    GameDetails gameDetail = dataSnapshotChild.getValue(GameDetails.class);
                    if (gameDetail.getId() == mGameDetails.getId()) {
                        if (gameDetail.getUsers() == null) {
                            isNoGamersAvailable = true;
                        } else {
                            gamerAvailabilityDetail = gameDetail;
                        }
                        break;
                    }
                }
                List<String> userIdList = new ArrayList<String>();
                if (isNoGamersAvailable) {
                    userIdList = new ArrayList<String>();
                    userIdList.add(mFirebaseUser.getUid());
                } else if (gamerAvailabilityDetail != null) {
                    userIdList = gamerAvailabilityDetail.getUsers();
                    if (!userIdList.contains(mFirebaseUser.getUid())) {
                        userIdList.add(mFirebaseUser.getUid());
                    }
                }

                mCountDataReference.child(String.valueOf(mGameDetails.getId())).child("users")
                        .setValue(userIdList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
