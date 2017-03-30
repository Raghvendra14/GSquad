package com.example.android.gsquad.database;

import com.example.android.gsquad.fcm.FcmNotificationBuilder;
import com.example.android.gsquad.model.Notifications;
import com.example.android.gsquad.model.UserBasicInfo;
import com.example.android.gsquad.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Raghvendra on 22-03-2017.
 */

public class FirebaseAddFriendsData {
    private String mUserId;
    private String mCurrentUserId;
    private DatabaseReference mUsersDatabaseReference;
    private String mReceiverFirebaseToken;
    private String mSenderUsername;
    private String mSenderProfilePicUrl;

    public FirebaseAddFriendsData(String userId, String currentUserId) {
        this.mUserId = userId;
        this.mCurrentUserId = currentUserId;
        this.mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
    }

    public void add() {
        Notifications notification = createFriendList(mUserId, mCurrentUserId);
        mUsersDatabaseReference.child(mCurrentUserId).child("notifications").push().setValue(notification);
        mUsersDatabaseReference.child(mUserId).child("notifications").push().setValue(notification);
        sendNotifications();
    }

    private Notifications createFriendList(String to, String from) {
        Notifications notification = new Notifications();
        notification.setTo(to);
        notification.setFrom(from);
        notification.setStatus(Constants.PENDING);
        return notification;
    }

    private void sendNotifications() {
        mUsersDatabaseReference.child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserBasicInfo userBasicInfo = dataSnapshot.getValue(UserBasicInfo.class);
                    mReceiverFirebaseToken = userBasicInfo.getFirebaseToken();
                    getSenderInformationFromFirebase();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getSenderInformationFromFirebase() {
        mUsersDatabaseReference.child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserBasicInfo userBasicInfo = dataSnapshot.getValue(UserBasicInfo.class);
                    mSenderUsername = userBasicInfo.getName();
                    mSenderProfilePicUrl = userBasicInfo.getPhotoUrl();

                    buildNotificationRequest();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void buildNotificationRequest() {
        FcmNotificationBuilder.initialize()
                .username(mSenderUsername)
                .profilePicUrl(mSenderProfilePicUrl)
                .receiverUid(mUserId)
                .receiverFirebaseToken(mReceiverFirebaseToken)
                .send();

    }
}
