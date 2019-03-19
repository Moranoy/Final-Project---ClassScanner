package com.example.galbenabu1.classscanner.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.galbenabu1.classscanner.R;
import com.example.galbenabu1.classscanner.ViewHolders.UserNotificationViewHolder;

import java.util.List;

import Logic.Models.UserNotification;

public class UserNotificationsAdapter extends RecyclerView.Adapter<UserNotificationViewHolder> {
    private final String TAG = "UserNotificationsAdapt";

    private List<UserNotification> mUserNotificationsList;

    public UserNotificationsAdapter(List<UserNotification> userNotificationsList) {
        mUserNotificationsList = userNotificationsList;
    }

    @Override
    public UserNotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.e(TAG, "onCreateViewHolder() >>");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_notification_item, parent, false);

        Log.e(TAG, "onCreateViewHolder() <<");
        return new UserNotificationViewHolder(parent.getContext(), itemView);
    }

    @Override
    public void onBindViewHolder(UserNotificationViewHolder holder, int position) {

        Log.e(TAG, "onBindViewHolder() >> " + position);

        UserNotification userNotification = mUserNotificationsList.get(position);

        // bind user notification data to it's view items
        holder.getMtvNotificationDescription().setText(userNotification.getDescription());
        holder.getMtvNotificationTitle().setText(userNotification.getTitle());


        Log.e(TAG, "onBindViewHolder() << " + position);
    }

    @Override
    public int getItemCount() {
        return mUserNotificationsList.size();
    }
}