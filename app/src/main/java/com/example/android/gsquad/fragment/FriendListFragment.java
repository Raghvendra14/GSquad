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
import com.example.android.gsquad.adapter.FriendListAdapter;
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
 * Created by Raghvendra on 16-03-2017.
 */

public class FriendListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FriendListAdapter mFriendListAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;
    private List<UserBasicInfo> mUserBasicInfoList;

    private String mFirebaseId;
    private DatabaseReference mUserDataReference;
    private DatabaseReference mUserFriendDataReference;
    private ValueEventListener mFirebaseValueEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public FriendListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend, container, false);

        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_textview_friend);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.friend_list_progressBar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.friends_recycler_view);
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
                    mFirebaseId = mFirebaseUser.getUid();
                    mUserDataReference = FirebaseDatabase.getInstance().getReference().child("users")
                            .child(mFirebaseId).child("friends");
                    attachDatabaseReadListener();
                } else {
                    mFirebaseId = null;
                    detachDatabaseReadListener();
                }
            }
        };

        // add empty list into adapter
        mUserBasicInfoList = new ArrayList<>();
        mFriendListAdapter = new FriendListAdapter(mUserBasicInfoList, getActivity());
        mRecyclerView.setAdapter(mFriendListAdapter);
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
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String userId = snapshot.getKey();
                            mUserFriendDataReference = FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(userId);
                            mUserFriendDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    UserBasicInfo userBasicInfo = dataSnapshot.getValue(UserBasicInfo.class);
                                    mUserBasicInfoList.add(userBasicInfo);

                                    mFriendListAdapter = new FriendListAdapter(mUserBasicInfoList, getActivity());
                                    mRecyclerView.setAdapter(mFriendListAdapter);
                                    if (mFriendListAdapter.getItemCount() != 0) {
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
            mUserDataReference.addValueEventListener(mFirebaseValueEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mFirebaseValueEventListener != null) {
            mUserDataReference.removeEventListener(mFirebaseValueEventListener);
            mFirebaseValueEventListener = null;
        }
        List<UserBasicInfo> emptyList = new ArrayList<>();
        mFriendListAdapter = new FriendListAdapter(emptyList, getActivity());
        mRecyclerView.setAdapter(mFriendListAdapter);
    }
}
