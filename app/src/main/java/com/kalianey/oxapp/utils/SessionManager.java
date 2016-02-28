package com.kalianey.oxapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by kalianey on 11/08/2015.
 */
public class SessionManager {

    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "OxappFile";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    //Initialization
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /*** Storing and retrieving data ***/

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        // commit changes
        editor.commit();
        Log.d(TAG, "User is logged in!");
    }

    //Check user login status
    public boolean isLoggedIn(){
        Boolean isLoggedIn = false;
        if (AppController.getInstance().getLoggedInUser().getUserId() != null) {
            isLoggedIn = true;
        }
        return isLoggedIn;
    }

    public void setLoginType(int loggedInType) {
        editor.putInt("loginType", loggedInType);
        // commit changes
        editor.commit();
    }

    public Integer getLoginType() {
        Integer loginType = pref.getInt("loginType", 0);
        return loginType;
    }

    public void setLoginInfo(String username, String password) {

        editor.putString("username", username);
        editor.putString("password", password);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login info modified!");
    }

    public String getUsername() {
        String username = pref.getString("username", "");
        return username;
    }

    public String getPassword() {
        String password = pref.getString("password", "");
        return password;
    }

    public void setToken(String token) {
        editor.putString("token", token);
        editor.commit();
    }

    public String getToken(){
        String token = pref.getString("token", "");
        return token;
    }




}
