package com.kalianey.oxapp.views;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kalianey.oxapp.AppController;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelConversation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
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
    private ArrayList<ModelConversation> conversations = new ArrayList<>();

    public ConversationListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false); //creates the view

        JsonObjectRequest conversationRequest = new JsonObjectRequest(Request.Method.GET, url, (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v("Data: ", response.toString());
                try {
                    Boolean success = response.getBoolean("success");
                    Log.v("Success: ", success.toString());
                    JSONArray data = response.getJSONArray("data");
                    Log.v("DataArray: ", data.toString());
                    //JSONObject conversations = data.getJSONObject()

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );



        AppController.getInstance().addToRequestQueue(conversationRequest);

        return view;
    }
}
