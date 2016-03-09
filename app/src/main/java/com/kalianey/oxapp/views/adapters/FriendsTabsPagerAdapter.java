package com.kalianey.oxapp.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kalianey.oxapp.views.fragments.FriendsListFragment;

/**
 * Created by kalianey on 28/08/2015.
 */
public class FriendsTabsPagerAdapter extends FragmentPagerAdapter {

    // Tab titles
    private String[] tabs = { "Friends", "Requests" };

    public FriendsTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {

        FriendsListFragment friendFragment = FriendsListFragment.getInstance(position);

        return friendFragment;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}

