package com.kalianey.oxapp.utils;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelUser;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 * Created by kalianey on 10/08/2015.
 */
public class AppController extends Application implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

    private static Context mContext;

    private static String appName;

    private ModelUser loggedInUser;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {

        super.onCreate();

        mInstance = this;
        mContext = getApplicationContext();
        loggedInUser = new ModelUser();
        appName = mContext.getResources().getString(R.string.app_name);

        //FBK
        FacebookSdk.sdkInitialize(getApplicationContext());

        //GOOGLE
        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();


    }

    public ModelUser getLoggedInUser() {
        return loggedInUser;
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setLoggedInUser(ModelUser user) {
        this.loggedInUser = user;
    }

    public static String getAppName() {
        return appName;
    }

    public static void setAppName(String appName) {
        AppController.appName = appName;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mContext;
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            CookieManager manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    // GOOGLE SIGN IN
    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
