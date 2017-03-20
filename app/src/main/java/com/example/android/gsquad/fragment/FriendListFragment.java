package com.example.android.gsquad.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.gsquad.activity.AddFriendActivity;
import com.example.android.gsquad.R;
import com.example.android.gsquad.adapter.FriendListAdapter;
import com.example.android.gsquad.utils.Utils;

/**
 * Created by Raghvendra on 16-03-2017.
 */

public class FriendListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FriendListAdapter mFriendListAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;

    public FriendListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend, container, false);

        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_textview_friend);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.friend_list_progressBar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.friends_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        // add empty list into adapter
       // TODO: initialize and add data into adapter and set it to recycler view.

        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_friend);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkAvailable(getActivity())) {
                    startActivity(new Intent(getActivity(), AddFriendActivity.class));
                } else {
                    Snackbar.make(v, getString(R.string.no_connection_available),
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }
}
