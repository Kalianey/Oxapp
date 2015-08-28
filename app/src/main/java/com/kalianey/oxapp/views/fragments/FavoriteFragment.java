package com.kalianey.oxapp.views.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kalianey.oxapp.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class FavoriteFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        Bundle bundle = getArguments();
        int pos = bundle.getInt("position");

        if(bundle != null) {
            Log.d("Favorite fragment n:", String.valueOf(pos) );
        }

//        switch (index) {
//            case 0:
//                // My fav fragment activity
//                return new FavoriteFragment();
//            case 1:
//                // Added me fav fragment activity
//                //return new AddedMeFavoriteFragment();
//            case 2:
//                // Mutual fav fragment activity
//                //return new MutualFavoriteFragment();
//        }

        return view;
    }
}
