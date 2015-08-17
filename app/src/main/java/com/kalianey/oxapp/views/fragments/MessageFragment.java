package com.kalianey.oxapp.views.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.models.ModelMessage;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.SessionManager;
import com.kalianey.oxapp.views.adapters.MessageListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MessageFragment extends Fragment {

    private QueryAPI query = new QueryAPI();
    private SessionManager session;
    private ListView listView;
    private MessageListAdapter adapter;
    private List<ModelMessage> messages = new ArrayList<>();
    private ModelConversation conversation;

    public MessageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        listView = (ListView) view.findViewById(R.id.message_list);

        //Get serialized object
        conversation = (ModelConversation) getActivity().getIntent().getSerializableExtra("convObj");
        Log.d("ConvId for messList: ",conversation.getId() );


        query.messageList(conversation.getId(), new QueryAPI.ApiResponse<List<ModelMessage>>() {
            @Override
            public void onCompletion(List<ModelMessage> result) {
                messages = result;
                ModelUser opponent = new ModelUser();

                QueryAPI.getInstance().user(conversation.getOpponentId(), new QueryAPI.ApiResponse<ModelUser>() {
                    @Override
                    public void onCompletion(ModelUser user) {
                        adapter = new MessageListAdapter(getActivity(), R.layout.message_item_sent, messages);
                        adapter.setSenderUser(user);
                        listView.setAdapter(adapter);
                        //adapter.notifyDataSetChanged();
                        Log.d("AdapterChanged mess: ", messages.toString());
                    }
                });


            }
        });

        return view;
    }
}
