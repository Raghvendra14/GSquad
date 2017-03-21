package com.example.android.gsquad.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.gsquad.R;
import com.example.android.gsquad.activity.AddGameActivity;
import com.example.android.gsquad.adapter.GameDetailsListAdapter;
import com.example.android.gsquad.model.Coordinates;
import com.example.android.gsquad.model.GameDetails;
import com.example.android.gsquad.model.UserBasicInfo;
import com.example.android.gsquad.utils.Constants;
import com.example.android.gsquad.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static final String TAG = MainActivityFragment.class.getSimpleName();
    public static final String ANONYMOUS = "anonymous";

    public static final int RC_PERMISSION = 2;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mUserDatabaseReference;
    private ValueEventListener mFirebaseValueEventListener;
    private DatabaseReference mUserGamesReference;
    private DatabaseReference mGameDataReference;

    /*
    *  All the location related module level variable declared here
    */
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
//    protected Location mLastLocation;

    private RecyclerView mRecyclerView;
    private GameDetailsListAdapter mGameDetailsListAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;
    private List<GameDetails> gameDetailsList;
    private UserBasicInfo mUserBasicInfo;

    private String mUsername;
    private String mUserEmailId;
    private double mUserLastLongitude;
    private double mUserLastLatitude;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Initiate references to views
        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_textview);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.game_detail_progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.games_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        // Add empty array list into adapter
        gameDetailsList = new ArrayList<>();
        mGameDetailsListAdapter = new GameDetailsListAdapter(gameDetailsList, getActivity(), true);
        mRecyclerView.setAdapter(mGameDetailsListAdapter);


        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkAvailable(getActivity())) {
                    startActivity(new Intent(getActivity(), AddGameActivity.class));
                } else {
                    Snackbar.make(rootView, getString(R.string.no_connection_available),
                            Snackbar.LENGTH_LONG).show();
                }
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
        mUserGamesReference = mUserDatabaseReference;

        if (mFirebaseUser.getDisplayName() != null && mFirebaseUser.getEmail() != null) {
            mUserBasicInfo = new UserBasicInfo();
            mUsername = mFirebaseUser.getDisplayName();
            mUserEmailId = mFirebaseUser.getEmail();

            mUserBasicInfo.setId(mFirebaseUser.getUid());
            mUserBasicInfo.setName(mUsername);
            mUserBasicInfo.setEmail(mUserEmailId);
            mUserBasicInfo.setPhotoUrl(Utils.getProfilePicUrl(mFirebaseUser, getActivity()));
            mUserBasicInfo.setCoordinates(null);
            mUserBasicInfo.setGamesOwned(null);
            storeUserBasicDataInDatabase();
        }
        attachDatabaseReadListener();

    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        mUserEmailId = null;
        mUserLastLongitude = 0;
        mUserLastLatitude = 0;
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mFirebaseValueEventListener == null) {
            mFirebaseValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    gameDetailsList.clear();

                    UserBasicInfo userInfo = dataSnapshot.getValue(UserBasicInfo.class);
                    if (userInfo != null) {
                        if (userInfo.getGamesOwned() == null) {
                            mEmptyTextView.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                        } else {
                            List<Integer> gamesOwnedList = userInfo.getGamesOwned();
                            for (int gameId : gamesOwnedList) {
                                mGameDataReference = FirebaseDatabase.getInstance().getReference()
                                        .child("games").child(String.valueOf(gameId));
                                mGameDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        GameDetails gameDetails = dataSnapshot.getValue(GameDetails.class);
                                        Log.d(TAG, gameDetails.getName());
                                        gameDetailsList.add(gameDetails);
                                        mGameDetailsListAdapter = new GameDetailsListAdapter(gameDetailsList, getActivity(), true);
                                        mRecyclerView.setAdapter(mGameDetailsListAdapter);
                                        Log.d(TAG, gameDetailsList.toString());
                                        if (mGameDetailsListAdapter.getItemCount() != 0) {
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
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mUserGamesReference.addValueEventListener(mFirebaseValueEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mFirebaseValueEventListener != null) {
            mUserGamesReference.removeEventListener(mFirebaseValueEventListener);
            mFirebaseValueEventListener = null;
        }
        List<GameDetails> emptyList = new ArrayList<>();
        mGameDetailsListAdapter = new GameDetailsListAdapter(emptyList, getActivity(), true);
        mRecyclerView.setAdapter(mGameDetailsListAdapter);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "In onConnected");
        requestForRequiredPermissions();

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
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(Constants.LOCATION_REQUEST_INTERVAL);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mUserBasicInfo != null) {
            mUserLastLatitude = location.getLatitude();
            mUserLastLongitude = location.getLongitude();
            Coordinates coordinates = new Coordinates(mUserLastLatitude, mUserLastLongitude);
            storeUserCoordinates(coordinates);
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
                Toast.makeText(getActivity(), getString(R.string.device_unsupported), Toast.LENGTH_LONG)
                        .show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    private void storeUserBasicDataInDatabase() {
        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mUserDatabaseReference.setValue(mUserBasicInfo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void storeUserCoordinates(final Coordinates coordinates) {
        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mUserDatabaseReference.child("coordinates").setValue(coordinates);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
