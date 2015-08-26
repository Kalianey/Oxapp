package com.kalianey.oxapp.utils;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.kalianey.oxapp.models.ModelAttachment;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.models.ModelFriend;
import com.kalianey.oxapp.models.ModelMessage;
import com.kalianey.oxapp.models.ModelQuestion;
import com.kalianey.oxapp.models.ModelUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by kalianey on 11/08/2015.
 */
//http://stackoverflow.com/questions/31602042/android-java-how-to-delay-return-in-a-method


public class QueryAPI {

    private String hostname = "http://bonnieandclit.com/";

    private SessionManager session;

    private static QueryAPI mInstance = new QueryAPI();

    public static synchronized QueryAPI getInstance() {
        return mInstance;

    }

    public class ApiResult {
        public Boolean success;
        public String message;
        public Object data;

        public boolean dataIsArray() {
            return (data != null && data instanceof JSONArray);
        }

        public boolean dataIsObject() {
            return (data != null && data instanceof JSONObject);
        }

        public boolean dataIsInteger() {
            return (data != null && data instanceof Integer);
        }

        public JSONArray getDataAsArray() {
            if (this.dataIsArray()) {
                return (JSONArray) this.data;
            } else {
                return null;
            }
        }

        public JSONObject getDataAsObject() {
            if (this.dataIsObject()) {
                return (JSONObject) this.data;
            } else {
                return null;
            }
        }

        public Integer getDataAsInteger() {
            if (this.dataIsInteger()) {
                return (Integer) this.data;
            } else {
                return null;
            }
        }

    }

    public interface ApiResponse<T> {
        public void onCompletion(T result);
    }


    public void RequestApi(String url, final ApiResponse<ApiResult> completion) {
        Log.v("Performing request: ", url);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, hostname + url, (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ApiResult res = new ApiResult();
                        Log.v("RequestApi Response", response.toString());
                        //Log.v("Data: ", response.toString());
                        try {

                            Boolean success = response.getBoolean("success");
                            try {
                                res.data = response.getJSONArray("data");
                            } catch (JSONException e) {
                                Log.v("exception catch", e.getMessage());
                                try {
                                    res.data = response.getJSONObject("data");
                                } catch (JSONException x) {
                                    Log.v("exception catch", x.getMessage());
                                    res.data = response.getInt("data");
                                }
                            }

                            res.success = success;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        completion.onCompletion(res);
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


//    public void RequestApiPost(String url, final Map<String, String> params, final ApiResponse<ApiResult> completion) {
//
//        String body = "";
//        StringBuilder encodedParams = new StringBuilder();
//        try {
//            for (Map.Entry<String, String> entry : params.entrySet()) {
//                encodedParams.append(URLEncoder.encode(entry.getKey(), "UTF8"));
//                encodedParams.append('=');
//                encodedParams.append(URLEncoder.encode(entry.getValue(), "UTF8"));
//                encodedParams.append('&');
//            }
//            body = encodedParams.toString();
//        } catch (UnsupportedEncodingException uee) {
//            Log.v("RequestApiPost ", "error encoding UTF8");
//        }
//
//
//
//
//
//
//        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, hostname + url, body,
//            new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    System.out.println(response);
//
//                }
//            },
//            new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    System.out.println(error.getLocalizedMessage());
//                }
//            }){
//
//            public String getBodyContentType() {
//                return "application/x-www-form-urlencoded; charset=utf-8" ;
//            }
//        }
//        ;
//
//        AppController.getInstance().addToRequestQueue(jsonRequest);
//    }


    public void RequestApiPOST (String url, final Map<String, String> params, final ApiResponse<ApiResult> completion )
    {

        StringRequest strRequest = new StringRequest(Request.Method.POST, hostname+url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String responseString)
                    {
                        JSONObject response = null;
                        ApiResult res = new ApiResult();
                        try {
                            response = new JSONObject(responseString);

                            Log.v("POST Response", response.toString());
                            try {

                                Boolean success = response.getBoolean("success");
                                try {
                                    res.data = response.getJSONArray("data");
                                }
                                catch (JSONException e)
                                {
                                    Log.v("exception catch", e.getMessage());
                                    try {
                                        res.data = response.getJSONObject("data");
                                    }
                                    catch (JSONException x)
                                    {
                                        Log.v("exception catch", x.getMessage());
                                        res.data = response.getInt("data");
                                    }
                                }

                                res.success = success;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        completion.onCompletion(res);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.v("error post:" , error.getLocalizedMessage());
                    }
                })

        {
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strRequest);
    }


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

    public void user(String userId, final ApiResponse<ModelUser> completion) {

        String url = "owapi/user/profile/"+userId;
        final ModelUser user = new ModelUser();

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                if (res.success && res.dataIsObject()) {
                    JSONObject userObj = res.getDataAsObject();
                    ModelUser user = new ModelUser();
                    user = new Gson().fromJson(userObj.toString(), ModelUser.class);
                    completion.onCompletion(user);
                } else {
                    completion.onCompletion(user);
                }
            }
        });

    }

