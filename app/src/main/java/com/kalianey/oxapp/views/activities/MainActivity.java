package com.kalianey.oxapp.views.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.menu.ResideMenu;
import com.kalianey.oxapp.menu.ResideMenuItem;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.SessionManager;
import com.kalianey.oxapp.views.fragments.AccountFragment;
import com.kalianey.oxapp.views.fragments.ConversationListFragment;
import com.kalianey.oxapp.views.fragments.PeopleFragment;
import com.kalianey.oxapp.views.fragments.ProfileFragment;

import java.io.IOException;

public class MainActivity  extends AppCompatActivity implements View.OnClickListener {

    private SessionManager session;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView navList;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private QueryAPI query= new QueryAPI();

    private ResideMenu resideMenu;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemConversations;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemFriends;
    private ResideMenuItem itemFav;
    private ResideMenuItem itemAccount;

    //Action Bar
    android.support.v7.app.ActionBar mActionBar;
    private TextView mTitleTextView;

    // GCM
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    /**
     * Substitute you own project number here. This project number comes
     * from the Google Developers Console.
     */
    public static final String PROJECT_NUMBER = "645184786563";
    private GoogleCloudMessaging mGcm;

    //GOOGLE SIGN IN
    private GoogleApiClient mGoogleApiClient;

    private Boolean error;

    private LocationManager mLocationManager;


    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);

        showActionBar();

        if(Build.VERSION.SDK_INT >= 21){
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_main);
        setUpMenu();
        setPaddings();

        session = AppController.getSession();

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            silentLogin();
        }
        else {

            //Location
            mLocationManager = AppController.getInstance().getLocationManager();
            AppController.getInstance().updateLocation();
            query.httpRequestGeocode(AppController.getInstance().getCurrentUserLat().toString(), AppController.getInstance().getCurrentUserLng().toString(), new QueryAPI.ApiResponse<String>() {
                @Override
                public void onCompletion(String address) {

                    query.updateLocation(address, new QueryAPI.ApiResponse<Boolean>() {
                        @Override
                        public void onCompletion(Boolean result) {
                            if (result == true) {
                                Log.i("Location ", "successfully updated");
                            } else {
                                Log.i("Location", " not updated");
                            }
                        }
                    });

                }
            });

            //Load Main Fragment
            changeFragment(new PeopleFragment());
        }



        //We check if user has GooglePlayServices as they are needed for the app // TODO: check how it works and implement
        //we replace getApplicationContext() by this
