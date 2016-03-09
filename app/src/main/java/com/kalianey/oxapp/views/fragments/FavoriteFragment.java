package com.kalianey.oxapp.views.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelFavorite;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.views.adapters.FavoriteListAdapter;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class FavoriteFragment extends Fragment {

    private ListView listView;
    private FavoriteListAdapter mAdapter;
    QueryAPI query = new QueryAPI();
    private ArrayList<ModelUser> users = new ArrayList<ModelUser>();
    private TextView noFavorite;

    public static FavoriteFragment getInstance(int position) {

        FavoriteFragment favoriteFragment = new FavoriteFragment();

        Bundle args = new Bundle();
        args.putInt("position", position);
        favoriteFragment.setArguments(args);

        return favoriteFragment;
    }

    public FavoriteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        noFavorite = (TextView) view.findViewById(R.id.noFav);

        query.favorite(new QueryAPI.ApiResponse<ModelFavorite>() {
            @Override
            public void onCompletion(ModelFavorite favorite) {

                view.findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);

                if (favorite != null ) {
//                    users.clear();
//                    users = favorite.getMy();
//                    mAdapter = new FavoriteListAdapter(getActivity(), R.layout.fragment_favorite, users);
//                    listView.setAdapter(mAdapter);
//                    mAdapter.notifyDataSetChanged();

                    Bundle bundle = getArguments();
                    final int pos = bundle.getInt("position");

                    if (bundle != null) {
                        switch (pos) {
                            case 0:
                                // My fav fragment activity
                                if (users != null) {
                                    users.clear();
                                    users = favorite.getMy();
                                    mAdapter = new FavoriteListAdapter(getActivity(), R.layout.fragment_favorite, users);
                                    listView.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
                                }
                                else {
                                    noFavorite.setText("No favorite yet.");
                                    noFavorite.setVisibility(View.VISIBLE);
                                }

                                break;

                            case 1:
                                // Added me fav fragment activity
                                if ( users != null) {
                                    users.clear();
                                    users = favorite.getMe();
                                    mAdapter = new FavoriteListAdapter(getActivity(), R.layout.fragment_favorite, users);
                                    listView.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
                                }
                                else {
                                    noFavorite.setVisibility(View.VISIBLE);
                                }

                                break;
                            case 2:
                                // Mutual fav fragment activity
                                if (users != null) {
                                    users.clear();
                                    users = favorite.getMutual();
                                    mAdapter = new FavoriteListAdapter(getActivity(), R.layout.fragment_favorite, users);
                                    listView.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
                                }
                                else {
                                    noFavorite.setVisibility(View.VISIBLE);
                                }

                                break;
                        }
                    }
                }
            }

        });

        return view;
    }
}
