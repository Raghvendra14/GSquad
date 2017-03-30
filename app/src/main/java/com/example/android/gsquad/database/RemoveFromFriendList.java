package com.example.android.gsquad.database;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.android.gsquad.activity.MainActivity;
import com.example.android.gsquad.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Raghvendra on 30-03-2017.
 */

public class RemoveFromFriendList {
    private DatabaseReference mUserDataReference;
    private String mCurrentUserId;

    public RemoveFromFriendList() {
        mUserDataReference = FirebaseDatabase.getInstance().getReference().child("users");
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void remove(final String userId) {
        mUserDataReference.child(userId).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(mCurrentUserId)) {
                        mUserDataReference.child(userId).child("friends").child(mCurrentUserId)
                                .removeValue();
                        removeFromCurrentUserList(userId);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void removeFromCurrentUserList(final String userId) {
        mUserDataReference.child(mCurrentUserId).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(userId)) {
                        mUserDataReference.child(mCurrentUserId).child("friends").child(userId)
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent restartIntent = new Intent(getApplicationContext(), MainActivity.class);
                                restartIntent.putExtra(Constants.PARENT_IS_ADD_FRIENDS, true);
                                restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                getApplicationContext().startActivity(restartIntent);
                            }
                        });
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
