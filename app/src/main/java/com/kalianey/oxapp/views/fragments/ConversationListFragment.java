package com.kalianey.oxapp.views.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.SessionManager;
import com.kalianey.oxapp.views.activities.Message;
import com.kalianey.oxapp.views.adapters.ConversationListAdapter;

import java.util.ArrayList;
import java.util.List;


public class ConversationListFragment extends Fragment {

    private QueryAPI query = new QueryAPI();
    private SessionManager session;
    private ListView listView;
    private ConversationListAdapter adapter;
    private List<ModelConversation> conversations = new ArrayList<>();
    private TextView noConversation;
    private View view;

    private BroadcastReceiver gcmReceiver;

    public ConversationListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_conversation_list, container, false); //creates the view

        listView = (ListView) view.findViewById(R.id.conversation_list);
        noConversation = (TextView) view.findViewById(R.id.noConversation);

        gcmReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadConversationList();
            }
        };

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

        loadConversationList();

        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(gcmReceiver, new IntentFilter("msg-received"));

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(gcmReceiver);
    }


    private void loadConversationList(){
        //If coming from GCM, we unparcel bundle and load conversation detail
        Bundle bundle = getArguments();
        if (bundle != null) {
            ModelConversation conversation = (ModelConversation) bundle.getSerializable("convObj");
            Intent i = new Intent(getActivity(), Message.class);
            i.putExtras(bundle);
            getActivity().startActivity(i);
        }

        query.conversationList(new QueryAPI.ApiResponse<List<ModelConversation>>() {
            @Override
            public void onCompletion(List<ModelConversation> result) {
                Log.v("ConvListCompletion", result.toString());
                view.findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
                if (!result.isEmpty() && result != null) {
                    conversations = result;
                    adapter = new ConversationListAdapter(getActivity(), R.layout.fragment_conversation_list_item, conversations);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Log.v("Data Set Changed: ", conversations.toString());
                } else {
                    noConversation.setVisibility(View.VISIBLE);
                }
            }

        });
    }

}
