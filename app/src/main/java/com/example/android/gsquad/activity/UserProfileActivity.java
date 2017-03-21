package com.example.android.gsquad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.gsquad.R;
import com.example.android.gsquad.model.UserBasicInfo;
import com.example.android.gsquad.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private String mUserId;
    private ProgressBar mProgressBar;
    private CircleImageView mCircleImageView;
    private TextView mTextView;
    private DatabaseReference mUserDataReference;
    private ValueEventListener mFirebaseValueEventListener;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = UserProfileActivity.this;
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.USER_ID)) {
            mUserId = intent.getStringExtra(Constants.USER_ID);
        }
        if (mUserId != null) {
            mCircleImageView = (CircleImageView) findViewById(R.id.profile_pic);
            mCircleImageView.setBorderColor(getResources().getColor(android.R.color.white));
            mCircleImageView.setBorderWidth(getResources().getInteger(R.integer.profile_pic_border_width));
            mTextView = (TextView) findViewById(R.id.user_name_text_view);
            mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_profile);
            mProgressBar.setVisibility(View.VISIBLE);
            mUserDataReference = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(mUserId);
        }
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
                    UserBasicInfo userBasicInfo = dataSnapshot.getValue(UserBasicInfo.class);
                    mProgressBar.setVisibility(View.GONE);
                    Glide.with(mContext)
                            .load(userBasicInfo.getPhotoUrl())
                            .asBitmap()
                            .error(R.drawable.no_image)
                            .into(mCircleImageView);
                    mTextView.setText(userBasicInfo.getName());
                    mCircleImageView.setContentDescription(mTextView.getText() + " Profile Picture");
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_friend) {
//            AddUserInFriendList addUserInFriendList = new AddUserInFriendList();
//            addUserInFriendList.apply();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
