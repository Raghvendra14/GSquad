package com.example.android.gsquad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.gsquad.R;
import com.example.android.gsquad.adapter.GameDetailsListAdapter;
import com.example.android.gsquad.database.FirebaseAddFriendsData;
import com.example.android.gsquad.database.RemoveFromFriendList;
import com.example.android.gsquad.model.GameDetails;
import com.example.android.gsquad.model.UserBasicInfo;
import com.example.android.gsquad.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private String mUserId;
    private boolean mIsCalledByFindFriends;
    private boolean mIsFriendProfile;
    private ProgressBar mProgressBar;
    private CircleImageView mCircleImageView;
    private TextView mTextView;
    private RecyclerView mRecyclerView;
    private TextView mEmptyTextView;
    private List<GameDetails> mGameDetailsList;
    private GameDetailsListAdapter mGameDetailsListAdapter;
    private Button mUnfriendButton;

    private DatabaseReference mUserDataReference;
    private DatabaseReference mGameDataReference;
    private ValueEventListener mFirebaseValueEventListener;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = UserProfileActivity.this;
        final Intent intent = getIntent();
        if (intent.hasExtra(Constants.USER_ID) && intent.hasExtra(Constants.CALLING_ACTIVITY)) {
            mUserId = intent.getStringExtra(Constants.USER_ID);
            mIsCalledByFindFriends = intent.getBooleanExtra(Constants.CALLING_ACTIVITY, false);
        }
        if (intent.hasExtra(Constants.IS_CALLED_BY_FRIEND_LIST)) {
            mIsFriendProfile = intent.getBooleanExtra(Constants.IS_CALLED_BY_FRIEND_LIST, false);
        }
        if (mIsCalledByFindFriends) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } else {
            toolbar.setVisibility(View.GONE);
        }
        if (mUserId != null) {
            mRecyclerView = (RecyclerView) findViewById(R.id.user_profile_games_recycler_view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                    linearLayoutManager.getOrientation());
            mRecyclerView.addItemDecoration(dividerItemDecoration);
            // Add empty array list into adapter
            mGameDetailsList = new ArrayList<>();
            mGameDetailsListAdapter = new GameDetailsListAdapter(mGameDetailsList, mContext, false);
            mRecyclerView.setAdapter(mGameDetailsListAdapter);

            mEmptyTextView = (TextView) findViewById(R.id.empty_user_profile_textview);
            mUnfriendButton = (Button) findViewById(R.id.remove_friend_button);
            mCircleImageView = (CircleImageView) findViewById(R.id.profile_pic);
            mCircleImageView.setBorderColor(getResources().getColor(android.R.color.white));
            mCircleImageView.setBorderWidth(getResources().getInteger(R.integer.profile_pic_border_width));
            mTextView = (TextView) findViewById(R.id.user_name_text_view);
            mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_profile);
            mProgressBar.setVisibility(View.VISIBLE);
            mUserDataReference = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(mUserId);
            if (mIsFriendProfile) {
                mUnfriendButton.setVisibility(View.VISIBLE);
            }
            mUnfriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RemoveFromFriendList removeFromFriendList = new RemoveFromFriendList();
                    removeFromFriendList.remove(mUserId);
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onPause() {
        detachDatabaseReference();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReference();
    }

    private void attachDatabaseReference() {
        if (mFirebaseValueEventListener == null) {
            mFirebaseValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mGameDetailsList.clear();
                    UserBasicInfo userBasicInfo = dataSnapshot.getValue(UserBasicInfo.class);
                    mProgressBar.setVisibility(View.GONE);
                    Glide.with(mContext)
                            .load(userBasicInfo.getPhotoUrl())
                            .asBitmap()
                            .error(R.drawable.no_image)
                            .into(mCircleImageView);
                    mTextView.setText(userBasicInfo.getName());
                    String contentDescription = String.format(Locale.ENGLISH, "%s %s", mTextView.getText(), getResources()
                            .getString(R.string.profile_content_label));
                    mCircleImageView.setContentDescription(contentDescription);
                    String profileLabel = getResources().getString(R.string.profile);
                    if (mIsCalledByFindFriends) {
                        setTitle(userBasicInfo.getName() + "'s " + profileLabel);
                    }

                    if (userBasicInfo.getGamesOwned() == null) {
                        mEmptyTextView.setVisibility(View.VISIBLE);
                    } else {
                        List<Integer> gamesOwnedList = userBasicInfo.getGamesOwned();
                        for (int gameId : gamesOwnedList) {
                            mGameDataReference = FirebaseDatabase.getInstance().getReference()
                                    .child("games").child(String.valueOf(gameId));
                            mGameDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    GameDetails gameDetails = dataSnapshot.getValue(GameDetails.class);
                                    mGameDetailsList.add(gameDetails);
                                    mGameDetailsListAdapter = new GameDetailsListAdapter(mGameDetailsList, mContext, false);
                                    mRecyclerView.setAdapter(mGameDetailsListAdapter);
                                    if (mGameDetailsListAdapter.getItemCount() != 0) {
                                        mEmptyTextView.setVisibility(View.GONE);
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
            mUserDataReference.addValueEventListener(mFirebaseValueEventListener);
        }
    }

    private void detachDatabaseReference() {
        if (mFirebaseValueEventListener != null) {
            mUserDataReference.removeEventListener(mFirebaseValueEventListener);
            mFirebaseValueEventListener = null;
        }
        List<GameDetails> emptyList = new ArrayList<>();
        mGameDetailsListAdapter = new GameDetailsListAdapter(emptyList, mContext, false);
        mRecyclerView.setAdapter(mGameDetailsListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mIsCalledByFindFriends) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_friend) {
            FirebaseAddFriendsData addUserInFriendList = new FirebaseAddFriendsData(mUserId,
                    FirebaseAuth.getInstance().getCurrentUser().getUid());
            addUserInFriendList.add();
            Toast.makeText(this, getResources().getString(R.string.friend_request_sent), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.PARENT_IS_ADD_FRIENDS, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
