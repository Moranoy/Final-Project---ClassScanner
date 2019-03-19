package com.example.galbenabu1.classscanner.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.galbenabu1.classscanner.Adapters.UserNotificationsAdapter;
import com.example.galbenabu1.classscanner.R;

import java.util.ArrayList;
import java.util.List;

import Logic.Database.DBManager;
import Logic.Managers.AnalyticsManager.AnalyticsHelpers.UserEventsHelper;
import Logic.Managers.AnalyticsManager.AnalyticsManager;
import Logic.Managers.AnalyticsManager.EventParams.UserEventParams;
import Logic.Models.UserNotification;

public class UserNotificationsActivity extends Activity {

    private static final String TAG = "UserNotificationsAct";

    private List<UserNotification> mNotificationList = new ArrayList<>();
    private RecyclerView mNotificationsRecyclerView;
    private UserNotificationsAdapter mNotificationsAdapter;

    DBManager mDBManager = new DBManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notifications);
        
        this.bindUI();
        this.fetchUserNotifications();
        Log.e(TAG, "onCreate >> ");
    }

    private void bindUI() {
        mNotificationsRecyclerView = findViewById(R.id.user_notifications_recycler_view);
        mNotificationsRecyclerView.setHasFixedSize(true);
        mNotificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mNotificationsRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void fetchUserNotifications() {
        this.mNotificationList.clear();
        this.mNotificationsAdapter = new UserNotificationsAdapter(this.mNotificationList);
        this.mNotificationsRecyclerView.setAdapter(this.mNotificationsAdapter);
        this.fetchUserNotificationsFromDB();
    }

    private void fetchUserNotificationsFromDB() {
        mDBManager.fetchUserNotifications(this::onFinishedFetchingUserNotifications);
    }

    private void onFinishedFetchingUserNotifications(List<UserNotification> userNotifications) {
        this.logNotificationsEvent(UserEventsHelper.eUserEventType.ViewNotifications, this.mNotificationList.size());
        this.mNotificationList.addAll(userNotifications);
        this.mNotificationsRecyclerView.getAdapter().notifyDataSetChanged();
    }

    // On clicks

    public void onClearNotifications(View v) {
        Log.e(TAG, "onBackClick >> ");
        this.logNotificationsEvent(UserEventsHelper.eUserEventType.ClearNotifications, this.mNotificationList.size());
        this.mDBManager.removeUserNotificationsFromDB();
        this.mNotificationList.clear();
        this.mNotificationsRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void onBackClick(View v) {
        Log.e(TAG, "onBackClick >> ");
        super.onBackPressed();
    }

    private void logNotificationsEvent(UserEventsHelper.eUserEventType eventType, int numberOfNotifications) {
        UserEventParams userEventParams = new UserEventParams();

        userEventParams.setmNumberOfNotifications(numberOfNotifications);

        AnalyticsManager.getInstance().trackUserEvent(eventType, userEventParams);
    }
}
