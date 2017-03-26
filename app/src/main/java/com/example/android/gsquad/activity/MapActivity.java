package com.example.android.gsquad.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.android.gsquad.R;
import com.example.android.gsquad.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String TAG = MapActivity.class.getSimpleName();

    private GoogleMap mMap;
    private LatLng mCurrentLatLng;
    private String mCurrentUserName;
    private LatLng mUserLatLng;
    private String mUserName;
    private List<Marker> markers;


    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mCurrentLatLng = bundle.getParcelable(Constants.CURRENT_LATLNG);
        mUserLatLng = bundle.getParcelable(Constants.USER_LATLNG);
        mUserName = bundle.getString(Constants.USER_NAME);
        mCurrentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        markers = new ArrayList<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        markers.add(mMap.addMarker(new MarkerOptions()
                .position(mCurrentLatLng)
                .title(mCurrentUserName)));

        markers.add(mMap.addMarker(new MarkerOptions()
                .position(mUserLatLng)
                .title(mUserName)));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        moveToCurrentLocation(bounds);


    }

    private void moveToCurrentLocation(final LatLngBounds bounds) {
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
            }
        });

    }
}
