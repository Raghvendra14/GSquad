package com.example.android.gsquad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;

import com.example.android.gsquad.R;
import com.example.android.gsquad.adapter.FindFriendsAdapter;
import com.example.android.gsquad.utils.Constants;

public class FindFriendsActivity extends AppCompatActivity {
    private int mGameId;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private FindFriendsAdapter mFindFriendsAdapter;
    private Context mContext;

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

    }

}
