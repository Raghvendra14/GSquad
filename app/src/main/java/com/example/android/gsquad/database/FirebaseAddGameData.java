package com.example.android.gsquad.database;

import com.example.android.gsquad.model.GameDetails;
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
                addGameDataInUser();
                addUserDataInGame();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void addGameDataInUser() {
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users").child(mFirebaseUser.getUid())
                .child("games_owned").child(String.valueOf(mGameDetails.getId()));
        mUserDatabaseReference.setValue(true);
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
                if (isNoGamersAvailable) {
                    List<String> userIdList = new ArrayList<String>();
                    userIdList.add(mFirebaseUser.getUid());
                    mCountDataReference.child(String.valueOf(mGameDetails.getId())).child("users")
                            .setValue(userIdList);
                } else if (gamerAvailabilityDetail != null) {
                    List<String> userIdList = gamerAvailabilityDetail.getUsers();
                    userIdList.add(mFirebaseUser.getUid());
                    mCountDataReference.child(String.valueOf(mGameDetails.getId())).child("users")
                            .setValue(userIdList);
                }

//                if (!dataSnapshot.exists()) {
//                    mCount = 0;
//                } else {
//                    mCount = dataSnapshot.getChildrenCount();
//                }
//                mCount = mCount + 1;
//                mGameDatabaseReference = mGameDatabaseReference.child("users");
//                Map<String, Object> data = new HashMap<String, Object>();
//                data.put(String.valueOf(mCount), mFirebaseUser.getUid());
//                mGameDatabaseReference.updateChildren(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
