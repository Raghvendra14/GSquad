package com.example.android.gsquad.database;

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

    private void addGameDataInUser() {
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users")
            .child(mFirebaseUser.getUid());
        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserBasicInfo userBasicInfo = dataSnapshot.getValue(UserBasicInfo.class);
                List<Integer> gameIdList;
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

    private void addUserDataInGame() {
        mCountDataReference = mFirebaseDatabase.getReference().child("games")
                .child(String.valueOf(mGameDetails.getId())).child("users");
        mCountDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isUserAlreadyAddedInGameList = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String gameUser = (String) snapshot.getValue();
                    if (gameUser.equals(mFirebaseUser.getUid())) {
                        isUserAlreadyAddedInGameList = true;
                        break;
                    }
                }
                if (!isUserAlreadyAddedInGameList) {
                    mCountDataReference.push().setValue(mFirebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
