package com.example.galbenabu1.classscanner.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.galbenabu1.classscanner.R;


public class UserNotificationViewHolder extends RecyclerView.ViewHolder {

    private CardView mUserNotificationCardView;
    private TextView mtvNotificationDescription;

    public TextView getMtvNotificationTitle() {
        return mtvNotificationTitle;
    }

    public void setMtvNotificationTitle(TextView mtvNotificationTitle) {
        this.mtvNotificationTitle = mtvNotificationTitle;
    }

    private TextView mtvNotificationTitle;


    public UserNotificationViewHolder(Context context, View itemView) {
        super(itemView);

        this.mtvNotificationDescription = itemView.findViewById(R.id.tvUserNotificationDescription);
        this.mtvNotificationTitle= itemView.findViewById(R.id.tvUserNotificationTitle);
    }

    public CardView getmUserNotificationCardView() {
        return mUserNotificationCardView;
    }

    public void setmUserNotificationCardView(CardView mUserNotificationCardView) {
        this.mUserNotificationCardView = mUserNotificationCardView;
    }

    public TextView getMtvNotificationDescription() {
        return mtvNotificationDescription;
    }

    public void setMtvNotificationDescription(TextView mtvNotificationDescription) {
        this.mtvNotificationDescription = mtvNotificationDescription;
    }
}