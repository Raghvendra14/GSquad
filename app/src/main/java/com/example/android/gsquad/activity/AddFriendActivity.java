package com.example.android.gsquad.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.gsquad.R;
import com.example.android.gsquad.adapter.SingleGameSelectAdapter;
import com.example.android.gsquad.model.Game;
import com.example.android.gsquad.model.GameDetails;
import com.example.android.gsquad.model.UserBasicInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddFriendActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private SingleGameSelectAdapter mSingleGameSelectAdapter;
    private Context context;
    private List<Game> mGameList;
    private TextView mEmptyTextView;

    private DatabaseReference mUserGamesReference;
    private DatabaseReference mGameDataReference;
    private ValueEventListener mFirebaseValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        context = AddFriendActivity.this;
        mProgressBar = (ProgressBar) findViewById(R.id.game_list_progressBar);
        mEmptyTextView = (TextView) findViewById(R.id.add_friend_empty_view);
        setupRecyclerView();
        mUserGamesReference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        // Add empty array list into adapter
        mGameList = new ArrayList<>();
        mSingleGameSelectAdapter = new SingleGameSelectAdapter(mGameList, context);
        mRecyclerView.setAdapter(mSingleGameSelectAdapter);
    }

    private void attachDatabaseReadReference() {
        if (mFirebaseValueEventListener == null) {
            mFirebaseValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mGameList.clear();
                    UserBasicInfo userBasicInfo = dataSnapshot.getValue(UserBasicInfo.class);
                    if (userBasicInfo.getGamesOwned() == null) {
                        mEmptyTextView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                    } else {
                        List<Integer> gamesOwnedList = userBasicInfo.getGamesOwned();
                        for (int gameId : gamesOwnedList) {
                            mGameDataReference = FirebaseDatabase.getInstance().getReference()
                                    .child("games").child(String.valueOf(gameId));
                            mGameDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    GameDetails gameDetails = dataSnapshot.getValue(GameDetails.class);
                                    Game game = new Game();
                                    game.setId(gameDetails.getId());
                                    game.setName(gameDetails.getName());
                                    game.setCover(gameDetails.getCover());
                                    mGameList.add(game);
                                    mSingleGameSelectAdapter = new SingleGameSelectAdapter(mGameList, context);
                                    mRecyclerView.setAdapter(mSingleGameSelectAdapter);
                                    if (mSingleGameSelectAdapter.getItemCount() != 0) {
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
            mUserGamesReference.addValueEventListener(mFirebaseValueEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mFirebaseValueEventListener != null) {
            mUserGamesReference.removeEventListener(mFirebaseValueEventListener);
            mFirebaseValueEventListener = null;
        }
        List<Game> emptyList = new ArrayList<>();
        mSingleGameSelectAdapter = new SingleGameSelectAdapter(emptyList, context);
        mRecyclerView.setAdapter(mSingleGameSelectAdapter);
    }

}
