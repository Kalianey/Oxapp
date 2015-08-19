package com.kalianey.oxapp.views.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kalianey.oxapp.R;
import com.kalianey.oxapp.utils.SessionManager;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.views.activities.MainActivity;
import com.kalianey.oxapp.views.activities.People;

import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class SignInFragment extends Fragment {

    private EditText email;
    private EditText password;
    private Text noAccount;
    private Button signInButton;

    private QueryAPI query = new QueryAPI();
    private ProgressDialog pDialog;
    private SessionManager session;

    public SignInFragment() {
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
                if (session.isLoggedIn()) {
                    // User is already logged in. Take him to main activity
                    Intent intent = new Intent(getActivity(), People.class);
                    startActivity(intent);
                    //finish();
                }

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
}
