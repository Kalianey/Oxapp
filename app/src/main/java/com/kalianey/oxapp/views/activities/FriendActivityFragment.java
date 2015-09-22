package com.kalianey.oxapp.views.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kalianey.oxapp.R;
import com.kalianey.oxapp.utils.SlidingTabLayout;
import com.kalianey.oxapp.views.adapters.FriendsTabsPagerAdapter;

/**
 * Created by kalianey on 22/09/2015.
 */
public class FriendActivityFragment extends Fragment {


    public FriendActivityFragment() {
    }

    private ViewPager mPager;
    private SlidingTabLayout mTabs;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_friend, container, false);
        super.onCreate(savedInstanceState);

        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(new FriendsTabsPagerAdapter(getActivity().getSupportFragmentManager()));
        mTabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);
        mTabs.setSelectedIndicatorColors(getResources().getColor(R.color.red));
        mTabs.setBackgroundColor(getResources().getColor(R.color.theme_color));
        mTabs.setViewPager(mPager);

        return view;

    }
}