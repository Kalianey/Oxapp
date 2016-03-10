package com.kalianey.oxapp.views.fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.models.ModelMessage;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.EndlessScrollListener;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.SessionManager;
import com.kalianey.oxapp.utils.Utility;
import com.kalianey.oxapp.views.adapters.MessageListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MessageFragment extends Fragment {

    private QueryAPI query = new QueryAPI();
    private SessionManager session;
    private ListView listView;
    private MessageListAdapter adapter;
    private List<ModelMessage> messages = new ArrayList<>();
    private ModelConversation conversation;

    //UI
    private LinearLayout messageFragmentContainer;
    private FrameLayout mNavigationTop;
    private TextView mNavigationTitle;
    private Button mNavigationBackBtn;
    private ImageButton sendButton;
    private ImageButton cameraButton;
    private EditText text;

    private Uri outputFileUri;

    private EndlessScrollListener scrollListener;

    //NOTIFICATIONS
    private BroadcastReceiver gcmReceiver;

    public MessageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_message, container, false);

        setUpUI(view);

        //Get serialized object
        conversation = (ModelConversation) getActivity().getIntent().getSerializableExtra("convObj");
        mNavigationTitle.setText(conversation.getName());

        query.messageList(conversation.getId(), new QueryAPI.ApiResponse<List<ModelMessage>>() {
            @Override
            public void onCompletion(List<ModelMessage> result) {
                messages = result;
                ModelUser opponent = new ModelUser();

                QueryAPI.getInstance().user(conversation.getOpponentId(), new QueryAPI.ApiResponse<ModelUser>() {
                    @Override
                    public void onCompletion(ModelUser user) {
                        view.findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
                        adapter = new MessageListAdapter(getActivity(), R.layout.message_item_sent, messages);
                        adapter.setSenderUser(user);
                        listView.setAdapter(adapter);
                        //adapter.notifyDataSetChanged();
                        Log.d("AdapterChanged mess: ", messages.toString());
                    }
                });


            }
        });

        setUpNotificationListener();

        return view;
    }


    // Append more data into the adapter
    public void loadMore(int offset) {
        // Use the offset value and add it as a parameter to the API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        String lastMessageId = messages.get(0).getId();
        query.conversationHistory(conversation.getId(), lastMessageId, new QueryAPI.ApiResponse<List<ModelMessage>>() {
            @Override
            public void onCompletion(List<ModelMessage> result) {
                Collections.reverse(result);
                int index = result.size();  //listView.getFirstVisiblePosition();
                if (index == 0)
                {
                    scrollListener = null;
                    return;
                }

                Toast.makeText(getActivity(), "Scrolled to top", Toast.LENGTH_SHORT).show();
                View v = listView.getChildAt(0);
                int top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());

                messages.addAll(0, result);
                adapter.notifyDataSetChanged();

                listView.setSelectionFromTop(index, top);
                scrollListener.finishedLoading();
            }
        });
    }

    public void finish() {

        getActivity().finish();
        getActivity().overridePendingTransition(0, 0);
    }

    @Override
    public void onResume() {

        super.onResume();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(gcmReceiver, new IntentFilter("msg-received"));

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(gcmReceiver);
    }


    /*** Set up the UI ***/
    private void setUpUI(View view){

        RelativeLayout listViewContainer = (RelativeLayout) view.findViewById(R.id.listViewContainer);
        mNavigationTop = (FrameLayout) view.findViewById(R.id.layout_top);
        mNavigationTitle = (TextView) view.findViewById(R.id.titleBar);
        mNavigationBackBtn = (Button) view.findViewById(R.id.title_bar_left_menu);

        listView = (ListView) view.findViewById(R.id.message_list);
        text = (EditText) view.findViewById(R.id.txt);
        sendButton = (ImageButton) view.findViewById(R.id.btnSend);
        cameraButton = (ImageButton) view.findViewById(R.id.camera);


        mNavigationBackBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

        cameraButton.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });

        sendButton.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageToSend = text.getText().toString();

                if (!messageToSend.equals("") && messageToSend != null) {

                    query.messageSend(conversation.getId(), messageToSend, new QueryAPI.ApiResponse<ModelMessage>() {
                        @Override
                        public void onCompletion(ModelMessage message) {

                            messages.add(message);
                            adapter.notifyDataSetChanged();
                            text.setText("");

                        }
                    });
                }

            }
        });

        scrollListener = new EndlessScrollListener(){
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                loadMore(page);
            }
        };
        scrollListener.setScrollDirection(EndlessScrollListener.SCROLL_DIRECTION_UP);
        //Load more on scroll top
        listView.setOnScrollListener(scrollListener);

    }

    /*  Set up the Notification Listener for new messages  */

    private void setUpNotificationListener(){
        /** Set up top notifications TSnackBar **/
        gcmReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle extras = intent.getExtras();
                if (!extras.isEmpty()) {
                    // Unparcel the bundle included in the Intent
                    Bundle bundle = extras.getBundle("conversation");
                    ModelConversation openConvFromMsg = (ModelConversation) bundle.getSerializable("convObj");

                    Log.d("MSG received", "Got message: " + conversation.getPreviewText());

                    if (bundle != null) {
                        //We check if the conversation notification to show is different from the current open conversation
                        if (!openConvFromMsg.getId().equals(conversation.getId())) {
                            // display a notification at the top of the screen
                            Utility.displayNewMsgNotification(getActivity().getApplicationContext(), getActivity(), MessageFragment.class, bundle, mNavigationTop);
                        } else {
                            //we get the latest messageList and notify the adapter to display the received msg
                            //TODO: check if it would be better to append the new msg without querying the server?
                            query.messageList(conversation.getId(), new QueryAPI.ApiResponse<List<ModelMessage>>() {
                                @Override
                                public void onCompletion(List<ModelMessage> result) {
                                    messages = new ArrayList<ModelMessage>();
                                    messages = result;
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            }
        };
    }

    /*** IMAGE PICKER FUNCTIONS ***/

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
                    final File imgFile;
                    if (isCamera) {
                        selectedImageUri = outputFileUri;
                        imgFile = new File(selectedImageUri.getPath());
                        if (imgFile.exists()) {

                            Bitmap myBitmap = getScaledBitmap(imgFile.getAbsolutePath(), 800, 800);

                            //TODO: send bitmap
                            Log.v("Image picked", selectedImageUri.toString());

                        }
                    } else {
                        selectedImageUri = data == null ? null : data.getData();
                        //String realPath = getRealPathFromURI(getActivity().getApplicationContext(), selectedImageUri);
                        String realPath = getImagePath(selectedImageUri);
                        imgFile = new File(realPath);

                    }

                    //File imgFile = new File(getRealPathFromURI(getActivity().getApplicationContext(), selectedImageUri));

                    if (imgFile.exists()) {

                        //TODO: send bitmap
                        //Bitmap myBitmap = getScaledBitmap(imgFile.getAbsolutePath(), 800, 800);

                        Log.v("Image picked", selectedImageUri.toString());

                        String lastMessage = messages.get(messages.size() - 1).getId();

                        query.messageSendWithMedia(conversation.getId(), conversation.getOpponentId(), lastMessage, imgFile, new QueryAPI.ApiResponse<List<ModelMessage>>() {
                            @Override
                            public void onCompletion(List<ModelMessage> newMessages) {
                                Log.d("Result media", newMessages.toString());

                                if (newMessages.size() > 0) {

                                    for (int i = 0; i < newMessages.size(); i++) {
                                        ModelMessage mess = newMessages.get(i);
                                        mess.setIsMediaMessage(true); //TODO : set attachment url in backend
                                        mess.setImage(imgFile);
                                        messages.add(mess);
                                    }
                                    adapter.notifyDataSetChanged();

                                }
                            }
                        });
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

