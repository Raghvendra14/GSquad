package com.example.android.gsquad.listener;

import android.view.View;

/**
 * Created by Raghvendra on 18-03-2017.
 */

public interface RecyclerClickListener {
    /*
    *  Interface for Recycler View Click Listener
    */

    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
