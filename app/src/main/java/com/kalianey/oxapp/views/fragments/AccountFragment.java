package com.kalianey.oxapp.views.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.menu.ResideMenu;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.UICircularImage;
import com.kalianey.oxapp.views.activities.MainActivity;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class AccountFragment extends Fragment {

    //Configuration
    private ModelUser user;
    QueryAPI query = new QueryAPI();
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    //UI Elements
    private View view;
    private FrameLayout mNavigationTop;
    private TextView mNavigationTitle;
    private Button mNavigationBackBtn;
    private NetworkImageView mBackgroundImageView;
    private UICircularImage mAvatarView;
    private Button mEditAvatar;
    private TextView mLabelName;
    private EditText mEditName;
    private TextView mLabelEmail;
    private EditText mEditEmail;
    private TextView mLabelPassword;
    private EditText mEditPassword;
    private TextView mTermsTextView;
    private Button mLogoutButton;
    private Button mSaveButton;

    //Var
    private String newPassword;

    public AccountFragment() {
    }

    public ModelUser getUser() {
        return user;
    }

    public void setUser(ModelUser user) {
        this.user = user;
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);

        //Get the views
        mNavigationTop = (FrameLayout) view.findViewById(R.id.layout_top);
        mNavigationTitle = (TextView) view.findViewById(R.id.titleBar);
        mNavigationBackBtn = (Button) view.findViewById(R.id.title_bar_left_menu);
        mBackgroundImageView = (NetworkImageView) view.findViewById(R.id.mBackgroundImageView);
        mAvatarView = (UICircularImage) view.findViewById(R.id.mImageView);
        mEditAvatar = (Button) view.findViewById(R.id.editAvatar);
        mLabelName = (TextView) view.findViewById(R.id.nameTextView);
        mEditName = (EditText) view.findViewById(R.id.nameEditText);
        mLabelEmail = (TextView) view.findViewById(R.id.emailTextView);
        mEditEmail = (EditText) view.findViewById(R.id.emailEditText);
        mLabelPassword = (TextView) view.findViewById(R.id.passwordTextView);
        mEditPassword = (EditText) view.findViewById(R.id.passwordEditText);
        mTermsTextView = (TextView) view.findViewById(R.id.termsTextView);
        mLogoutButton = (Button) view.findViewById(R.id.logoutButton);
        mSaveButton = (Button) view.findViewById(R.id.saveButton);

        //Set the views
        mNavigationTop.getBackground().setAlpha(0);
        mNavigationTitle.setVisibility(View.INVISIBLE);
        mNavigationBackBtn.setBackgroundResource(R.drawable.titlebar_menu_selector);

        mNavigationBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ResideMenu resideMenu = ((MainActivity) getActivity()).getResideMenu();
                if (resideMenu.isOpened()) {
                    resideMenu.closeMenu();
                } else {
                    resideMenu.openMenu();
                }
            }
        });

        mBackgroundImageView.setImageUrl(user.getCover_url(), imageLoader);
        if (user.getCover_url() != null) {
            mBackgroundImageView.setBackground(mBackgroundImageView.getDrawable());
        }

        Picasso.with(getActivity())
                .load(user.getAvatar_url())
                .noFade()
                .into(mAvatarView);

        mEditName.setText(user.getName());

        mEditEmail.setText(user.getEmail());

        mEditPassword.setText("samplePass");

//        mEditPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditPassword.setText("");
//            }
//        });

        mEditPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    newPassword = mEditPassword.getText().toString();

                    return true;
                }
                return false;
            }
        });

        mEditPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    newPassword = mEditPassword.getText().toString();
            }
        });


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = mEditName.getText().toString();

                final String[] oldPassword = {""};

                //Get old password
                if (newPassword != null && newPassword != "samplePass") {

                    final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    final TextView label = new TextView(getActivity());
                    label.setText("Please enter your old password");
                    alert.setView(label);
                    final EditText input = new EditText(getActivity());
                    alert.setView(input);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            oldPassword[0] = input.getText().toString().trim();
                            Log.v("oldPass", oldPassword.toString());
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }


//                if ( ((newName != "") && (newName != user.getName()))  ){
//
//                    query.updateProfile(newPassword, newName, oldPassword, new QueryAPI.ApiResponse<Boolean>() {
//                        @Override
//                        public void onCompletion(Boolean result) {
//
//                        }
//                    });
//                }

            }
        });


        String str_links = "Read the <a href='"+AppController.getHostname()+"conditions-d-utilisation'>Terms & Conditions</a>";
        mTermsTextView.setLinksClickable(true);
        mTermsTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mTermsTextView.setText(Html.fromHtml(str_links));




        return view;
    }



}
