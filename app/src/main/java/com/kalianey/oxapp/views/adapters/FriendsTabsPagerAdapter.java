package com.kalianey.oxapp.views.adapters;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kalianey.oxapp.views.fragments.FriendsListFragment;

/**
 * Created by kalianey on 28/08/2015.
 */
public class FriendsTabsPagerAdapter extends FragmentStatePagerAdapter {

    // Tab titles
    private String[] tabs = { "Friends", "Requests" };
    private FriendsListFragment[] mFragments;

    public FriendsTabsPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new FriendsListFragment[tabs.length];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragments[position] == null) {
            mFragments[position] = FriendsListFragment.getInstance(position);
        }

        return mFragments[position];
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}

