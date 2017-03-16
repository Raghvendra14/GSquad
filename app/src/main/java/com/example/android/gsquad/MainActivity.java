package com.example.android.gsquad;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();


    public static final int RC_SIGN_IN = 1;

    /*
    *  All the firebase related module level variable declared here
    */
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;

    private int mSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        this.setTitle(R.string.tab_title_games);

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

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "User Connected");
                    setupProfilePicInNavDrawer(user);
                    mNavigationView.getMenu().getItem(mSize-1).setChecked(false);
                    // User is signed in
//                    Toast.makeText(MainActivity.this, "You're now signed in.", Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            AuthUI.getInstance().signOut(this);
            return true;
        }

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

                break;
            case R.id.settings:

                break;
            case R.id.help_feedback:

                break;
            case R.id.about_us:

                break;
            case R.id.logout:
                //ToDo : Add offline logout support
                AuthUI.getInstance().signOut(this);
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    *  Setup Profile pic from the login providers like Facebook, Google, etc. into the Profile Pic
    *  imageview of Navigational Drawer
    */
    private void setupProfilePicInNavDrawer(FirebaseUser user) {
        String facebookUserId = "";
        String photoUrl = "";
        View headerLayout = mNavigationView.getHeaderView(0);
        CircleImageView profilePic = (CircleImageView) headerLayout.findViewById(R.id.user_pic);
        for (UserInfo profile : user.getProviderData()) {
            if (profile.getProviderId().equals(getString(R.string.facebook_provider_id))) {
                facebookUserId = profile.getUid();
                photoUrl = "https://graph.facebook.com/" + facebookUserId +
                        "/picture?height=500&width=500";
            } else if (profile.getProviderId().equals(getString(R.string.google_provider_id))) {
                photoUrl = profile.getPhotoUrl().toString();
            }
        }

        Glide.with(MainActivity.this)
                .load(photoUrl)
                .asBitmap()  // Provides auto refreshing of image after it's downloaded in case of CircularImageView
                .placeholder(android.R.drawable.sym_def_app_icon)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .into(profilePic);
    }

    private void setupTabLayoutWithViewPager() {
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_title_games));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_title_friends));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        setupViewPager(tabLayout);
    }

    private void setupViewPager(TabLayout tabLayout) {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                setTabTitle(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setTabTitle(int position) {
        switch (position) {
            case 0:
                setTitle(R.string.tab_title_games);
                break;
            case 1:
                setTitle(R.string.tab_title_friends);
                break;
            default:
                setTitle(R.string.app_name);
                break;
        }
    }
}
