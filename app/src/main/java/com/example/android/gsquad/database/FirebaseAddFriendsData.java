package com.example.android.gsquad.database;

import com.example.android.gsquad.model.Friends;
import com.example.android.gsquad.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Raghvendra on 22-03-2017.
 */

public class FirebaseAddFriendsData {
    private String mUserId;
    private String mCurrentUserId;
    private DatabaseReference mUsersDatabaseReference;

    public FirebaseAddFriendsData(String userId, String currentUserId) {
        this.mUserId = userId;
        this.mCurrentUserId = currentUserId;
        this.mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
    }

    public void add() {
        Friends userFriendList = createFriendList();
        Map<String, Object> userUpdates = new HashMap<String, Object>();
        userUpdates.put(mCurrentUserId + "/friends/" + mUserId, userFriendList);
        userUpdates.put(mUserId + "/friends/" + mCurrentUserId, userFriendList);

        mUsersDatabaseReference.updateChildren(userUpdates);
    }

    private Friends createFriendList() {
        Friends friends = new Friends();
        friends.setStatus(Constants.PENDING);
        return friends;
    }
}