    public void userExtra(final ModelUser user, final ApiResponse<ModelUser> completion)  {

        String url = "owapi/user/profile/extra/"+user.getUserId();
        final List<ModelUser> friends = new ArrayList<ModelUser>();
        final List<ModelAttachment> photos = new ArrayList<ModelAttachment>();

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {
                if (res.success)
                {
                    JSONObject data = res.getDataAsObject();

                    //Get friends
                    try {
                        JSONObject friendList = data.getJSONObject("friends");

                        Iterator<String> keysIterator = friendList.keys();
                        while (keysIterator.hasNext())
                        {
                            String keyStr = (String)keysIterator.next();
                            String valueStr = friendList.getString(keyStr);
                            ModelUser friend = new ModelUser();
                            friend = new Gson().fromJson(valueStr.toString(), ModelUser.class);
                            friends.add(friend);
                        }

                        user.setFriends(friends);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Get photos
                    try {
                        JSONObject photoList = data.getJSONObject("photos");

                        Iterator<String> keysIterator = photoList.keys();
                        while (keysIterator.hasNext())
                        {
                            String keyStr = (String)keysIterator.next();
                            String valueStr = photoList.getString(keyStr);
                            ModelAttachment photo = new ModelAttachment();
                            photo = new Gson().fromJson(valueStr.toString(), ModelAttachment.class);
                            photos.add(photo);
                        }
                        user.setPhotos(photos);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //questions
                    try {
                        JSONObject questions = data.getJSONObject("questions");
                        JSONArray sections = questions.getJSONArray("sections");
                        Log.v("sections", sections.toString()); // ["Infos","Recherche"]
                        user.setSections(sections);
                        JSONArray questionData = questions.getJSONArray("questionsData");
                        //user.setQuestions(questionData);

                        List<ModelQuestion> questionList = new ArrayList<ModelQuestion>();

                        for (int i = 0; i < questionData.length(); i++) {
                            try {
                                JSONArray questionArray = questionData.getJSONArray(i);
                                for (int index = 0; index < questionArray.length(); index++) {
                                    JSONObject questionObj = questionArray.getJSONObject(index);
                                    ModelQuestion q = new ModelQuestion();
                                    q.setQuestionName( questionObj.getString("questionName") );
                                    q.setQuestionValue( questionObj.getString("questionValue") );
                                    q.setSection( questionObj.getString("sectionName") );
                                    questionList.add(q);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        user.setQuestions(questionList);

                        Log.v("questionData", questionData.toString()); //[[{"questionValue":"07 Mai","sectionName":"Infos","sectionKey":"f90cde5913

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                completion.onCompletion(user);
            }
        });



    }



    public void allUsers(String first, final ApiResponse<List<ModelUser>> completion)
    {

        String url = "owapi/user/all/"+first;
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

    public void friendList(final ApiResponse<List<ModelFriend>> completion)
    {

        String url = "owapi/messenger/contactList";
        final List<ModelFriend> friends = new ArrayList<ModelFriend>();

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                Log.d("Friends", res.data.toString());
                if (res.success && res.dataIsObject() ) {
                    JSONObject data = res.getDataAsObject();
                    JSONArray friendList = null;
                    try {
                        friendList = data.getJSONArray("list");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("FriendList",friendList.toString() );
                    for (int i = 0; i < friendList.length(); i++) {
                        try {
                            JSONObject jsonObject = friendList.getJSONObject(i);
                            ModelFriend friend = new Gson().fromJson(jsonObject.toString(), ModelFriend.class);
                            friends.add(friend);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                completion.onCompletion(friends);
            }
        });


    }


    /* Conversation Functions */

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
                //Log.d("ConvList Completion", res.data.toString());
                completion.onCompletion(conversations);
            }
        });

    }



    public void messageList(String convId, final ApiResponse<List<ModelMessage>> completion) {
        String url = "owapi/messenger/conversation/"+convId;

        final List<ModelMessage> messages = new ArrayList<ModelMessage>();

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {
                Log.d("Success",res.toString() );

                if (res.success && res.dataIsObject()) {
                    JSONObject conversation = res.getDataAsObject();
                    JSONArray messageList = null;
                    try {
                        messageList = conversation.getJSONArray("messages");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("MessageList",messageList.toString() );
                    for (int i = 0; i < messageList.length(); i++) {

                        try {
                            JSONObject jsonObject = messageList.getJSONObject(i);
                            try {
                                ModelMessage message = new Gson().fromJson(jsonObject.toString(), ModelMessage.class);
                                if (!message.getAttachment().equals("")){
                                    Log.v("Message", message.getAttachment().toString());
                                    LinkedTreeMap<String, Object> attachment = (LinkedTreeMap<String, Object>) message.getAttachment();
                                    String downloadUrl = (String) attachment.get("downloadUrl");
                                    String attachmentId = (String) attachment.get("id");
                                    message.setDownloadUrl(downloadUrl);
                                    message.setAttachmentId(attachmentId);
                                    message.setIsMediaMessage(true);
                                } else {
                                    message.setIsMediaMessage(false);
                                }

                                messages.add(message);
                            }
                           catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //Log.d("ConvList Completion", res.data.toString());
                completion.onCompletion(messages);
            }
        });
    };



    public void conversationGet(String opponentId, final ApiResponse<String> completion) {
        String url = "owapi/messenger/conversation/get/" + opponentId;

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                String convId = "";
                if (res.success && res.dataIsInteger()) {
                    Integer result = res.getDataAsInteger();
                    convId = result.toString();
                }
                completion.onCompletion(convId);
            }
        });
    }


    public void conversationCreate(String opponentId, final ApiResponse<ModelConversation> completion) {
        String url = "owapi/messenger/conversation/create";
        //here need to pass post params
        Log.v("url", url);

//        this.RequestApi(url, new ApiResponse<ApiResult>() {
//            @Override
//            public void onCompletion(ApiResult res) {
//
//                String convId = "";
//                if (res.success && res.dataIsInteger()) {
//                    Integer result = res.getDataAsInteger();
//                    convId = result.toString();
//                    Log.v("My convid", convId);
//                }
//                completion.onCompletion(convId);
//            }
//        });
    }


    public void conversationHistory(String conversationId, String lastMessageId, final ApiResponse<List<ModelMessage>> completion) {

        String url = "owapi/messenger/conversation/"+conversationId+"/history/"+lastMessageId;

        final List<ModelMessage> messages = new ArrayList<ModelMessage>();

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {
                Log.d("Success MessHist",res.data.toString() );

                if (res.success && res.dataIsArray()) {
                    JSONArray messageList = res.getDataAsArray();

                    Log.d("MessageList",messageList.toString() );
                    for (int i = 0; i < messageList.length(); i++) {

                        try {
                            JSONObject jsonObject = messageList.getJSONObject(i);
                            try {
                                ModelMessage message = new Gson().fromJson(jsonObject.toString(), ModelMessage.class);
                                if (!message.getAttachment().equals("")){
                                    Log.v("Message", message.getAttachment().toString());
                                    LinkedTreeMap<String, Object> attachment = (LinkedTreeMap<String, Object>) message.getAttachment();
                                    String downloadUrl = (String) attachment.get("downloadUrl");
                                    String attachmentId = (String) attachment.get("id");
                                    message.setDownloadUrl(downloadUrl);
                                    message.setAttachmentId(attachmentId);
                                    message.setIsMediaMessage(true);
                                } else {
                                    message.setIsMediaMessage(false);
                                }

                                messages.add(message);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //Log.d("ConvList Completion", res.data.toString());
                completion.onCompletion(messages);
            }
        });

    }

//    func conversationHistory(conversationId: String, lastMessageId: String, completion: ([ModelMessage]) -> ()) -> NSURLSessionDataTask {
//
//        let url = "owapi/messenger/conversation/"+conversationId+"/history/"+lastMessageId
//        println(url)
//
//        var task = self.requestAPI(url, params: "") { (res: ApiResponse) -> () in
//            var items = [ModelMessage]() //TODO error checking
//
//            if(res.success)
//            {
//                var data = res.data as! NSArray
//                //println(data)
//                for item in data{
//
//                let msg = ModelMessage(data: item as! NSDictionary)
//                items.append(msg)
//
//            }
//
//            }
//
//            completion(items)
//
//        }
//        return task
//    }



    public void messageSend(String convId, String messageText, final ApiResponse<ModelMessage> completion) {

        String url = "owapi/messenger/conversation/"+convId+"/send/";
        Map<String, String> params = new HashMap<String, String>();
        params.put("text", messageText);

        this.RequestApiPOST(url, params, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                Log.v("VicRes", res.toString());
                if (res.success) {
                    ModelMessage message = new ModelMessage();
                    message = new Gson().fromJson(res.data.toString(), ModelMessage.class);
                    completion.onCompletion(message);

                } else {
                    completion.onCompletion(null);
                }
            }
        });

    }


    /* DEVICE REGISTRATION FOR GCM PUSH NOTIFICATIONS */

    public void registerForNotifications(String token, final ApiResponse<String> completion) {

        String url = "owapi/device/register";
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("kind", "android");

        this.RequestApiPOST(url, params, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                Log.v("GCM registration:", res.success.toString());
                res.message = "GCM registration "+ res.success.toString();

                if (res.success){
                    res.message = "GCM registration successful";
                }

                completion.onCompletion(res.message);

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


