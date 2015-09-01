package com.kalianey.oxapp.views.fragments;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kalianey.oxapp.models.ModelFriend;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.views.activities.MainActivity;
import com.kalianey.oxapp.views.adapters.FriendListAdapter;

import android.view.Window;
import android.widget.ListView;

import com.kalianey.oxapp.R;

import java.util.ArrayList;
import java.util.List;

public class FriendsListFragment extends Fragment {

    private View parentView;
    private ListView listView;
    private FriendListAdapter mAdapter;
    private List<ModelFriend> friends = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        android.support.v7.app.ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_friend_list, container, false);
        listView   = (ListView) parentView.findViewById(R.id.listView);
        initView();
        return parentView;
    }

    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void initView() {
        //Getting width of display, could be useful for scaling bitmaps
//        Display display = getActivity().getWindowManager().getDefaultDisplay();
//        int width;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
//            Point size = new Point();
//            display.getSize(size);
//            width = size.x;
//        } else {
//            width = display.getWidth();
//        }


        QueryAPI query = new QueryAPI();
        query.friendList(new QueryAPI.ApiResponse<List<ModelFriend>>() {
            @Override
            public void onCompletion(List<ModelFriend> result) {

                mAdapter = new FriendListAdapter(getActivity(), R.layout.fragment_friend_list_item, result);
                listView.setAdapter(mAdapter);
            }
        });
    }

}
