package com.example.android.gsquad.utils;

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
import java.util.List;

/**
 * Created by Raghvendra on 19-03-2017.
 */

public class SearchNearbyPeople {
    private List<String> mRestUserIds;
    private String mCurrentUserId;
    private LatLng mCurrentUserLatLng;
    private List<LatLng> mLatLngsList;

    private DatabaseReference mUserLatLngReference;
    private DatabaseReference mUserBasicDataReference;

    public SearchNearbyPeople(List<String> restUserIds, String currentUserId) {
        this.mRestUserIds = restUserIds;
        this.mCurrentUserId = currentUserId;
    }

    public List<UserBasicInfo> search() {
        List<String> nearbyUserIds = new ArrayList<>();

        findCurrentUserCoordinates();
        findUsersCoordinates();
        if (mCurrentUserLatLng != null && !mLatLngsList.isEmpty()) {
            for (LatLng userLatLng : mLatLngsList) {
                double distanceInMeters = SphericalUtil.computeDistanceBetween(mCurrentUserLatLng, userLatLng);
                if (Double.compare(distanceInMeters, Constants.RANGE) <= 0) {
                    int nearbyUserIndex = mLatLngsList.indexOf(userLatLng);
                    nearbyUserIds.add(mRestUserIds.get(nearbyUserIndex));
                }
            }
        }
       return getNearbyPeopleInfo(nearbyUserIds);
    }

    private void findCurrentUserCoordinates() {
        mUserLatLngReference = buildDatabaseReference(mCurrentUserId, "coordinates");
        mUserLatLngReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Coordinates coordinates = dataSnapshot.getValue(Coordinates.class);
                mCurrentUserLatLng = new LatLng(coordinates.getLat(), coordinates.getLng());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void findUsersCoordinates() {
        mLatLngsList = new ArrayList<>();
        for(String user : mRestUserIds) {
            mUserLatLngReference = buildDatabaseReference(user, "coordinates");
            mUserLatLngReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Coordinates coordinates = dataSnapshot.getValue(Coordinates.class);
                    LatLng latLng = new LatLng(coordinates.getLat(), coordinates.getLng());
                    mLatLngsList.add(latLng);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private DatabaseReference buildDatabaseReference(String userId, String child) {
        return FirebaseDatabase.getInstance().getReference().child("users").child(userId)
                .child(child);
    }

    private List<UserBasicInfo> getNearbyPeopleInfo(List<String> nearbyUserIds) {
        List<UserBasicInfo> userBasicInfoList = new ArrayList<>();
        for (String userId : nearbyUserIds) {
            final UserBasicInfo userBasicInfo = new UserBasicInfo();
            mUserBasicDataReference = buildDatabaseReference(userId, "name");
            mUserBasicDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userBasicInfo.setName((String) dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mUserBasicDataReference = buildDatabaseReference(userId, "photoUrl");
            mUserBasicDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userBasicInfo.setPhotoUrl((String) dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            userBasicInfoList.add(userBasicInfo);
        }
        if (!userBasicInfoList.isEmpty()) {
            return userBasicInfoList;
        }
        return null;
    }

}
