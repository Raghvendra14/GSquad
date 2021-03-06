package com.example.android.gsquad.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.gsquad.R;
import com.example.android.gsquad.adapter.FindFriendsAdapter;
import com.example.android.gsquad.model.Coordinates;
import com.example.android.gsquad.model.Notifications;
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
    private Map<String, LatLng> mLatLngsList;
    private FindFriendsAdapter mFindFriendsAdapter;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;

    private DatabaseReference mUserBasicDataReference;
    private DatabaseReference mUserFriendListDataReference;
    private DatabaseReference mUserNotificationsDataReference;

    public SearchNearbyPeople(List<String> UserIds, String currentUserId, FindFriendsAdapter adapter,
                              RecyclerView recyclerView, Context context, ProgressBar progressBar,
                              TextView emptyTextView) {
        this.mUserIds = UserIds;
        this.mCurrentUserId = currentUserId;
        this.mLatLngsList = new HashMap<>();
        this.mFindFriendsAdapter = adapter;
        this.mRecyclerView = recyclerView;
        this.mContext = context;
        this.mProgressBar = progressBar;
        this.mEmptyTextView = emptyTextView;
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
                            mLatLngsList.put(userInfo.getId(), latLng);
                        }
                    }
                }
                if (mCurrentUserLatLng != null && !mLatLngsList.isEmpty()) {
                    String range = Utils.getPreferredRange(mContext);
                    Double rangeInDouble;
                    if (range.equals(mContext.getResources().getString(R.string.pref_range_ten_kilometers)) ||
                            range.equals(mContext.getResources().getString(R.string.pref_range_fifty_kilometers)) ||
                            range.equals(mContext.getResources().getString(R.string.pref_range_hundred_kilometers))) {
                        rangeInDouble = Double.valueOf(range);
                    } else {
                        rangeInDouble = Constants.RANGE;
                    }
                    for (int i = 0; i < mLatLngsList.size(); i++) {
                        double distanceInMeters = SphericalUtil.computeDistanceBetween(mCurrentUserLatLng,
                                mLatLngsList.get(mUserIds.get(i)));
                        if (Double.compare(distanceInMeters, rangeInDouble) <= 0) {
                            nearbyUserIds.add(mUserIds.get(i));
                            nearbyUserDistance.put(mUserIds.get(i), distanceInMeters);
                        }
                    }
                }

                if (nearbyUserIds.isEmpty()) {
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyTextView.setVisibility(View.VISIBLE);
                } else {
                    filterAlreadySentRequest(nearbyUserIds, nearbyUserDistance);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getNearbyPeopleInfo(final List<String> nearbyUserIds, final Map<String, Double> nearbyUserDistance) {
        final List<UserBasicInfo> userBasicInfoList = new ArrayList<>();
        final List<Boolean> userShowDistance = new ArrayList<>();
            mUserBasicDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean showLocation;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserBasicInfo userBasicInfo = snapshot.getValue(UserBasicInfo.class);
                        if (nearbyUserIds.contains(userBasicInfo.getId())) {
                            userBasicInfoList.add(userBasicInfo);
                            showLocation = userBasicInfo.getShowLocation();
                            userShowDistance.add(showLocation);
                        }
                    }
                    mFindFriendsAdapter = new FindFriendsAdapter(userBasicInfoList, nearbyUserDistance, userShowDistance, mContext);
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

    private void filterFriendsAlreadyAvailable(final List<String> nearbyUserIds, final Map<String, Double> nearbyUserDistance) {
        mUserFriendListDataReference = mUserBasicDataReference.child(mCurrentUserId).child("friends");
        mUserFriendListDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userFriendInList = snapshot.getKey();
                    if (nearbyUserIds.contains(userFriendInList)) {
                        nearbyUserIds.remove(userFriendInList);
                        nearbyUserDistance.remove(userFriendInList);
                    }
                }
                if (nearbyUserIds.isEmpty()) {
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyTextView.setVisibility(View.VISIBLE);
                } else {
                    getNearbyPeopleInfo(nearbyUserIds, nearbyUserDistance);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void filterAlreadySentRequest(final List<String> nearbyUserIds, final Map<String, Double> nearbyUserDistance) {
        mUserNotificationsDataReference = mUserBasicDataReference.child(mCurrentUserId).child("notifications");
        mUserNotificationsDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notifications notification = snapshot.getValue(Notifications.class);
                    if (nearbyUserIds.contains(notification.getTo())) {
                        nearbyUserIds.remove(notification.getTo());
                        nearbyUserDistance.remove(notification.getTo());
                    } else if (nearbyUserIds.contains(notification.getFrom())) {
                        nearbyUserIds.remove(notification.getFrom());
                        nearbyUserDistance.remove(notification.getFrom());
                    }
                }
                if (nearbyUserIds.isEmpty()) {
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyTextView.setVisibility(View.VISIBLE);
                } else {
                    filterFriendsAlreadyAvailable(nearbyUserIds, nearbyUserDistance);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
