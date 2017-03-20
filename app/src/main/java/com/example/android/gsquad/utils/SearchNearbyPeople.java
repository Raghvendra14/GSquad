package com.example.android.gsquad.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.gsquad.adapter.FindFriendsAdapter;
import com.example.android.gsquad.model.Coordinates;
import com.example.android.gsquad.model.UserBasicInfo;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Raghvendra on 19-03-2017.
 */

public class SearchNearbyPeople {
    private List<String> mUserIds;
    private String mCurrentUserId;
    private LatLng mCurrentUserLatLng;
    private List<LatLng> mLatLngsList;
    private FindFriendsAdapter mFindFriendsAdapter;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private ProgressBar mProgressBar;

    private DatabaseReference mUserBasicDataReference;

    public SearchNearbyPeople(List<String> UserIds, String currentUserId, FindFriendsAdapter adapter,
                              RecyclerView recyclerView, Context context, ProgressBar progressBar) {
        this.mUserIds = UserIds;
        this.mCurrentUserId = currentUserId;
        this.mLatLngsList = new ArrayList<>();
        this.mFindFriendsAdapter = adapter;
        this.mRecyclerView = recyclerView;
        this.mContext = context;
        this.mProgressBar = progressBar;
    }

    public void search() {
        final List<String> nearbyUserIds = new ArrayList<>();
        final Map<String, Double> nearbyUserDistance = new HashMap<>();
        mUserBasicDataReference = FirebaseDatabase.getInstance().getReference().child("users");
        mUserBasicDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserBasicInfo userInfo = snapshot.getValue(UserBasicInfo.class);
                    if (mUserIds.contains(userInfo.getId()) || userInfo.getId().equals(mCurrentUserId)) {
                        Coordinates coordinates = userInfo.getCoordinates();
                        if (userInfo.getId().equals(mCurrentUserId)) {
                            mCurrentUserLatLng = new LatLng(coordinates.getLat(), coordinates.getLng());
                        } else {
                            LatLng latLng = new LatLng(coordinates.getLat(), coordinates.getLng());
                            mLatLngsList.add(latLng);
                        }
                    }
                }
                if (mCurrentUserLatLng != null && !mLatLngsList.isEmpty()) {
                    for (LatLng userLatLng : mLatLngsList) {
                        double distanceInMeters = SphericalUtil.computeDistanceBetween(mCurrentUserLatLng,
                                userLatLng);
                        if (Double.compare(distanceInMeters, Constants.RANGE) <= 0) {
                            int nearbyUserIdIndex = mLatLngsList.indexOf(userLatLng);
                            nearbyUserIds.add(mUserIds.get(nearbyUserIdIndex));
                            nearbyUserDistance.put(mUserIds.get(nearbyUserIdIndex), distanceInMeters);
                        }
                    }
                }
                getNearbyPeopleInfo(nearbyUserIds, nearbyUserDistance);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getNearbyPeopleInfo(final List<String> nearbyUserIds, final Map<String, Double> nearbyUserDistance) {
        final List<UserBasicInfo> userBasicInfoList = new ArrayList<>();
            mUserBasicDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserBasicInfo userBasicInfo = snapshot.getValue(UserBasicInfo.class);
                        if (nearbyUserIds.contains(userBasicInfo.getId())) {
                            userBasicInfoList.add(userBasicInfo);
                        }
                    }
                    mFindFriendsAdapter = new FindFriendsAdapter(userBasicInfoList, nearbyUserDistance, mContext);
                    mRecyclerView.setAdapter(mFindFriendsAdapter);
                    if (mFindFriendsAdapter.getItemCount() != 0) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

}
