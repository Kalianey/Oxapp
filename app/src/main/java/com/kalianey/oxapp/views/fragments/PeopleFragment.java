package com.kalianey.oxapp.views.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.EndlessScrollListener;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.SessionManager;
import com.kalianey.oxapp.views.adapters.PeopleGridViewAdapter;

import java.util.ArrayList;
import java.util.List;

import br.com.goncalves.pugnotification.notification.PugNotification;

/**
 * A placeholder fragment containing a simple view.
 */
public class PeopleFragment extends Fragment {

    QueryAPI query = new QueryAPI();
    private GridView gridView;
    private SessionManager session;
    private PeopleGridViewAdapter adapter;
    private List<ModelUser> users = new ArrayList<>();
    private Integer first = 0;

    public PeopleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        PugNotification.with(getActivity().getApplicationContext())
                .load()
                .smallIcon(R.drawable.pugnotification_ic_launcher)
                .autoCancel(true)
                .largeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.pugnotification_ic_launcher))
                .title("title of my notif")
                .message("Message of my notif")
                        //.bigTextStyle(bigtext)
                .simple()
                .build();

        gridView = (GridView) view.findViewById(R.id.people_gridView);
        gridView.setClipToPadding(false);
        //setInsets(gridView);

        query.allUsers(first.toString(), new QueryAPI.ApiResponse<List<ModelUser>>() {
            @Override
            public void onCompletion(List<ModelUser> result) {
                Log.v("UserListCompletion", result.toString());
                if (!result.isEmpty() && result != null) {
                    users = result;
                    adapter = new PeopleGridViewAdapter(getActivity(), R.layout.people_gridview_item, users);
                    gridView.setAdapter(adapter);
                    //adapter.notifyDataSetChanged();
                    Log.v("Data Set Changed: ", users.toString());
                }
            }

        });

        gridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadMore(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });

    return view;

    }


    // Append more data into the adapter
    public void loadMore(int offset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter

        //Toast.makeText(getActivity(), "Scrolled to bottom", Toast.LENGTH_LONG).show();

        Integer index = users.size();
        query.allUsers(index.toString(), new QueryAPI.ApiResponse<List<ModelUser>>() {
            @Override
            public void onCompletion(List<ModelUser> result) {
                Log.v("UserListCompletion", result.toString());
                if (!result.isEmpty() && result != null) {
                    users.addAll(result);
                    adapter.notifyDataSetChanged();
                }
            }

        });
    }

}
