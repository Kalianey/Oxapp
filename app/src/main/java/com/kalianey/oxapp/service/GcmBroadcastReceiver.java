/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kalianey.oxapp.service;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.AvoidXfermode;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.views.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static android.support.v4.content.WakefulBroadcastReceiver.completeWakefulIntent;
import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

public class GcmBroadcastReceiver extends BroadcastReceiver {
    private final String LOG_TAG = BroadcastReceiver.class.getSimpleName();

    private static final String EXTRA_SENDER = "from";
    private static final String EXTRA_TYPE = "type";
    private static final String EXTRA_MESSAGE = "message";

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    public GcmBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);
        Log.d("Notif received", messageType.toString());

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Is this our message?? Better be if you're going to act on it!
                if (MainActivity.PROJECT_NUMBER.equals(extras.getString(EXTRA_SENDER))) {
                    // Process message and then post a notification of the received message.
                    String type = extras.getString(EXTRA_TYPE);
                    String message = extras.getString(EXTRA_MESSAGE);
                    String dataString = extras.getString("extra");

                    //Build a conv to send to MessageView
                    ModelConversation conversation = new ModelConversation();
                    try {
                        JSONObject dataObj = new JSONObject(dataString); //{"senderId":3,"conversationId":2,"displayName":"Veda","recipientId":"1","message":"test again"}
                        conversation.setId(dataObj.getString("conversationId"));
                        conversation.setName(dataObj.getString("displayName"));
                        conversation.setPreviewText(message);
                        conversation.setOpponentId(dataObj.getString("senderId"));
                        conversation.setInitiatorId(dataObj.getString("recipientId"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Send notification
                    String alert = "New message: " + message;
                    sendNotification(context, alert);

                    //Send to MessageFragment
                    Intent broadcastIntent = new Intent();
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("convObj", conversation);
                    broadcastIntent.setAction("GCM_RECEIVED_ACTION");
                    broadcastIntent.putExtra("conversation", mBundle);
                    broadcastIntent.putExtra("gcm", message);
                    context.sendBroadcast(broadcastIntent);

                }

                Log.i(LOG_TAG, "Received: " + extras.toString());
            }
        }
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with a GCM message.
    //http://developer.android.com/guide/topics/ui/notifiers/notifications.html
    private void sendNotification(Context context, String msg) {

        String appName = AppController.getAppName();

        PendingIntent contentIntent =
                PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icons_chat)
                        .setContentTitle(appName)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        mBuilder.setContentIntent(contentIntent);

        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
