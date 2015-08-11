package com.kalianey.oxapp.utils;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kalianey.oxapp.models.ModelConversation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by kalianey on 11/08/2015.
 */
public class QueryAPI {

    public class ApiResult
    {
        public Boolean success;
        public String message;
        public Object data;
    }

    public interface ApiResponse<ApiResult>
    {
        public void onCompletion(ApiResult result);
    }

//    public interface ApiResponse<T>
//    {
//        public void onCompletion(T result);
//    }

//    public void getMessage( final ApiResponse<ModelMessage> completion  )
//    {
//
//    }


    public void getConversations( final ApiResponse<ModelConversation> completion  )
    {

        String url = "";
        JsonObjectRequest conversationRequest = new JsonObjectRequest(Request.Method.GET, url, (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {

                Log.v("Data: ", response.toString());
                try {
                    Boolean success = response.getBoolean("success");
                    Log.v("Success: ", success.toString());
                    JSONArray data = response.getJSONArray("data");
                    Log.v("DataArray: ", data.toString());
                    //JSONObject conversations = data.getJSONObject()
                    //ModelConversation myModel = new ModelConversation(data);
                    ApiResult res = new ApiResult();
                    //completion.onCompletion(res);

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
    }

}
