package com.kalianey.oxapp.views.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
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
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.menu.ResideMenu;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.SessionManager;
import com.kalianey.oxapp.utils.UICircularImage;
import com.kalianey.oxapp.views.activities.MainActivity;
import com.kalianey.oxapp.views.activities.SignIn;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    //Vars
    private String oldPassword;
    private String currentPassword = AppController.getSession().getPassword();
    private Uri outputFileUri;

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

        mEditAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });

        mEditName.setText(user.getName());

        mEditEmail.setText(user.getEmail());

        if (currentPassword.equals("")) {
            currentPassword = "samplePassword";
        }

        mEditPassword.setText(currentPassword);

//        mEditPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditPassword.setText("");
//            }
//        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newName = mEditName.getText().toString();
                final String newPassword = mEditPassword.getText().toString();

                if (newPassword.length() > 5 && (newName.length() > 3)) {

                    //if user has not input any new password but has changed the name
                    if ((newPassword.equals("samplePassword") || newPassword.equals(currentPassword)) && !newName.equals(user.getName())) {
                        query.updateProfile("", newName, "", new QueryAPI.ApiResponse<Boolean>() {
                            @Override
                            public void onCompletion(Boolean result) {
                                if (result) {
                                    AppController.getInstance().getLoggedInUser().setName(newName);
                                    Toast.makeText(getActivity().getApplicationContext(), "Changed successfully saved!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), "Error, the changes could not be saved.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    //if user has input a new password, we ask to confirm the old password
                    else if (!newPassword.equals("samplePassword") && !newPassword.equals(currentPassword)) {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Confirmation");
                        alert.setMessage("Please enter your old password");
                        final EditText input = new EditText(getActivity());
                        alert.setView(input);
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                oldPassword = input.getText().toString().trim();
                                Log.v("oldPass", oldPassword.toString());
                                if (oldPassword.length() < 4) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Enter a valid password", Toast.LENGTH_SHORT).show();
                                } else {
                                    query.updateProfile(newPassword, newName, oldPassword, new QueryAPI.ApiResponse<Boolean>() {
                                        @Override
                                        public void onCompletion(Boolean result) {
                                            if (result) {
                                                AppController.getSession().setPassword(newPassword);
                                                AppController.getInstance().getLoggedInUser().setName(newName);
                                                Toast.makeText(getActivity().getApplicationContext(), "Changed successfully saved!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity().getApplicationContext(), "Error, the changes could not be saved.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                        alert.show();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please fill all the fields. Password length must be at least 6 characters, and name at least 4.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        String str_links = "Read the <a href='"+AppController.getHostname()+"conditions-d-utilisation'>Terms & Conditions</a>";
        mTermsTextView.setLinksClickable(true);
        mTermsTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mTermsTextView.setText(Html.fromHtml(str_links));

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return view;
    }


    private void logout(){

        //Google
        GoogleApiClient GoogleApiClient = AppController.getInstance().getmGoogleApiClient();
        GoogleApiClient.connect();

        if (GoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(GoogleApiClient);
            GoogleApiClient.disconnect();

            //set timer to delay of 1sec the continuity of the script and allow the disconnect to happen
            //mGoogleApiClient.connect();
        }

        //Facebook
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        LoginManager.getInstance().logOut();

        //Oxwall
        query.logout(new QueryAPI.ApiResponse<Boolean>() {
            @Override
            public void onCompletion(Boolean result) {
                //Wipe out preferences
                AppController.getSession().delete();
                Intent i = new Intent(getActivity().getApplicationContext(), SignIn.class);
                startActivity(i);
            }
        });

    }


    /*** Pick and Send Image  ***/

    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "AmbajePhotos" + File.separator);
        root.mkdirs();
        final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getResources().getString(R.string.add_ambaj_select_source));

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try {
            if(resultCode == getActivity().RESULT_OK)
            {
                if(requestCode == 1)
                {
                    final boolean isCamera;
                    if(data == null)
                    {
                        isCamera = true;
                    }
                    else
                    {
                        final String action = data.getAction();
                        if(action == null)
                        {
                            isCamera = false;
                        }
                        else
                        {
                            isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }

                    Uri selectedImageUri;
                    if (isCamera) {
                        selectedImageUri = outputFileUri;
                        File imgFile = new File(selectedImageUri.getPath());
                        if (imgFile.exists()) {

                            Bitmap myBitmap = getScaledBitmap(imgFile.getAbsolutePath(), 800, 800);

                            //TODO: send bitmap
                            Log.v("Image picked", selectedImageUri.toString());

                        }
                    } else {
                        selectedImageUri = data == null ? null : data.getData();
                        //String realPath = getRealPathFromURI(getActivity().getApplicationContext(), selectedImageUri);
                        String realPath = getImagePath(selectedImageUri);
                        File imgFile = new File(realPath);

                        query.updateAvatar(imgFile, new QueryAPI.ApiResponse<Boolean>() {
                            @Override
                            public void onCompletion(Boolean result) {

                            }
                        });

                        //File imgFile = new File(getRealPathFromURI(getActivity().getApplicationContext(), selectedImageUri));

                        //TODO: what is this for???
//                        if (imgFile.exists()) {
//
//                            Bitmap myBitmap = getScaledBitmap(imgFile.getAbsolutePath(), 800, 800);
//
//                            //TODO: send bitmap
//
//                            Log.v("Image picked", selectedImageUri.toString());
//                        }
                    }


                }
            }
        }
        catch(Exception e){
            Log.w("KKK", "Error: "+e.toString());
        }
    }

    public String getImagePath(Uri uri){
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

}