//        if (checkPlayServices()) {
//            mGcm = GoogleCloudMessaging.getInstance(this);
//            String regId = getRegistrationId(this);
//
//            if (PROJECT_NUMBER.equals("")) {
//                new AlertDialog.Builder(this)
//                        .setTitle("Needs Project Number")
//                        .setMessage("GCM will not function until you set the Project Number to the one from the Google Developers Console.")
//                        .setPositiveButton(android.R.string.ok, null)
//                        .create().show();
//            } else if (regId.isEmpty()) {
//                registerInBackground(this);
//            }
//        } else {
//            Log.i(LOG_TAG, "No valid Google Play Services APK.");
//            // Store regID as null
//            storeRegistrationId(this, null);
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO
        // If Google Play Services is not available, some features, such as GCM-powered weather
        // alerts, will not be available.
        if (!checkPlayServices()) {
            // Store regID as null
        }

        // FB: Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

        //Location : request updates every 10 minutes & 1km
        AppController.getInstance().updateLocation();

    }

    @Override
    protected void onPause() {
        super.onPause();

        // FB : Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);

        AppController.getInstance().stopUpdateLocation();
    }


    private void silentLogin() {

        error = false;

        //we check if there are some silent login info stored
        if (session.getLoginType() != 0) {

            String token = session.getToken();

            switch(session.getLoginType()) {

                //Fb SignIn
                case 2:
                    if (token != "") {
                        query.fbConnect(token, new QueryAPI.ApiResponse<Boolean>() {
                            @Override
                            public void onCompletion(Boolean result) {
                                if (result) {
                                    Log.v("Silent FB Login: ", result.toString());
                                    //Load first menu item by default
                                    changeFragment(new PeopleFragment());
                                } else {
                                    error = true;
                                }
                            }
                        });
                    } else {
                        error = true;
                    }
                    break;

                //Google SignIn
                case 3:
                    if (token != "") {
                        query.googleConnect(token, new QueryAPI.ApiResponse<Boolean>() {
                            @Override
                            public void onCompletion(Boolean result) {
                                if (result) {
                                    Log.v("Silent Google Login: ", result.toString());
                                    //Load first menu item by default
                                    changeFragment(new PeopleFragment());
                                } else {
                                    error = true;
                                }
                            }
                        });
                    } else {
                        error = true;
                    }
                    break;

                //Normal Sign In
                default:
                    if (session.getUsername() != "" && session.getPassword() != "") {
                        query.login(session.getUsername(), session.getPassword(), new QueryAPI.ApiResponse<QueryAPI.ApiResult>() {
                            @Override
                            public void onCompletion(QueryAPI.ApiResult res) {

                                if (res.success) {
                                    Log.v("Silent Normal Login: ", res.success.toString());
                                    //Load first menu item by default
                                    changeFragment(new PeopleFragment());
                                }
                            }
                        });
                    } else {
                        error = true;
                    }
            }

            //if an error occurred, , we redirect the user to the sign in page
            if (error == true) {
                Log.d("Silent logged In failed", "");
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }

        }

        //if no user info are stored, we redirect the user to the sign in page
        else {
            Log.d("PeopleUserLoggedIn", "false");
            Intent intent = new Intent(MainActivity.this, SignIn.class);
            startActivity(intent);
        }

    }

    private void setUpMenu() {

        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setShadowVisible(true);
        resideMenu.setHeaderView(findViewById(R.id.actionbar));
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        itemHome     = new ResideMenuItem(this, R.drawable.ic_home,     "People");
        itemConversations  = new ResideMenuItem(this, R.drawable.icons_chat,  "Messages");
        itemProfile = new ResideMenuItem(this, R.drawable.ic_list_2, "Profile");
        itemFriends = new ResideMenuItem(this, R.drawable.ic_list_1, "Friends");
        itemFav = new ResideMenuItem(this, R.drawable.icons_star, "Favorites");
        itemAccount = new ResideMenuItem(this, R.drawable.exit, "Account");

        itemHome.setOnClickListener(this);
        itemConversations.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        itemFriends.setOnClickListener(this);
        itemFav.setOnClickListener(this);
        itemAccount.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome);
        resideMenu.addMenuItem(itemConversations);
        resideMenu.addMenuItem(itemProfile);
        resideMenu.addMenuItem(itemFriends);
        resideMenu.addMenuItem(itemFav);
        resideMenu.addMenuItem(itemAccount);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {

        if (view == itemHome){
            changeFragment(new PeopleFragment());
            mTitleTextView.setText("Bonnie&Clit");
            mActionBar.show();
        }
        else if (view == itemConversations){
            changeFragment(new ConversationListFragment());
            mTitleTextView.setText("Messages");
            mActionBar.show();
        }
        else if (view == itemProfile){
            ProfileFragment profile = new ProfileFragment();
            profile.setUser(AppController.getInstance().getLoggedInUser());
            changeFragment(profile);
            mActionBar.hide();
//            mActionBar.setDisplayShowTitleEnabled(false);
//            mActionBar.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        else if (view == itemFriends){
            //changeFragment(new FriendsListFragment());
            changeFragment(new FriendActivityFragment());
            mTitleTextView.setText("Friends");
            mActionBar.show();
        }
        else if (view == itemFav){
            changeFragment(new FavoriteActivityFragment());
            mTitleTextView.setText("Favorites");
            mActionBar.show();
        }
        else if (view == itemAccount){
            AccountFragment account = new AccountFragment();
            account.setUser(AppController.getInstance().getLoggedInUser());
            changeFragment(account);
            mActionBar.hide();
        }

        resideMenu.closeMenu();
    }

    //Example of menuListener
    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() { }

        @Override
        public void closeMenu() { }
    };

    private void changeFragment(Fragment targetFragment){
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    //return the residemenu to fragments
    public ResideMenu getResideMenu(){
        return resideMenu;
    }

    @Override
    public void onBackPressed() {
        if (resideMenu.isOpened()){
            resideMenu.closeMenu();
        } else {
            resideMenu.openMenu();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setPaddings();
    }

    /**
     * Due to a bug in Lollipop/Material theme the navigationbar overlaps the layout.
     * We fix this by manually adding padding to our main view, and (scrollable)menu view.
     */
    private void setPaddings() {
        if (Build.VERSION.SDK_INT >= 21) {

            int bottomPadding = 0;
            int rightPadding = 0;
            // int topPadding = insets.top;
            int leftPadding = 0;

            int size = getNavBarHeight(this);
            if (size > 0 && !isTablet(this)) {
                WindowManager manager = (WindowManager) this
                        .getSystemService(Context.WINDOW_SERVICE);
                switch (manager.getDefaultDisplay().getRotation()) {
                    case Surface.ROTATION_90:
                        rightPadding += size;
                        break;
                    case Surface.ROTATION_180:
                        // topPadding += size;
                        break;
                    case Surface.ROTATION_270:
                        rightPadding += size;
                        break;
                    default:
                        bottomPadding += size;
                }
            } else if (size > 0) {
                // on tablets, the navigationbar is always at the bottom.
                bottomPadding += size;
            }

            View scrollViewMenu = findViewById(R.id.sv_left_menu);
            scrollViewMenu.setPadding(scrollViewMenu.getPaddingLeft(), 0 +
                    getStatusBarHeight(),scrollViewMenu.getPaddingRight(),scrollViewMenu.getPaddingBottom());

            View menu = findViewById(R.id.menu);
            menu.setPadding(0 + leftPadding,
                    menu.getPaddingTop(), 0 + rightPadding,
                    0 + bottomPadding);
            View main = findViewById(R.id.main);
            main.setPadding(0 + leftPadding,
                    main.getPaddingTop(), 0 + rightPadding,
                    0 + bottomPadding);
        }
    }

    @SuppressLint("NewApi")
    public int getNavBarHeight(Context c) {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey(); //method is only called on API21+
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if(!hasMenuKey && !hasBackKey) {
            //The device has a navigation bar
            Resources resources = getResources();

            int orientation = getResources().getConfiguration().orientation;
            int resourceId;
            if (isTablet(c)){
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
            }  else {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_width", "dimen", "android");
            }

            if (resourceId > 0) {
                return getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    private boolean isTablet(Context c) {
        return (c.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    //Custom action bar

    private void showActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText("Bonnie&Clit");

        ImageButton menuButton = (ImageButton) mCustomView
                .findViewById(R.id.menu);
        menuButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (resideMenu.isOpened()) {
                    resideMenu.closeMenu();
                } else {
                    resideMenu.openMenu();
                }
            }
        });

        ImageButton mapButton = (ImageButton) mCustomView
                .findViewById(R.id.map);
        mapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
            }
        });


        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

    }



    /************************** GCM ********************************************************************************************/

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(LOG_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(LOG_TAG, "GCM Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(LOG_TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // Sunshine persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you. Just make sure
        // that it is private!
        return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // Should never happen. WHAT DID YOU DO?!?!
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground(final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String msg = "";
                try {
                    if (mGcm == null) {
                        mGcm = GoogleCloudMessaging.getInstance(context);
                    }
                    String regId = mGcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regId;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    //sendRegistrationIdToBackend();
                    query.registerForNotifications(regId, new QueryAPI.ApiResponse<String>() {
                        @Override
                        public void onCompletion(String result) {

                        }
                    });

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // TODO: If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return null;
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(LOG_TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }


}
