package com.example.android.gsquad.database;

import com.example.android.gsquad.model.Notifications;
import com.example.android.gsquad.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        Notifications notification = createFriendList(mUserId, mCurrentUserId);
        mUsersDatabaseReference.child(mCurrentUserId).child("notifications").push().setValue(notification);
        mUsersDatabaseReference.child(mUserId).child("notifications").push().setValue(notification);
    }

    private Notifications createFriendList(String to, String from) {
        Notifications notification = new Notifications();
        notification.setTo(to);
        notification.setFrom(from);
        notification.setStatus(Constants.PENDING);
        return notification;
    }
}
