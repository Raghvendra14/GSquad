package com.example.android.gsquad.database;

import com.example.android.gsquad.model.Notifications;
import com.example.android.gsquad.model.UserBasicInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Raghvendra on 23-03-2017.
 */

public class ResponseToInvite {
    private UserBasicInfo mUserBasicInfo;
    private boolean mIsInviteAccepted;
    private String receiverKey;

    private DatabaseReference mCurrentUserNotificationDataReference;
    private DatabaseReference mUserNotificationDataReference;
    private DatabaseReference mUserFriendListReference;

    public ResponseToInvite(UserBasicInfo userBasicInfo, boolean isInviteAccepted) {
        this.mUserBasicInfo = userBasicInfo;
        this.mIsInviteAccepted = isInviteAccepted;
        this.mCurrentUserNotificationDataReference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notifications");
    }

    public void response() {
        mCurrentUserNotificationDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    receiverKey = null;
                    final String senderId = mUserBasicInfo.getId();
                    Notifications notification = null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        notification = snapshot.getValue(Notifications.class);
                        if (notification.getFrom().equals(senderId) && notification.getTo()
                                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            receiverKey = snapshot.getKey();
                            break;
                        }
                    }
                    if (notification != null && receiverKey != null) {
                        mUserFriendListReference = FirebaseDatabase.getInstance().getReference().child("users");
                        if (mIsInviteAccepted) {
                            Map<String, Object> updateValues = new HashMap<String, Object>();
                            updateValues.put("/" + notification.getFrom() + "/friends/" + notification.getTo(), true);
                            updateValues.put("/" + notification.getTo() + "/friends/" + notification.getFrom(), true);
                            mUserFriendListReference.updateChildren(updateValues);
                        }
                        mUserNotificationDataReference = mUserFriendListReference.child(senderId).child("notifications");
                        mUserNotificationDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Notifications notifications = snapshot.getValue(Notifications.class);
                                    if (notifications.getFrom().equals(senderId)) {
                                        String senderKey = snapshot.getKey();
                                        mUserNotificationDataReference.child(senderKey).removeValue();
                                        mCurrentUserNotificationDataReference.child(receiverKey).removeValue();
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
