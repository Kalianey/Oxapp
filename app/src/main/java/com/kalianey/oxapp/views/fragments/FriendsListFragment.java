package com.kalianey.oxapp.views.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelFriend;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.views.activities.MainActivity;
import com.kalianey.oxapp.views.adapters.FavoriteListAdapter;
import com.kalianey.oxapp.views.adapters.FriendListAdapter;
import com.kalianey.oxapp.views.adapters.FriendRequestListAdapter;

import java.util.ArrayList;
import java.util.List;

public class FriendsListFragment extends Fragment {

    private View parentView;
    private ListView listView;
    private FriendListAdapter mAdapter;

    public static FriendsListFragment getInstance(int position) {

        FriendsListFragment friendsListFragment = new FriendsListFragment();

        Bundle args = new Bundle();
        args.putInt("position", position);
        friendsListFragment.setArguments(args);

        return friendsListFragment;
    }

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

        QueryAPI query = new QueryAPI();

        Bundle bundle = getArguments();
        final int pos = bundle.getInt("position");

        if (bundle != null) {
            switch (pos) {
                case 0:
                    // Friend List
                    parentView.findViewById(R.id.avloadingIndicatorView).setVisibility(View.VISIBLE);
                    query.friendList(new QueryAPI.ApiResponse<List<ModelFriend>>() {
                        @Override
                        public void onCompletion(List<ModelFriend> result) {
                            parentView.findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
                            mAdapter = new FriendListAdapter(getActivity(), R.layout.fragment_friend_list_item, result);
                            listView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                    break;

                case 1:
                    // Friend Requests
                    parentView.findViewById(R.id.avloadingIndicatorView).setVisibility(View.VISIBLE);
                    query.friendRequestList(new QueryAPI.ApiResponse<List<ModelUser>>() {
                        @Override
                        public void onCompletion(List<ModelUser> result) {
                            parentView.findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
                            FriendRequestListAdapter adapter = new FriendRequestListAdapter(getActivity(), R.layout.fragment_friend_list_item, result);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    break;
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        parentView.findViewById(R.id.avloadingIndicatorView).setVisibility(View.VISIBLE);
    }
}
