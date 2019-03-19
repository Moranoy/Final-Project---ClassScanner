package com.example.galbenabu1.classscanner;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.galbenabu1.classscanner.Activities.ShowAlbumsActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String TAG ="PushNotificationService";

    private static final String COURSE_ID_DATA = "course_id_data"; // Course ID for fetching course albums (only relevant if shouldShowPrivateAlbums is false.
    private static final String SHOULD_SHOW_PRIVATE_ALBUMS_DATA = "should_show_private_albums"; // Showing private albums if true, shared albums if false.
    private static final String IS_SELECTING_ALBUMS = "is_selecting_albums"; // In an album selecting mode. Returns selected albums to previous activity.

    public PushNotificationService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String channelID = "my_channel_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e(TAG, "onMessageReceived() >>");
            // init channel
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // The id of the channel.

            // The user-visible name of the channel.
            CharSequence name = "test channel name";

            // The user-visible description of the channel.
            String description = "test channel description";

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(channelID, name, importance);

            // Configure the notification channel.
            mChannel.setDescription(description);

            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);

            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            channelID = null;
        }

        String title;
        String body;
        String albumName = null;
        String courseName = null;
        String courseID = null;
        int icon = R.drawable.iconapp;

        Uri soundRri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Map<String, String> data;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() == 0) {
            Log.e(TAG, "onMessageReceived() << No data doing nothing");
            return;
        }

        //parse the data
        data = remoteMessage.getData();
        Log.e(TAG, "Message data : " + data);

        String value = data.get(NotificationDataKeys.COURSE_ID);
        if(value != null) {
            courseID = value;
        }

        value = data.get(NotificationDataKeys.COURSE_NAME);
        if(value != null) {
            courseName = value;
        }

        value = data.get(NotificationDataKeys.ALBUM_NAME);
        if(value != null) {
            albumName = value;
        }

        title = "Album added to course";
        body = "The album \'" + albumName + "\' has been added to the course \'" + courseName + "\'";

        Intent intent = new Intent(this, ShowAlbumsActivity.class);

        intent.putExtra(COURSE_ID_DATA, courseID);
        intent.putExtra(IS_SELECTING_ALBUMS, false);
        intent.putExtra(SHOULD_SHOW_PRIVATE_ALBUMS_DATA, false);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelID)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(icon)
                        .setSound(soundRri);


        notificationBuilder.addAction(new NotificationCompat.Action(R.drawable.iconapp,"View New Album", pendingIntent));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 , notificationBuilder.build());

        Log.e(TAG, "onMessageReceived() <<");
    }

    private static class NotificationDataKeys {
        private static final String COURSE_ID = "courseID";
        private static final String ALBUM_NAME = "albumName";
        private static final String COURSE_NAME = "courseName";
    }
}
