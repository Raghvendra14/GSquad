package com.example.android.gsquad.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.gsquad.R;
import com.example.android.gsquad.model.GameDetails;
import com.example.android.gsquad.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class GameDetailsActivity extends AppCompatActivity {
    private int mGameId;
    private Context mContext;
    private ProgressBar mProgressBar;
    private ImageView mImageView;
    private TextView mGameTitleTextView;
    private TextView mRatingTextView;
    private TextView mSummaryTextView;

    private DatabaseReference mGameDatabaseReference;
    private ValueEventListener mFirebaseValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.GAME_ID)) {
            mGameId = intent.getIntExtra(Constants.GAME_ID, 0);
        }
        if (mGameId != 0) {
            mContext = GameDetailsActivity.this;
            mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_game);
            mImageView = (ImageView) findViewById(R.id.game_cover_pic);
            mGameTitleTextView = (TextView) findViewById(R.id.game_name_text_view);
            mRatingTextView = (TextView) findViewById(R.id.rating_text_view);
            mSummaryTextView = (TextView) findViewById(R.id.summary_text_view);
            mGameDatabaseReference = FirebaseDatabase.getInstance().getReference().child("games")
                    .child(String.valueOf(mGameId));
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        detachDatabaseReadReference();
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
                    GameDetails gameDetails = dataSnapshot.getValue(GameDetails.class);
                    String coverUrl = gameDetails.getCover().getUrl();
                    if (coverUrl.contains("t_thumb")) {
                        coverUrl = coverUrl.replaceAll("t_thumb", "t_screenshot_med");
                    }
                    Glide.with(mContext)
                            .load("https:" + coverUrl)
                            .asBitmap()
                            .error(R.drawable.no_image)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .listener(new RequestListener<String, Bitmap>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                                    mProgressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    mProgressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(mImageView);

                    mGameTitleTextView.setText(gameDetails.getName());
                    String ratingString;
                    if (gameDetails.getAggregatedRating() != 0) {
                        ratingString = String.format(Locale.ENGLISH, " %s", String.valueOf(gameDetails.getAggregatedRating()));
                    } else {
                        ratingString = String.format(Locale.ENGLISH, " %s", getResources().getString(R.string.no_rating_available));
                    }
                    mRatingTextView.setText(ratingString);

                    if (gameDetails.getSummary() != null) {
                        mSummaryTextView.setText(gameDetails.getSummary());
                    } else {
                        mSummaryTextView.setText(getResources().getString(R.string.no_summary_available));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mGameDatabaseReference.addValueEventListener(mFirebaseValueEventListener);
        }
    }

    private void detachDatabaseReadReference() {
        if (mFirebaseValueEventListener != null) {
            mGameDatabaseReference.removeEventListener(mFirebaseValueEventListener);
            mFirebaseValueEventListener = null;
        }
    }

}
