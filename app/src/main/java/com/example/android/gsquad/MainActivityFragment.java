package com.example.android.gsquad;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static final String TAG = MainActivityFragment.class.getSimpleName();
    public static final String ANONYMOUS = "anonymous";

    public static final int RC_PERMISSION = 2;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mUserDatabaseReference;
    private ValueEventListener mFirebaseValueEventListener;

    /*
    *  All the location related module level variable declared here
    */
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    private String mUsername;
    private String mUserEmailId;
    private String mUserLastLongitude;
    private String mUserLastLatitude;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FriendListActivity.class));
            }
        });

        mUsername = ANONYMOUS;

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    // User is signed in
                    onSignedInIntialize();

                } else {
                    // User is signed out
                    onSignedOutCleanup();
                }
            }
        };

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
        return rootView;
    }

    protected void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
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
        checkPlayServices();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    private void onSignedInIntialize() {
        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(mFirebaseUser.getUid());

        if (mFirebaseUser.getDisplayName() != null && mFirebaseUser.getEmail() != null) {
            mUsername = mFirebaseUser.getDisplayName();
            mUserEmailId = mFirebaseUser.getEmail();
            mUserDatabaseReference.child("name").setValue(mUsername);
            mUserDatabaseReference.child("email").setValue(mUserEmailId);
        }
        attachDatabaseReadListener();

    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        mUserEmailId = null;
        mUserLastLongitude = null;
        mUserLastLatitude = null;
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mFirebaseValueEventListener == null) {
            mFirebaseValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String Username = snapshot.getValue().toString();
                        Log.d("Hello", Username);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mUserDatabaseReference.addValueEventListener(mFirebaseValueEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mFirebaseValueEventListener != null) {
            mUserDatabaseReference.removeEventListener(mFirebaseValueEventListener);
            mFirebaseValueEventListener = null;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "In onConnected");
        requestForRequiredPermissions();
        if (mUserDatabaseReference != null && mLastLocation != null) {
            mUserLastLongitude = String.valueOf(mLastLocation.getLongitude());
            mUserLastLatitude = String.valueOf(mLastLocation.getLatitude());
            mUserDatabaseReference.child("long").setValue(mUserLastLongitude);
            mUserDatabaseReference.child("lat").setValue(mUserLastLatitude);
        }
//        if (mLastLocation != null) {
//            Log.d(TAG, "Last Location from onConnected is " + String.valueOf(mLastLocation.getLatitude()) + " and "
//                    + String.valueOf(mLastLocation.getLongitude()));
//        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "In onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "In onConnectionFailed " + connectionResult.getErrorMessage());
    }

    private void requestForRequiredPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    RC_PERMISSION);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                      PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity(), "This device is not supported", Toast.LENGTH_LONG)
                        .show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }
}
