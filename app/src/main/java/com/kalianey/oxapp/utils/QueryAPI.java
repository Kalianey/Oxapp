package com.kalianey.oxapp.utils;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.kalianey.oxapp.AppController;
import com.kalianey.oxapp.SessionManager;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.models.ModelUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kalianey on 11/08/2015.
 */
//http://stackoverflow.com/questions/31602042/android-java-how-to-delay-return-in-a-method


public class QueryAPI {

    private String hostname = "http://bonnieandclit.com/";

    private SessionManager session;

    public class ApiResult
    {
        public Boolean success;
        public String message;
        public Object data;

        public boolean dataIsArray()
        {
            return (data != null && data instanceof JSONArray);
        }

        public boolean dataIsObject()
        {
            return (data != null && data instanceof JSONObject);
        }

        public JSONArray getDataAsArray()
        {
            if ( this.dataIsArray()) {
                return (JSONArray) this.data;
            }
            else
            {
                return null;
            }
        }

        public JSONObject getDataAsObject()
        {
            if ( this.dataIsObject()) {
                return (JSONObject) this.data;
            }
            else
            {
                return null;
            }
        }

    }

    public interface ApiResponse<T>
    {
        public void onCompletion(T result);
    }


    public void RequestApi( String url, final ApiResponse<ApiResult> completion  )
    {
        Log.v("Performing request: ", url);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, hostname+url, (JSONObject) null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    Log.v("RequestApi Response", response.toString());
                    //Log.v("Data: ", response.toString());
                    try {
                        ApiResult res = new ApiResult();
                        Boolean success = response.getBoolean("success");
                        try {
                            res.data = response.getJSONArray("data");
                        }
                        catch (JSONException e)
                        {
                            Log.v("exception catch", e.getMessage());
                            res.data = response.getJSONObject("data");
                        }

                        res.success = success;
                        completion.onCompletion(res);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ApiResult res = new ApiResult();
                    res.success = false;
                    completion.onCompletion(res);
                }
            }
        );

        AppController.getInstance().addToRequestQueue(jsonRequest);
    }


//    func currentUser(completion: (ModelUser?) -> ()) {
//
//        self.requestAPI("owapi/user/profile", params: "") { (res: ApiResponse) -> () in
//
//            var item: ModelUser? //TODO error checking
//
//            if(res.success)
//            {
//                var data = res.data as! NSDictionary
//                item = ModelUser(data: data)
//            }
//            completion(item) //TODO: check if it should be put inside if or outside
//        }
//
//    }

    public void currentUser(final ApiResponse<ModelUser> completion) {

        String url = "owapi/user/profile";
        final ModelUser user = new ModelUser();

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                if (res.success && res.dataIsObject()) {
                    JSONObject userObj = res.getDataAsObject();
                    ModelUser user = new ModelUser();
                    user = new Gson().fromJson(userObj.toString(), ModelUser.class);
                    completion.onCompletion(user);
                }
                else {
                    completion.onCompletion(user);
                }
            }
        });

    }


    public void allUsers(final ApiResponse<List<ModelUser>> completion)
    {

        String url = "owapi/user/all";
        final List<ModelUser> users = new ArrayList<ModelUser>();

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                if (res.success && res.dataIsArray() ) {
                    JSONArray usersList = res.getDataAsArray();
                    for (int i = 0; i < usersList.length(); i++) {

                        try {
                            JSONObject jsonObject = usersList.getJSONObject(i);
                            ModelUser user = new Gson().fromJson(jsonObject.toString(), ModelUser.class);
                            users.add(user);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
//                Log.v("UserList Completion", res.data.toString());
                completion.onCompletion(users);
            }
        });


    }

    public void conversationList(final ApiResponse<List<ModelConversation>> completion)
    {
        String url = "owapi/messenger/conversationList";
        final List<ModelConversation> conversations = new ArrayList<ModelConversation>();

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                if (res.success && res.dataIsArray()) {
                    JSONArray conversationList = res.getDataAsArray();
                    for (int i = 0; i < conversationList.length(); i++) {

                        try {
                            JSONObject jsonObject = conversationList.getJSONObject(i);
                            ModelConversation conversation = new Gson().fromJson(jsonObject.toString(), ModelConversation.class);
                            conversations.add(conversation);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //Log.v("ConvList Completion", res.data.toString());
                completion.onCompletion(conversations);
            }
        });

    }


    /* LOGIN METHODS */

    public void login(final String username, final String password, final ApiResponse<ApiResult> completion) {

        session = new SessionManager(AppController.getContext());

        // Url of Oxwall website
        String url = hostname+"base/user/ajax-sign-in";

        final ApiResult res = new ApiResult();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Login Response", response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            res.success = jObj.getBoolean("result");
                            res.message = jObj.getString("message");

                            if (res.success) {
                                session.setLogin(true);
                                Boolean isUserLoggedIn = session.isLoggedIn();
                                Log.v("SessionSetUserLoggedIn", isUserLoggedIn.toString());
                                //Here request user and save it in a local var (or in sqlite), then in completion save email and password in sqlite
                                currentUser(new ApiResponse<ModelUser>() {
                                    @Override
                                    public void onCompletion(ModelUser user) {
                                        if(user != null){
                                            AppController.getInstance().setLoggedInUser(user);
                                            ModelUser currentUser = AppController.getInstance().getLoggedInUser();
                                            Log.v("Global var created",currentUser.toString());
                                        }
                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            res.success = false;
                            res.message = "Error during login";
                        }

                        completion.onCompletion(res);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("Login ERROR","error => "+error.toString());
                    }
                }
        ) {

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("form_name", "sign-in");
                params.put("identity", username);
                params.put("password", password); //userAccount.getPassword()
                params.put("remember", "on");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("User-Agent", "Oxapp");
                params.put("X-Requested-With", "XMLHTTPRequest");

                return params;
            }
        };

        Log.v("Login Request", postRequest.toString());
        AppController.getInstance().addToRequestQueue(postRequest);

    }



}

