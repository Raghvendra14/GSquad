package com.example.android.gsquad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.gsquad.R;
import com.example.android.gsquad.adapter.FindFriendsAdapter;
import com.example.android.gsquad.listener.RecyclerClickListener;
import com.example.android.gsquad.listener.RecyclerTouchListener;
import com.example.android.gsquad.model.UserBasicInfo;
import com.example.android.gsquad.utils.Constants;
import com.example.android.gsquad.utils.SearchNearbyPeople;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindFriendsActivity extends AppCompatActivity {
    private int mGameId;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private FindFriendsAdapter mFindFriendsAdapter;
    private Context mContext;
    private ActionMode mActionMode = null;
    private Bundle mSavedInstanceState;
    private List<UserBasicInfo> mUserBasicInfoList;
    private TextView mEmptyTextView;


    private DatabaseReference mGameUserDataReference;
    private DatabaseReference mUserDetailsDataReference;
    private ValueEventListener mFirebaseValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.GAME_ID)) {
            mGameId = intent.getIntExtra(Constants.GAME_ID, 0);
        }
        if (mGameId != 0) {
            mContext = FindFriendsActivity.this;
            mProgressBar = (ProgressBar) findViewById(R.id.nearby_people_progressBar);
            mEmptyTextView = (TextView) findViewById(R.id.find_friend_empty_view);
            setupRecyclerView();
            implementRecyclerViewClickListener();
            mGameUserDataReference = FirebaseDatabase.getInstance().getReference().child("games")
                    .child(String.valueOf(mGameId)).child("users");
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        detachDatabaseReadListener();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadReference();
    }

    private void attachDatabaseReadReference() {
        if (mFirebaseValueEventListener == null) {
            mFirebaseValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mUserBasicInfoList.clear();
                    List<String> userIds = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String gameUser = (String) snapshot.getValue();
                        if (gameUser != null) {
                            userIds.add(gameUser);
                        }
                    }
                    if (mFindFriendsAdapter.getItemCount() == 0 && userIds.isEmpty()) {
                        mEmptyTextView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                    }
                    String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (!userIds.isEmpty() && userIds.contains(user)) {
                        if (userIds.size() != 1) {
                            userIds.remove(user);
                            SearchNearbyPeople searchNearbyPeople = new SearchNearbyPeople(userIds, user,
                                    mFindFriendsAdapter, mRecyclerView, mContext, mProgressBar);
                            searchNearbyPeople.search();
                        } else {
                            mEmptyTextView.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mGameUserDataReference.addValueEventListener(mFirebaseValueEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mFirebaseValueEventListener != null) {
            mGameUserDataReference.removeEventListener(mFirebaseValueEventListener);
            mFirebaseValueEventListener = null;
        }
        List<UserBasicInfo> emptyList = new ArrayList<>();
        mFindFriendsAdapter = new FindFriendsAdapter(emptyList, null, mContext);
        mRecyclerView.setAdapter(mFindFriendsAdapter);
    }

    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.nearby_people_recycler_view);
        int spanCount = mContext.getResources().getInteger(R.integer.column_count);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, spanCount);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        // Add empty array list into adapter
        mUserBasicInfoList = new ArrayList<>();
        mFindFriendsAdapter = new FindFriendsAdapter(mUserBasicInfoList, null, mContext);
        mRecyclerView.setAdapter(mFindFriendsAdapter);

    }

    // Implement item click and long click over recycler view
    private void implementRecyclerViewClickListener() {
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(mContext, mRecyclerView,
                new RecyclerClickListener() {
                    @Override
                    public void onClick(View view, int position) {
//                      onListItemSelect(position);
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        // Select item on long click
//                        onListItemSelect(position);
                    }
                }));
    }




}
