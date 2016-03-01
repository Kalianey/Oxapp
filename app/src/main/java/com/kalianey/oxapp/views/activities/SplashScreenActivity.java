package com.kalianey.oxapp.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.SessionManager;
import com.kalianey.oxapp.views.fragments.PeopleFragment;

/**
 * Created by kalianey on 01/03/2016.
 */
public class SplashScreenActivity extends AppCompatActivity {


    private SessionManager session;
    private QueryAPI query= new QueryAPI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = AppController.getSession();

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            silentLogin();
        }
        else {
            dismissSplashScreen();
        }

    }

    private void dismissSplashScreen() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void silentLogin() {

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
                                    dismissSplashScreen();
                                } else {
                                    loginFailed();
                                }
                            }
                        });
                    } else {
                        loginFailed();
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
                                    dismissSplashScreen();
                                } else {
                                    loginFailed();
                                }
                            }
                        });
                    }
                    else {
                        loginFailed();
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
                                    dismissSplashScreen();
                                }
                                else {
                                    loginFailed();
                                }
                            }
                        });
                    }
                    else {
                        loginFailed();
                    }
            }

        }
        //if no user info are stored, we redirect the user to the sign in page
        else {
            loginFailed();
        }

    }

    private void loginFailed () {
        Log.d("Silent logged In failed", " on MainActivity");
        Intent intent = new Intent(getApplicationContext(), SignIn.class);
        startActivity(intent);
    }
}
