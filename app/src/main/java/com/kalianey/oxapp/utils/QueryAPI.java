package com.kalianey.oxapp.utils;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.kalianey.oxapp.models.ModelAttachment;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.models.ModelFavorite;
import com.kalianey.oxapp.models.ModelFriend;
import com.kalianey.oxapp.models.ModelMessage;
import com.kalianey.oxapp.models.ModelQuestion;
import com.kalianey.oxapp.models.ModelUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by kalianey on 11/08/2015.
 */
//http://stackoverflow.com/questions/31602042/android-java-how-to-delay-return-in-a-method


public class QueryAPI {

//    private String hostname = "http://192.168.123.1/bonnieandclit/";
//    private String hostname = "http://10.0.3.2/bonnieandclit/";
//    private String hostname = "http://192.168.57.1/bonnieandclit/";
    private String hostname = "http://bonnieandclit.com/";

//    private String hostname = AppController.getHostname();

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
                            res.success = false;
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

        //Added because of Login ERROR﹕ error => com.android.volley.TimeoutError
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));

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
                        if (responseString != null) {
                            Log.d("POSTResponse", responseString);
                        }
                        JSONObject response = null;
                        ApiResult res = new ApiResult();
                        res.success = false;
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
                                        try {
                                            res.data = response.getInt("data");
                                        }
                                        catch (JSONException z)
                                        {
                                            res.data = response.getString("data");
                                        }
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
                        Log.v("error post:", error.toString());
                    }
                })

        {
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };

        //Added for fb connect which timeout
        strRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));

        AppController.getInstance().addToRequestQueue(strRequest);
    }


    public void RequestMultiPart(File file, String filename, String boundary, String url, String fileField, Map<String,String> params, final ApiResponse<String> completion ) {

        final String reqUrl = hostname+url;
        MultipartRequest imageUploadReq = new MultipartRequest(reqUrl,params,file,filename,fileField,
            new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("Multipart Request Url: ", reqUrl);
                        Log.d("Multipart ERROR", "error => " + error.toString());
                        completion.onCompletion(error.toString());
                    }
                },
            new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MediaSent Response", response);
                        completion.onCompletion(response);

                    }
                }


            ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                CookieManager manager = AppController.getInstance().getCookieManager();
                List<HttpCookie> cookies = manager.getCookieStore().getCookies();
                String cookie = "";
                for (HttpCookie eachCookie : cookies) {
                    String cookieName = eachCookie.getName().toString();
                    String cookieValue = eachCookie.getValue().toString();
                    cookie += cookieName + "=" + cookieValue + "; ";
                }
                headers.put("Cookie", cookie);
                return headers;
            }

        };

        imageUploadReq.setRetryPolicy(new DefaultRetryPolicy(1000 * 60, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(imageUploadReq);
    }

    /*** END REQUESTS METHODS ***/


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
                } else {
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
        final ArrayList<ModelAttachment> photos = new ArrayList<ModelAttachment>();

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

        String url = "owapi/user/friendList";
        final List<ModelFriend> friends = new ArrayList<ModelFriend>();

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                if (res.success && res.dataIsObject() ) {
                    JSONObject json = res.getDataAsObject();
                    for(Iterator<String> iter = json.keys();iter.hasNext();) {
                        String key = iter.next();
                        try {
                            JSONObject jsonObject = json.getJSONObject(key);
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

    public void friendRequestList(final ApiResponse<List<ModelUser>> completion)
    {

        String url = "owapi/user/friend/requestList";
        final List<ModelUser> users = new ArrayList<ModelUser>();

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                if (res.success && res.dataIsObject() ) {
                    JSONObject data = res.getDataAsObject();
                    JSONArray friendList = null;
                    try {
                        friendList = data.getJSONArray("friendsRequest");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < friendList.length(); i++) {
                        try {
                            JSONObject jsonObject = friendList.getJSONObject(i);
                            ModelUser user = new Gson().fromJson(jsonObject.toString(), ModelUser.class);
                            users.add(user);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    completion.onCompletion(users);
                }
                else {
                    completion.onCompletion(users);
                }
                //completion.onCompletion(users);
            }
        });

    }


    public void favorite(final ApiResponse<ModelFavorite> completion)
    {

        String url = "owapi/user/favorite/list";
        final ModelFavorite favorite = new ModelFavorite();

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                if (res.success && res.dataIsObject()) {

                    JSONObject usersList = res.getDataAsObject();

                    //Get Me array
                    try {
                        JSONObject me = usersList.getJSONObject("me");
                        favorite.setMeMenuLabel(me.getString("menu-label"));
                        JSONArray list = me.getJSONArray("userIds");
                        ArrayList<ModelUser> users = new ArrayList<ModelUser>();
                        for (int i = 0; i < list.length(); i++) {

                            JSONObject jsonObject = list.getJSONObject(i);
                            ModelUser user = new Gson().fromJson(jsonObject.toString(), ModelUser.class);
                            users.add(user);
                        }
                        favorite.setMe(users);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Get My array
                    try {
                        JSONObject my = usersList.getJSONObject("my");
                        favorite.setMyMenuLabel(my.getString("menu-label"));
                        JSONArray list = my.getJSONArray("userIds");
                        ArrayList<ModelUser> users = new ArrayList<ModelUser>();
                        for (int i = 0; i < list.length(); i++) {

                            JSONObject jsonObject = list.getJSONObject(i);
                            ModelUser user = new Gson().fromJson(jsonObject.toString(), ModelUser.class);
                            users.add(user);
                        }
                        favorite.setMy(users);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Get Mutual array
                    try {
                        JSONObject mutual = usersList.getJSONObject("mutual");
                        favorite.setMutualMenuLabel(mutual.getString("menu-label"));
                        JSONArray list = mutual.getJSONArray("userIds");
                        ArrayList<ModelUser> users = new ArrayList<ModelUser>();
                        for (int i = 0; i < list.length(); i++) {

                            JSONObject jsonObject = list.getJSONObject(i);
                            ModelUser user = new Gson().fromJson(jsonObject.toString(), ModelUser.class);
                            users.add(user);
                        }
                        favorite.setMutual(users);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                completion.onCompletion(favorite);
            }
        });

    }


    public void updateProfile(String password, String realname, String oldPassword, final ApiResponse<Boolean> completion) {

        String url = "owapi/user/update/";
        Map<String, String> params = new HashMap<String, String>();
        params.put("password", password);
        params.put("realname", realname);
        params.put("oldPassword", oldPassword);

        this.RequestApiPOST(url, params, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                if (res.success != null && res.success) {
                    Log.v("Profile Updated:", res.success.toString());
                } else {
                    Log.v("Profile NOT Updated", res.success.toString());
                }

                completion.onCompletion(res.success);

            }
        });

    }


    public void updateAvatar(File media, final ApiResponse<ApiResult> completion) {

        String url = "owapi/user/update/avatar";

        String uuid = UUID.randomUUID().toString();
        String boundary = "----------------------------"+uuid;
        String fileName = uuid+".jpg";
        Map<String,String> params = new HashMap<>();
        params.put("ajaxFunc", "ajaxUploadImage");
        params.put("pluginKey", "mailbox");

        this.RequestMultiPart(media, fileName, boundary, url, "file", params, new ApiResponse<String>() {
            @Override
            public void onCompletion(String result) {

                ApiResult res = new ApiResult();
                res.success = false;
                //Parse result
                String pattern = ".*updateItems\\((.+)\\);.*";
                String replacement = "$1";
                String processedStr = result.replaceAll(pattern, replacement);
                Log.d("ProcessedStr", processedStr);

                try {
                    JSONObject data = new JSONObject(processedStr);
                    res.success = data.getBoolean("success");
                    if (res.success) {
                        try {
                            res.data = data.getString("data");
                        } catch (JSONException x) {
                            x.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                completion.onCompletion(res);
            }
        });

    }


    /* Add Friends & Favorites Functions */

    public void isFriend(String friendId, final ApiResponse<Integer> completion) {

        String url = "owapi/user/isfriend/"+friendId;

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult result) {
                Integer isFriend = 0;
                 if (result.success && result.dataIsObject()) {
                     try {
                         JSONObject data = result.getDataAsObject();
                         String status = data.getString("isFriend");
                         if (status.equals("pending")) {
                             isFriend = 1;
                         } else if (status.equals("active")) {
                             isFriend = 2;
                         }
                         completion.onCompletion(isFriend);

                     } catch (JSONException x) {
                         completion.onCompletion(isFriend);
                     }
                 } else {
                     completion.onCompletion(-1);
                 }
            }
        });
    }


    public void friend(String friendId, String command, final ApiResponse<Boolean> completion) {

        String url = "owapi/user/friend/"+command+"/"+friendId;

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult result) {
                if (result.success && result.dataIsObject()) {
                    try {
                        JSONObject data = result.getDataAsObject();
                        String msg = data.getString("msg");
                        Log.i("Friend Command: ", msg);
                        completion.onCompletion(result.success);
                    } catch (JSONException x) {
                        completion.onCompletion(result.success);
                    }
                } else {
                    completion.onCompletion(result.success);
                }
            }
        });
    }


    public void isFavorite(String favoriteId, final ApiResponse<Boolean> completion) {

        String url = "owapi/user/isFavorite/"+favoriteId;

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult result) {
                Boolean isFavorite = false;
                if (result.success && result.dataIsObject()) {
                    try {
                        JSONObject data = result.getDataAsObject();
                        isFavorite = data.getBoolean("isFavorite");
                        completion.onCompletion(isFavorite);
                    } catch (JSONException x) {
                        completion.onCompletion(isFavorite);
                    }
                } else {
                    completion.onCompletion(isFavorite);
                }
            }
        });
    }


    public void favorite(String favoriteId, String command, final ApiResponse<Boolean> completion) {

        String url = "owapi/user/favorite/"+command+"/"+favoriteId;

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult result) {
                if (result.success && result.dataIsObject()) {
                    try {
                        JSONObject data = result.getDataAsObject();
                        String msg = data.getString("msg");
                        Log.i("Favorite Command: ", msg);
                        completion.onCompletion(result.success);
                    } catch (JSONException x) {
                        completion.onCompletion(result.success);
                    }
                } else {
                    completion.onCompletion(result.success);
                }
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
                    JSONArray messageList = new JSONArray();
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
                                    Log.v("MessageList", message.getAttachment().toString());
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

    public void messageUnread(String convId, String lastMessage, final ApiResponse<List<ModelMessage>> completion) {
        String url = "owapi/messenger/conversation/"+convId+"/unread/"+lastMessage;

        final List<ModelMessage> messages = new ArrayList<ModelMessage>();

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                Log.d("messageUnread ", res.data.toString());

                if (res.success && res.dataIsArray()) {
                    JSONArray messageList = res.getDataAsArray();
                    for (int i = 0; i < messageList.length(); i++) {

                        try {
                            JSONObject jsonObject = messageList.getJSONObject(i);
                            try {
                                ModelMessage message = new Gson().fromJson(jsonObject.toString(), ModelMessage.class);
                                if (message.getAttachment() != null){
                                    Log.v("MessageUnRead", message.getAttachment().toString());
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


    public void conversationCreate(final String initiatorId, String interlocutorId, final ApiResponse<ModelConversation> completion) {
        String url = "owapi/messenger/conversation/create";
        //here need to pass post params
        Log.v("url", url);

        Map<String, String> params = new HashMap<String, String>();
        params.put("initiatorId", initiatorId);
        params.put("interlocutorId", interlocutorId);

        this.RequestApiPOST(url, params, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                Log.v("GoogleRes", res.toString());
                ModelConversation conversation = new ModelConversation();
                if (res.success && res.dataIsObject()) {

                    JSONObject data = res.getDataAsObject();
                    conversation = new Gson().fromJson(data.toString(), ModelConversation.class);
                    try {
                        conversation.setId(data.getString("id"));
                        conversation.setOpponentId(data.getString("interlocutorId"));
                        conversation.setInitiatorId(data.getString("initiatorId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                completion.onCompletion(conversation);
            }

        });


    }


    public void conversationHistory(String conversationId, String lastMessageId, final ApiResponse<List<ModelMessage>> completion) {

        String url = "owapi/messenger/conversation/"+conversationId+"/history/"+lastMessageId;

        final List<ModelMessage> messages = new ArrayList<ModelMessage>();

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                if (res.data != null) {
                    Log.d("Success MessHist", res.data.toString());
                }

                if (res.success && res.dataIsArray()) {
                    JSONArray messageList = res.getDataAsArray();

                    Log.d("MessageList", messageList.toString());
                    for (int i = 0; i < messageList.length(); i++) {

                        try {
                            JSONObject jsonObject = messageList.getJSONObject(i);
                            try {
                                ModelMessage message = new Gson().fromJson(jsonObject.toString(), ModelMessage.class);
                                if (!message.getAttachment().equals("")) {
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
                            } catch (Exception e) {
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



    public void messageSend(String convId, String messageText, final ApiResponse<ModelMessage> completion) {

        String url = "owapi/messenger/conversation/"+convId+"/send/";
        Map<String, String> params = new HashMap<String, String>();
        params.put("text", messageText);

        this.RequestApiPOST(url, params, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                if (res.success) {
                    Log.v("VicRes", res.toString());
                    ModelMessage message = new ModelMessage();
                    message = new Gson().fromJson(res.data.toString(), ModelMessage.class);
                    completion.onCompletion(message);

                } else {
                    completion.onCompletion(null);
                }
            }
        });

    }

    public void messageSendWithMedia(final String convId, String opponentId, final String lastMessage, File media, final ApiResponse<List<ModelMessage>> completion) {

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        String bundle = "mailbox_dialog_"+convId+"_"+opponentId+"_"+ts;
        String url = "base/attachment/add-file/?flUid="+bundle;

        String uuid = UUID.randomUUID().toString();
        String boundary = "----------------------------"+uuid;
        String fileName = uuid+".jpg";
        String mediaName = "{"+fileName+":1}";//Utils.urlEncode( "{\"\(uuid).jpg\":1}" )
        Map<String,String> params = new HashMap<>();
        params.put("flData", mediaName);
        params.put("flUid", bundle);
        params.put("pluginKey", "mailbox");

        this.RequestMultiPart(media, fileName, boundary, url, "ow_file_attachment[]", params, new ApiResponse<String>() {
            @Override
            public void onCompletion(String result) {

                //Parse result
                String pattern = ".*updateItems\\((.+)\\);.*";
                String replacement = "$1";
                String processedStr = result.replaceAll(pattern, replacement);
                Log.d("ProcessedStr", processedStr);

                List<ModelMessage> messages = new ArrayList<ModelMessage>();

                try {

                    JSONObject data = new JSONObject(processedStr);
                    Boolean success = data.getBoolean("result");
                    if (success) {

                        //Here query unread messages and return it in completion res
                        messageUnread(convId, lastMessage, new ApiResponse<List<ModelMessage>>() {

                            @Override
                            public void onCompletion(List<ModelMessage> messages) {
                                completion.onCompletion(messages);
                            }

                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    /* LOCATION */

    public void updateLocation(String json, final ApiResponse<Boolean> completion) {

        String url = "owapi/user/updateLocation/";

        Map<String, String> params = new HashMap<String, String>();
        params.put("json", json);

        this.RequestApiPOST(url, params, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                //TODO: check why it sends it twice
                completion.onCompletion(res.success);

            }
        });

    }


    public void nearUsers(final ApiResponse<List<ModelUser>> completion) {

        String url = "owapi/user/near/";

        final List<ModelUser> users = new ArrayList<ModelUser>();

        this.RequestApi(url, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {
                Log.d("Success NearUsers", res.data.toString());

                if (res.success && res.dataIsArray()) {

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
                completion.onCompletion(users);
            }
        });
    }


    //Request address from Google API with lat/lng
    public void httpRequestGeocode(String lat, String lng, final ApiResponse<String> completion) {

        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lng+"&language=fr&key=AIzaSyDd2dDC7nZbtckZHE1diYiKsAwJweNjU_Q";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, (JSONObject) null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //Log.v("RequestApi Response", response.toString());
                    String res = "";
                    try {
                        JSONArray results = response.getJSONArray("results");
                        try {
                           res  =  results.getJSONObject(0).toString();
                        } catch (JSONException e) {
                            Log.v("exception catch", e.getMessage());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    completion.onCompletion(res);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ApiResult res = new ApiResult();
                    completion.onCompletion("");
                }
            }
        );

        //Added because of Login ERROR﹕ error => com.android.volley.TimeoutError
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));

        AppController.getInstance().addToRequestQueue(jsonRequest);

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

                if (res.success != null && res.success) {
                    Log.v("GCM registration:", res.success.toString());
                    res.message = "GCM registration successful";
                }

                completion.onCompletion(res.message);

            }
        });

    }


    /* LOGIN METHODS */

    public void setSession(final ApiResponse<Boolean> completion) {

        final ApiResult res = new ApiResult();

        session = AppController.getSession();
        session.setLogin(true);

        //Here request user and save it in a local var (or in sqlite), then in completion save email and password in sqlite
        currentUser(new ApiResponse<ModelUser>() {
            @Override
            public void onCompletion(ModelUser user) {
                if (user != null) {
                    res.success = true;
                    AppController.getInstance().setLoggedInUser(user);
                    ModelUser currentUser = AppController.getInstance().getLoggedInUser();
                    Log.v("Global var created", currentUser.toString());

                } else {
                    res.success = false;
                }
                completion.onCompletion(res.success);
            }
        });

    }

    public void login(final String username, final String password, final ApiResponse<ApiResult> completion) {

        //session = new SessionManager(AppController.getContext());
        session = AppController.getSession();

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
                                session.setLoginType(1);
                                session.setLoginInfo(username, password);
                                Boolean isUserLoggedIn = session.isLoggedIn();
                                Log.v("SessionSetUserLoggedIn", isUserLoggedIn.toString());
                                //Here request user and save it in a local var (or in sqlite), then in completion save email and password in sqlite
                                currentUser(new ApiResponse<ModelUser>() {
                                    @Override
                                    public void onCompletion(ModelUser user) {
                                        if(user != null){
                                            AppController.getInstance().setLoggedInUser(user);
                                            Log.v("Global var created",user.toString());
                                            completion.onCompletion(res);
                                        } else {
                                            res.success = false;
                                            res.message = "Cannot retrieve user info after login";
                                            completion.onCompletion(res);
                                        }
                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            res.success = false;
                            res.message = "Error during login";
                            completion.onCompletion(res);
                        }


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



    public void logout( final ApiResponse<Boolean> completion) {

        String url = hostname+"sign-out";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        completion.onCompletion(true);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("Login ERROR","error => "+error.toString());
                        completion.onCompletion(false);
                    }
                }
        ) {

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



    /* FB CONNECT */

    public void fbConnect(String token, final ApiResponse<Boolean> completion) {

        String url = "owapi/site/fbconnect/";
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);

        this.RequestApiPOST(url, params, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                Log.v("FBRes", res.toString());
                if (res.success) {
                    setSession(new ApiResponse<Boolean>() {
                        @Override
                        public void onCompletion(Boolean result) {
                            completion.onCompletion(result);
                        }
                    });

                } else {
                    completion.onCompletion(false);
                }
            }
        });

    }


    /* GOOGLE CONNECT */
    public void googleConnect(String token, final ApiResponse<Boolean> completion) {

        String url = "owapi/site/glconnect/";
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("kind", "android");

        this.RequestApiPOST(url, params, new ApiResponse<ApiResult>() {
            @Override
            public void onCompletion(ApiResult res) {

                if (res.success) {
                    //Log.v("GoogleRes", res.toString());
                    setSession(new ApiResponse<Boolean>() {
                        @Override
                        public void onCompletion(Boolean result) {
                            completion.onCompletion(result);
                        }
                    });

                } else {
                    completion.onCompletion(false);
                }
            }
        });

    }




}


