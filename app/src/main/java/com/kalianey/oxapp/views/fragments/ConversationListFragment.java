package com.kalianey.oxapp.views.fragments;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.SessionManager;
import com.kalianey.oxapp.views.activities.MainActivity;
import com.kalianey.oxapp.views.activities.Message;
import com.kalianey.oxapp.views.adapters.ConversationListAdapter;

import java.util.ArrayList;
import java.util.List;

import br.com.goncalves.pugnotification.notification.PugNotification;


public class ConversationListFragment extends Fragment {

    private QueryAPI query = new QueryAPI();
    private SessionManager session;
    private ListView listView;
    private ConversationListAdapter adapter;
    private List<ModelConversation> conversations = new ArrayList<>();

    private BroadcastReceiver gcmReceiver;

    public ConversationListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // A BroadcastReceiver must override the onReceive() event.
        gcmReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle extras = intent.getExtras();
                if (!extras.isEmpty()) {
                    // Unparcel the bundle included in the Intent
                    Bundle bundle = extras.getBundle("conversation");
                    ModelConversation conversation = (ModelConversation) bundle.getSerializable("convObj");

                    Log.d("Notif receiver", "Got message: " + conversation.getPreviewText());

                    if (bundle != null && getActivity() != null) {
                        // display a notification at the top of the screen
                        displayAlert(getActivity().getApplicationContext(), MessageFragment.class, bundle);

                    }
                }
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false); //creates the view

        listView = (ListView) view.findViewById(R.id.conversation_list);

        //TODO: implement properly, I'm just a test
        PugNotification.with(getActivity().getApplicationContext())
                .load()
                .title("test")
                .message("msg")
                .bigTextStyle("testtest")
                .smallIcon(R.drawable.pugnotification_ic_launcher)
                .largeIcon(R.drawable.pugnotification_ic_launcher)
                .flags(Notification.FLAG_LOCAL_ONLY)
                .simple()
                .build();

        query.conversationList(new QueryAPI.ApiResponse<List<ModelConversation>>() {
                @Override
                public void onCompletion(List<ModelConversation> result) {
                    Log.v("ConvListCompletion", result.toString());
                    if (!result.isEmpty() && result != null) {
                        conversations = result;
                        adapter = new ConversationListAdapter(getActivity(), R.layout.fragment_conversation_list_item, conversations);
                        listView.setAdapter(adapter);
                        //adapter.notifyDataSetChanged();
                        Log.v("Data Set Changed: ", conversations.toString());
                    }
                }

            });

        return view;
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


    private void displayAlert(Context context, Class activity, Bundle bundle) {

        //Extract the conversation
        ModelConversation conversation = (ModelConversation) bundle.getSerializable("convObj");

        //Build the notification to display at the top of the activity's screen
        PugNotification.with(context)
                .load()
                .smallIcon(R.drawable.pugnotification_ic_launcher)
                .autoCancel(false)
                .largeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.pugnotification_ic_launcher))
                .title(conversation.getName() + " sent you a new message")
                .message(conversation.getPreviewText())
                .bigTextStyle("Here is some bigTestStyle")
                .click(activity, bundle)
                //.dismiss(activity, bundle)
                //.autoCancel(true)
                .simple()
                .build();
    }

    /**
     * class func displayAlert(notification:NSNotification, view:UIViewController, segueId:String, conversation:ModelConversation ){

     let l:Lang = Lang.sharedInstance

     //Display the alert with a link to the message
     TSMessage.addCustomDesignFromFileWithName("TSMessagesCustomDesign.json")


     TSMessage.showNotificationInViewController(
     view,
     title: l.text("newMsgFrom", or: "New message from ") + conversation.displayName!,
     subtitle: conversation.previewText!,
     image:UIImage(named: "timeline-chat"),
     type: TSMessageNotificationType.Message,
     duration: 5,
     //callback: { () -> Void in },
     callback: {
     view.performSegueWithIdentifier(segueId, sender: nil)
     },
     buttonTitle: "",
     buttonCallback: { () -> Void in },
     atPosition: TSMessageNotificationPosition.Top,
     canBeDismissedByUser: true
     )

     }
     */



}
