package com.kalianey.oxapp.utils;

import android.app.Application;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

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
import java.util.List;

/**
 * Created by kalianey on 10/08/2015.
 */
public class AppController extends Application implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

    private static Context mContext;

    private static SessionManager mSession;

    private static String appName;

    private static String hostname;

    private ModelUser loggedInUser;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    //Location
    private LocationManager locationManager;
    private String provider;

    private Double mCurrentUserLat;
    private Double mCurrentUserLng;

    private QueryAPI mQuery;


    @Override
    public void onCreate() {

        super.onCreate();

        mInstance = this;
        mContext = getApplicationContext();
        mSession = new SessionManager(mContext);

        mQuery = new QueryAPI();

        loggedInUser = new ModelUser();
        appName = mContext.getResources().getString(R.string.app_name);
        hostname = "http://bonnieandclit.com/";

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

        //LOCATION
        locationManager = (LocationManager) getSystemService(mContext.LOCATION_SERVICE);
        // new Criteria() is left empty as it will automatically get the default one, but can be specified
        provider = locationManager.getBestProvider(new Criteria(), false);

        // get last location of the user
        mCurrentUserLat = 48.8567; //default to Paris
        mCurrentUserLng = 2.3508;
        Location location = getLastKnownLocation();
        if (location == null) {
            Log.i("Location achieved: ", "No");
            if ((getLoggedInUser().getLat() != 0.0) && (getLoggedInUser().getLng() != 0.0)) {
                mCurrentUserLat = getLoggedInUser().getLat();
                mCurrentUserLng = getLoggedInUser().getLng();
            }
        } else {
            Log.i("Location achieved: ", "Yes");
            mCurrentUserLat = location.getLatitude();
            mCurrentUserLng = location.getLongitude();
        }
        Log.i("User Latitude: ", mCurrentUserLat.toString());
        Log.i("User Longitude: ", mCurrentUserLng.toString());

    }

    public ModelUser getLoggedInUser() {
        return loggedInUser;
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public static SessionManager getSession() { return mSession; };

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public String getProvider() {
        return provider;
    }

    public Double getCurrentUserLat() {
        return mCurrentUserLat;
    }

    public Double getCurrentUserLng() {
        return mCurrentUserLng;
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

    public static String getHostname() {
        return hostname;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mContext;
    }



    /*** VOLLEY URL REQUEST MANAGER ***/

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

    /*** FACEBOOK SIGN IN ***/


    /*** GOOGLE SIGN IN ***/
    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    /*** LOCATION FUNCTIONS ***/

    public void updateLocation() {
        locationManager.requestLocationUpdates(provider, 300000, 1000, (LocationListener) mContext);
    }

    public void stopUpdateLocation() {
        locationManager.removeUpdates((LocationListener) mContext);
    }

    @Override
    public void onLocationChanged(Location location) {

        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        Log.i("Longitude", lng.toString());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }


}
