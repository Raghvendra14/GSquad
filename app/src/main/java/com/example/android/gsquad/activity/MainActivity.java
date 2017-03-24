package com.example.android.gsquad.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.gsquad.R;
import com.example.android.gsquad.adapter.ViewPagerAdapter;
import com.example.android.gsquad.fragment.FriendListFragment;
import com.example.android.gsquad.fragment.MainActivityFragment;
import com.example.android.gsquad.fragment.NotificationListFragment;
import com.example.android.gsquad.utils.Constants;
import com.example.android.gsquad.utils.Utils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();


    public static final int RC_SIGN_IN = 1;

    /*
    *  All the firebase related module level variable declared here
    */
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mStatusDataReference;
    private String mUserId = "";

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private boolean mIsCalledByAddFriendsActivity;
    private ViewPager mViewPager;
    private Spinner mSpinner;

    private int mSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        final Intent intent = getIntent();
        if (intent.hasExtra(Constants.PARENT_IS_ADD_FRIENDS)) {
            mIsCalledByAddFriendsActivity = intent.getBooleanExtra(Constants.PARENT_IS_ADD_FRIENDS, false);
        }
        // It is meant to setup the Navigational Drawer for home screen of the app.
        setUpNavigationalDrawer();
        setupTabLayoutWithViewPager();

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.example.android.gsquad",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
        mStatusDataReference = FirebaseDatabase.getInstance().getReference().child("status");
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    mUserId = user.getUid();
                    Log.d(TAG, "User Connected");
                    setupProfilePicInNavDrawer(user);
                    mNavigationView.getMenu().getItem(mSize-1).setChecked(false);
                    if (mIsCalledByAddFriendsActivity) {
                        mViewPager.setCurrentItem(1, true);
                        intent.putExtra(Constants.PARENT_IS_ADD_FRIENDS, false);
                    }
                    checkForStatus(user);
                } else {
                    // User is signed out
                    mUserId = "";
                    mIsCalledByAddFriendsActivity = false;
                    startActivityForResult(
                            AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
                }
            }
        };
        mSpinner = (Spinner) findViewById(R.id.status_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, R.layout.spinner_text_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                mViewPager.setCurrentItem(0, true);
                Toast.makeText(this, "You are signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        mDrawer.removeDrawerListener(mToggle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        mDrawer.addDrawerListener(mToggle);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return mToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void setUpNavigationalDrawer() {
        mDrawer  = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mSize = mNavigationView.getMenu().size();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.profile:
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(Constants.USER_ID, mFirebaseAuth.getCurrentUser().getUid());
                intent.putExtra(Constants.CALLING_ACTIVITY, false);
                startActivity(intent);
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.feedback:
                Intent feedbackIntent = new Intent(Intent.ACTION_SEND);
                feedbackIntent.setType("message/rfc822");
                feedbackIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.feedback_email)});
                feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
                try {
                    startActivity(Intent.createChooser(feedbackIntent, getString(R.string.email_chooser)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, getString(R.string.no_clients_available), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case R.id.share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_info));
                startActivity(shareIntent);
                break;
            case R.id.logout:
                AuthUI.getInstance().signOut(this);
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkForStatus(final FirebaseUser user) {
        mStatusDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isAvailable = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(user.getUid())) {
                        String status = (String) snapshot.getValue();
                        int selectedId = 0;
                        switch (status) {
                            case "Online":
                                selectedId = 0;
                                break;
                            case "Away":
                                selectedId = 1;
                                break;
                            case "Offline":
                                selectedId = 2;
                        }
                        mSpinner.setSelection(selectedId);
                        isAvailable = true;
                        break;
                    }
                }
                if (!isAvailable) {
                    mSpinner.setSelection(0);
                    String defValue = "Online";
                    mStatusDataReference.child(mUserId).setValue(defValue);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
    *  Setup Profile pic from the login providers like Facebook, Google, etc. into the Profile Pic
    *  imageview of Navigational Drawer
    */
    private void setupProfilePicInNavDrawer(FirebaseUser user) {
        String photoUrl;
        View headerLayout = mNavigationView.getHeaderView(0);
        CircleImageView profilePic = (CircleImageView) headerLayout.findViewById(R.id.user_pic);
        photoUrl = Utils.getProfilePicUrl(user, this);

        Glide.with(MainActivity.this)
                .load(photoUrl)
                .asBitmap()  // Provides auto refreshing of image after it's downloaded in case of CircularImageView
                .placeholder(android.R.drawable.sym_def_app_icon)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .into(profilePic);
    }

    private void setupTabLayoutWithViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainActivityFragment(), getString(R.string.tab_title_games));
        adapter.addFragment(new FriendListFragment(), getString(R.string.tab_title_friends));
        adapter.addFragment(new NotificationListFragment(), getString(R.string.tab_title_notifications));
        mViewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Map<String, Object> updateStatus = new HashMap<String, Object>();
        updateStatus.put("/" + mUserId, parent.getItemAtPosition(position).toString());
        mStatusDataReference.updateChildren(updateStatus);
        updateWidget();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void updateWidget() {
        Context context = MainActivity.this;
        Intent dataUpdatedIntent = new Intent(Constants.ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }
}
