package com.kalianey.oxapp.views;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kalianey.oxapp.AppController;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.SessionManager;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.views.adapters.ConversationListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A placeholder fragment containing a simple view.
 */

/** Todo : implement GSon
 * http://www.mkyong.com/java/how-do-convert-java-object-to-from-json-format-gson-api/
 * http://stackoverflow.com/questions/24401094/parse-android-volley-jsonarray-response
 * */

public class ConversationListFragment extends Fragment {

    private String url = "http://bonnieandclit.com/owapi/messenger/conversationList";
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

        query.login("kalianey", "Fxvcoar123@Sal", new QueryAPI.ApiResponse<QueryAPI.ApiResult>() {
            @Override
            public void onCompletion(QueryAPI.ApiResult res) {

                if (res.success) {
                    Log.v("Login Successful", res.success.toString());

                    query.conversationList(new QueryAPI.ApiResponse<List<ModelConversation>>() {
                        @Override
                        public void onCompletion(List<ModelConversation> result) {
                            Log.v("ConvListCompletion", result.toString());
                            if (!result.isEmpty() && result != null) {
                                conversations = result;
                                adapter = new ConversationListAdapter(getActivity(), R.layout.conversation_list_row, conversations);
                                listView.setAdapter(adapter);
                                //adapter.notifyDataSetChanged();
                                Log.v("Data Set Changed: ", conversations.toString());
                            }
                        }

                    });

                }
            }

        });





        return view;
    }


}
