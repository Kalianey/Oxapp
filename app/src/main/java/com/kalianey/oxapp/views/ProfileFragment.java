package com.kalianey.oxapp.views;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelUser;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends Fragment {

    private ModelUser user;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Get serialized object
        user = (ModelUser) getActivity().getIntent().getSerializableExtra("userObj");
        Log.v("User name: ", user.getName());

        return view;
    }
}
