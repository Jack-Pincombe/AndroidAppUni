package com.example.youtubefirebase.utilities;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "MyFirebasesagingService";

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notificatiobody = "";
        String notificationtitle = "";
        String notificationdata = "";

        try {
            notificationdata = remoteMessage.getData().toString();
            notificationtitle = remoteMessage.getNotification().getTitle();
            notificatiobody = remoteMessage.getNotification().getBody();
        } catch (NullPointerException e) {
            Log.e(TAG, "onmessaged received null pointer exception " + e.getMessage());
        }
        Log.d(TAG, "onboard message received : data: " + notificationdata);
        Log.d(TAG, "Onboard message received : body: " + notificatiobody);
        Log.d(TAG, "Title message received : title::" + notificationtitle);
    }
}
