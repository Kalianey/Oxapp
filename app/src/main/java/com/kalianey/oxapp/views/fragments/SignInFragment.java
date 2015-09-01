package com.kalianey.oxapp.views.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.utils.SessionManager;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.views.activities.MainActivity;

import org.w3c.dom.Text;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

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
    private Text noAccount;
    private Button signInButton;
    private ProgressDialog pDialog;
    private SessionManager session;
    private QueryAPI query = new QueryAPI();

    private static final String TAG = "SignInFragment";

    //FB
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

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
    private String accessToken;

    public SignInFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

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
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);




        signInButton = (Button) view.findViewById(R.id.signInButton);
        email = (EditText) view.findViewById(R.id.emailInput);
        password = (EditText) view.findViewById(R.id.passwordInput);

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

                // Session manager
                session = new SessionManager(getActivity().getApplicationContext());

                // Check if user is already logged in or not
//                if (session.isLoggedIn()) {
//                    // User is already logged in. Take him to main activity
//                    Intent intent = new Intent(getActivity(), MainActivity.class);
//                    startActivity(intent);
//                    //finish();
//                }

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
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);

        //Google
        SignInButton signInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, callback);

    }

    // FB CONNECT METHODS

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
            String token = loginResult.getAccessToken().getToken();
            Log.i(TAG + " token: ", token);

            if (token != null) {
                query.fbConnect(token, new QueryAPI.ApiResponse<Boolean>() {
                    @Override
                    public void onCompletion(Boolean result) {
                        Profile profile = Profile.getCurrentProfile();
                        displayMessage(profile);

                        if (result) {
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


    //GOOGLE SIGN IN METHODS

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        mAccountName = Plus.AccountApi.getAccountName(mGoogleApiClient);

        new RetrieveTokenTask().execute(mAccountName);

        // Show the signed-in UI
//        Intent intent = new Intent(getActivity(), MainActivity.class);
//        startActivity(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
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
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                Toast.makeText(getActivity().getApplicationContext(), connectionResult.toString(), Toast.LENGTH_LONG);
                //showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }


    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getActivity().getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                //startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            super.onPostExecute(token);
            Log.i("Token Value: ", token);
            //TODO : access token verifier https://developers.google.com/identity/sign-in/android/backend-auth
            //http://android-developers.blogspot.ca/2013/01/verifying-back-end-calls-from-android.html

            accessToken = token;

//            Log.i("Token JWT", accessToken);

//            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
//                    .setAudience(Arrays.asList(CLIENT_ID))
//                    .build();
//
//            // (Receive idTokenString by HTTPS POST)
//
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


            //Make the app crash because of debug answer from API
//            query.googleConnect(accessToken, new QueryAPI.ApiResponse<Boolean>() {
//                @Override
//                public void onCompletion(Boolean result) {
//                    if (result) {
//                        Intent intent = new Intent(getActivity(), MainActivity.class);
//                        startActivity(intent);
//                    }
//                }
//            });

        }
    }

}


