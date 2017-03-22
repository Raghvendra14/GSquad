package com.example.android.gsquad.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.gsquad.R;
import com.example.android.gsquad.adapter.NotificationListAdapter;

/**
 * Created by Raghvendra on 22-03-2017.
 */

public class NotificationListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private NotificationListAdapter mNotificationListAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;

    public NotificationListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_textview_notification);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.notification_list_progressBar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.notification_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        // add empty list into adapter
        // TODO: initialize and add data into adapter and set it to recycler view.

        return rootView;
    }
}
