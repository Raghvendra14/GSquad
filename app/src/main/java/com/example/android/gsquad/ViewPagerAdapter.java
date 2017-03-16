package com.example.android.gsquad;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Raghvendra on 16-03-2017.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public ViewPagerAdapter(FragmentManager fragmentManager, int numOfTabs) {
        super(fragmentManager);
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return new MainActivityFragment();
            }
            case 1: {
                return new FriendListFragment();
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
