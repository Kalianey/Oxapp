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
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.models.ModelMessage;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.EndlessScrollListener;
import com.kalianey.oxapp.utils.MultipartRequest;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.SessionManager;
import com.kalianey.oxapp.views.adapters.MessageListAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

    private FrameLayout mNavigationTop;
    private TextView mNavigationTitle;
    private Button mNavigationBackBtn;
    private Button sendButton;
    private ImageView cameraButton;
    private TextView text;

    private Uri outputFileUri;

    private EndlessScrollListener scrollListener;

    public MessageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);



        //LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, new IntentFilter("Msg"));

        mNavigationTop = (FrameLayout) view.findViewById(R.id.layout_top);
        mNavigationTitle = (TextView) view.findViewById(R.id.titleBar);
        mNavigationBackBtn = (Button) view.findViewById(R.id.title_bar_left_menu);

        listView = (ListView) view.findViewById(R.id.message_list);
        text = (TextView) view.findViewById(R.id.txt);
        sendButton = (Button) view.findViewById(R.id.btnSend);
        cameraButton = (ImageView) view.findViewById(R.id.camera);

        mNavigationBackBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

        //Get serialized object
        conversation = (ModelConversation) getActivity().getIntent().getSerializableExtra("convObj");
        Log.d("ConvId for messList: ", conversation.getId());

        mNavigationTitle.setText(conversation.getName());


        query.messageList(conversation.getId(), new QueryAPI.ApiResponse<List<ModelMessage>>() {
            @Override
            public void onCompletion(List<ModelMessage> result) {
                messages = result;
                ModelUser opponent = new ModelUser();

                QueryAPI.getInstance().user(conversation.getOpponentId(), new QueryAPI.ApiResponse<ModelUser>() {
                    @Override
                    public void onCompletion(ModelUser user) {
                        adapter = new MessageListAdapter(getActivity(), R.layout.message_item_sent, messages);
                        adapter.setSenderUser(user);
                        listView.setAdapter(adapter);
                        //adapter.notifyDataSetChanged();
                        Log.d("AdapterChanged mess: ", messages.toString());
                    }
                });


            }
        });


        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageToSend = text.getText().toString();

                query.messageSend(conversation.getId(), messageToSend, new QueryAPI.ApiResponse<ModelMessage>() {
                    @Override
                    public void onCompletion(ModelMessage message) {

                        messages.add(message);
                        adapter.notifyDataSetChanged();
                        text.setText("");

                    }
                });
            }
        });

        scrollListener = new EndlessScrollListener(){
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadMore(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        };
        scrollListener.setScrollDirection(EndlessScrollListener.SCROLL_DIRECTION_UP);
        //Load more on scroll top
        listView.setOnScrollListener(scrollListener);

        return view;
    }


    // Append more data into the adapter
    public void loadMore(int offset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        Toast.makeText(getActivity(), "Scrolled to top", Toast.LENGTH_SHORT).show();

        String lastMessageId = messages.get(0).getId();
        query.conversationHistory(conversation.getId(), lastMessageId, new QueryAPI.ApiResponse<List<ModelMessage>>() {
            @Override
            public void onCompletion(List<ModelMessage> result) {
                Collections.reverse(result);
                int index = result.size();  //listView.getFirstVisiblePosition();
                View v = listView.getChildAt(0);
                int top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());

                messages.addAll(0, result);
                //adapter.notifyDataSetChanged();

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
        IntentFilter gcmFilter = new IntentFilter();
        gcmFilter.addAction("GCM_RECEIVED_ACTION");
        getActivity().registerReceiver(gcmReceiver, gcmFilter);

    }

    // A BroadcastReceiver must override the onReceive() event.
    private BroadcastReceiver gcmReceiver = new BroadcastReceiver() {

        private String broadcastMessage;

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras().getBundle("conversation");
            broadcastMessage = intent.getExtras().getString("gcm");

            if (broadcastMessage != null && getActivity() != null) {
                // display our received message
                onResume();
            }
        }
    };


//    private BroadcastReceiver onNotice= new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.v("Mess: ", intent.toString());
//
//            //add row
//
//        }
//    };


    // Send Image

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
                        String lastMessage = messages.get(messages.size() - 1).getId();

                        query.messageSendWithMedia(conversation.getId(), conversation.getOpponentId(),lastMessage, imgFile, new QueryAPI.ApiResponse<ModelMessage>() {
                            @Override
                            public void onCompletion(ModelMessage result) {

                                Log.d("Result media", result.toString());
                            }
                        });

                        //File imgFile = new File(getRealPathFromURI(getActivity().getApplicationContext(), selectedImageUri));

                        if (imgFile.exists()) {

                            Bitmap myBitmap = getScaledBitmap(imgFile.getAbsolutePath(), 800, 800);

                            //TODO: send bitmap

                            Log.v("Image picked", selectedImageUri.toString());
                        }
                    }


                }
            }
        }
        catch(Exception e){
            Log.w("KKK", "Error: "+e.toString());
        }
    }

//    public String getRealPathFromURI(Context context, Uri contentUri) {
//        Cursor cursor = null;
//        try {
//
//            if("content".equals(contentUri.getScheme())) {
//                String[] proj = {MediaStore.Images.Media.DATA};
//                cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToFirst();
//                return cursor.getString(column_index);
//            }
//            else{
//                return contentUri.getPath();
//            }
//
//
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//    }

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

