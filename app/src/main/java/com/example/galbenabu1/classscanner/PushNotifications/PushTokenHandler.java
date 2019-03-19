package com.example.galbenabu1.classscanner.PushNotifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import Logic.Database.DBManager;
import Logic.Managers.LoggedInUserDetailsManager;

public class PushTokenHandler extends FirebaseInstanceIdService {

    private static final String TAG = "PushTokenHandler";

    @Override
    public void onTokenRefresh() {

        Log.e(TAG, "onTokenRefresh() >>");
        // Get updated InstanceID token.
        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        //send registration to server
        sendRegistrationToServer(deviceToken);

        Log.e(TAG, "onTokenRefresh() << deviceToken=" + deviceToken);
    }

    private void sendRegistrationToServer(String deviceToken) {
        LoggedInUserDetailsManager.setPushNotificationToken(deviceToken);
    }
}


