package com.kalianey.oxapp.views.fragments;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.SessionManager;
import com.kalianey.oxapp.views.activities.MainActivity;

import org.w3c.dom.Text;

import java.io.IOException;

//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

/**
 * For google sign in try to follow : https://gist.github.com/ianbarber/9607551
 * Official doc for the picker : https://developers.google.com/android/guides/http-auth
 */
public class SignInFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener  {

    private EditText email;
    private EditText password;
    private TextView registerTextView;
    private TextView mTermsTextView;
    private Button signInButton;
    private ProgressDialog pDialog;
    private SessionManager session;
    private QueryAPI query = new QueryAPI();

    private static final String TAG = "SignInFragment";

    //FB
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private LoginButton fbLoginButton;
    private Button fbCustomButton;

    private String id;
    private String name;
    private String gender;
    private String emailFB;
    private Object birthday;

    //GOOGLE
     /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;
    private String mAccountName;
    private static final String CLIENT_ID = "645184786563-95fhqa4mqa2m2s8bdq36ki1edr7ij192.apps.googleusercontent.com";
    private static final int REQ_SIGN_IN_REQUIRED = 55664;
    private String accessToken;

    public SignInFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Session manager
        session = AppController.getSession();

        //FB
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        //GOOGLE
        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //.addApi(Plus.API)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(new Scope(Scopes.PROFILE))
                .build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        signInButton = (Button) view.findViewById(R.id.signInButton);
        email = (EditText) view.findViewById(R.id.emailInput);
        email.getBackground().mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        password = (EditText) view.findViewById(R.id.passwordInput);
        password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        registerTextView = (TextView) view.findViewById(R.id.register);
        String txt_links = "Don't have an account? Register with Facebook or Google below, <a href='"+AppController.getHostname()+"join'> or on the web</a>";
        registerTextView.setLinksClickable(true);
        registerTextView.setMovementMethod(LinkMovementMethod.getInstance());
        registerTextView.setText(Html.fromHtml(txt_links));
        mTermsTextView = (TextView) view.findViewById(R.id.termsText);
        String str_links = "By continuing, I agree to the <a href='"+AppController.getHostname()+"conditions-d-utilisation'>Terms & Conditions</a>";
        mTermsTextView.setLinksClickable(true);
        mTermsTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mTermsTextView.setText(Html.fromHtml(str_links));

        // Check if user is already logged in
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }

        //Normal Sign In Button clicked
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                signInButton.setEnabled(false);
                email.setEnabled(false);
                password.setEnabled(false);

                // Progress dialog
                pDialog = new ProgressDialog(getActivity());
                pDialog.setCancelable(false);

                //We check that the fields are not empty and perform the login
                if (!emailText.isEmpty() && !passwordText.isEmpty()) {

                    query.login(emailText, passwordText, new QueryAPI.ApiResponse<QueryAPI.ApiResult>() {
                        @Override
                        public void onCompletion(QueryAPI.ApiResult result) {

                            if (result.success) {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Error: "+ result.message, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG).show();
                }

            }
        });

        return view;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //FB
        fbLoginButton = (LoginButton) view.findViewById(R.id.login_button);
        fbCustomButton = (Button) view.findViewById(R.id.fb_button);
        fbCustomButton.setOnClickListener(this);
        fbLoginButton.setReadPermissions("user_friends");
        fbLoginButton.setFragment(this);
        fbLoginButton.registerCallback(callbackManager, callback);

        Profile profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile != null) {
            fbCustomButton.setText("Log out from facebook");

        } else {
            // user has not logged in
            fbCustomButton.setText("Sign in with facebook");
        }

        //Google
        Button glSignInButton = (Button) view.findViewById(R.id.sign_in_button);
        glSignInButton.setOnClickListener(this);

    }

    /*** FB CONNECT METHODS ***/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != Activity.RESULT_OK) {
                mAccountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }


    }

    private void displayMessage(Profile profile){
        if(profile != null){
            Log.i(TAG +" login ok", profile.getName());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
        mGoogleApiClient.disconnect();

    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);
    }

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            final String token = loginResult.getAccessToken().getToken();
            Log.i(TAG + " token: ", token);

            if (token != null) {
                query.fbConnect(token, new QueryAPI.ApiResponse<Boolean>() {
                    @Override
                    public void onCompletion(Boolean result) {
                        Profile profile = Profile.getCurrentProfile();
                        displayMessage(profile);

                        if (result) {
                            session.setLoginType(2);
                            session.setToken(token);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };


    /*** GOOGLE SIGN IN METHODS ***/

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.

        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        mAccountName = Plus.AccountApi.getAccountName(mGoogleApiClient);

        new RetrieveTokenTask().execute(mAccountName);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }

        if (v == fbCustomButton) {
            fbLoginButton.performClick();
        }
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //.addApi(Plus.API)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
        //mStatusTextView.setText(R.string.signing_in);
        Log.i("GoogleSignIn", "in progress");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    //Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                Toast.makeText(getActivity().getApplicationContext(), connectionResult.toString(), Toast.LENGTH_LONG);
                //showErrorDialog(connectionResult);
            }
        }
    }


    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            //String scopes = "oauth2:profile email";
            String scopes = "oauth2:"
                    + Scopes.PLUS_LOGIN
                    + " "
                    + Scopes.PROFILE;
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getActivity().getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
                Log.e(TAG, e.getMessage());
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            super.onPostExecute(token);


            //TODO : access token verifier https://developers.google.com/identity/sign-in/android/backend-auth
            //http://android-developers.blogspot.ca/2013/01/verifying-back-end-calls-from-android.html
//            Log.i("Token JWT", accessToken);
//            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
//                    .setAudience(Arrays.asList(CLIENT_ID))
//                    .build();
//            // (Receive idTokenString by HTTPS POST)
//            GoogleIdToken idToken = null;
//            try {
//                idToken = verifier.verify(accessToken);
//            } catch (GeneralSecurityException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (idToken != null) {
//                Payload payload = idToken.getPayload();
//                if (payload.getHostedDomain().equals(APPS_DOMAIN_NAME)
//                        // If multiple clients access the backend server:
//                        && Arrays.asList(ANDROID_CLIENT_ID, IOS_CLIENT_ID).contains(payload.getAuthorizedParty())) {
//                    System.out.println("User ID: " + payload.getSubject());
//                } else {
//                    System.out.println("Invalid ID token.");
//                }
//            } else {
//                System.out.println("Invalid ID token.");
//            }

            if (token != "" && token != null) {
                Log.i("Token Value: ", token);
                accessToken = token;
                //Make the app crash because of debug answer from API
                query.googleConnect(accessToken, new QueryAPI.ApiResponse<Boolean>() {
                    @Override
                    public void onCompletion(Boolean result) {
                        if (result) {
                            session.setLoginType(3);
                            session.setToken(accessToken);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });

            }

        }
    }

}


