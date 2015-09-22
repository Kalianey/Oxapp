package com.kalianey.oxapp.views.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.SessionManager;
import com.kalianey.oxapp.views.adapters.ConversationListAdapter;

import java.util.ArrayList;
import java.util.List;


public class ConversationListFragment extends Fragment {

    private QueryAPI query = new QueryAPI();
    private SessionManager session;
    private ListView listView;
    private ConversationListAdapter adapter;
    private List<ModelConversation> conversations = new ArrayList<>();

    public ConversationListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false); //creates the view

        listView = (ListView) view.findViewById(R.id.conversation_list);

        //session = new SessionManager(getActivity().getApplicationContext());

        query.conversationList(new QueryAPI.ApiResponse<List<ModelConversation>>() {
                @Override
                public void onCompletion(List<ModelConversation> result) {
                    Log.v("ConvListCompletion", result.toString());
                    if (!result.isEmpty() && result != null) {
                        conversations = result;
                        adapter = new ConversationListAdapter(getActivity(), R.layout.fragment_conversation_list_item, conversations);
                        listView.setAdapter(adapter);
                        //adapter.notifyDataSetChanged();
                        Log.v("Data Set Changed: ", conversations.toString());
                    }
                }

            });

        return view;
    }


}
