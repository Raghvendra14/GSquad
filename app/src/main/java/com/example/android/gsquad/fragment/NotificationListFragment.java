package com.example.android.gsquad.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.gsquad.R;
import com.example.android.gsquad.adapter.NotificationListAdapter;
import com.example.android.gsquad.model.Notifications;
import com.example.android.gsquad.model.UserBasicInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raghvendra on 22-03-2017.
 */

public class NotificationListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private NotificationListAdapter mNotificationListAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;
    private List<UserBasicInfo> mUserBasicInfoList;
    private List<Boolean> mIsCurrentUserSender;
    private String mFirebaseUserId;

    private DatabaseReference mNotificationDataReference;
    private DatabaseReference mUserDataReference;
    private ValueEventListener mFirebaseValueEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public NotificationListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_textview_notification);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.notification_list_progressBar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.notification_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    mFirebaseUserId = mFirebaseUser.getUid();
                    mNotificationDataReference = FirebaseDatabase.getInstance().getReference().child("users")
                            .child(mFirebaseUserId).child("notifications");
                    attachDatabaseReadListener();
                } else {
                    mFirebaseUserId = null;
                    detachDatabaseReadListener();
                }
            }
        };

        // add empty list into adapter
        mUserBasicInfoList = new ArrayList<>();
        mIsCurrentUserSender = new ArrayList<>();
        mNotificationListAdapter = new NotificationListAdapter(mUserBasicInfoList, mIsCurrentUserSender,
                getActivity());
        mRecyclerView.setAdapter(mNotificationListAdapter);

        mProgressBar.setVisibility(View.VISIBLE);
        return rootView;
    }

    @Override
    public void onPause() {
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void attachDatabaseReadListener() {
        if (mFirebaseValueEventListener == null) {
            mFirebaseValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        mEmptyTextView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                } else {
                        mUserBasicInfoList.clear();
                        mIsCurrentUserSender.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Notifications notifications = snapshot.getValue(Notifications.class);
                            String requiredUserId;
                            boolean isCurrentUserSender;
                            if (notifications.getFrom().equals(mFirebaseUserId)) {
                                requiredUserId = notifications.getTo();
                                isCurrentUserSender = true;
                            } else {
                                requiredUserId = notifications.getFrom();
                                isCurrentUserSender = false;
                            }
                            // Add the boolean to check whether current user is the sender or not.
                            mIsCurrentUserSender.add(isCurrentUserSender);
                            mUserDataReference = FirebaseDatabase.getInstance().getReference().child("users")
                                    .child(requiredUserId);
                            mUserDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    UserBasicInfo userInfo = dataSnapshot.getValue(UserBasicInfo.class);
                                    mUserBasicInfoList.add(userInfo);

                                    mNotificationListAdapter = new NotificationListAdapter(mUserBasicInfoList,
                                            mIsCurrentUserSender, getActivity());
                                    mRecyclerView.setAdapter(mNotificationListAdapter);
                                    if (mNotificationListAdapter.getItemCount() != 0) {
                                        mEmptyTextView.setVisibility(View.GONE);
                                        mProgressBar.setVisibility(View.GONE);
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
            };
            mNotificationDataReference.addValueEventListener(mFirebaseValueEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mFirebaseValueEventListener != null) {
            mNotificationDataReference.removeEventListener(mFirebaseValueEventListener);
            mFirebaseValueEventListener = null;
        }
        List<UserBasicInfo> emptyList = new ArrayList<>();
        List<Boolean> emptyBooleanList = new ArrayList<>();
        mNotificationListAdapter = new NotificationListAdapter(emptyList, emptyBooleanList, getActivity());
        mRecyclerView.setAdapter(mNotificationListAdapter);
        mNotificationListAdapter.notifyDataSetChanged();
    }
}
