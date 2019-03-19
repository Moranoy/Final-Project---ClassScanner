package Logic.Managers.AnalyticsManager;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import Logic.Managers.AnalyticsManager.AnalyticsHelpers.CourseEventsHelper;

public class Logger {
    private static final String USER_ID_KEY = "userID";
    private String mUserID;
    private FirebaseAnalytics mFirebaseAnalytics;

    public void init(FirebaseAnalytics firebaseAnalytics, String userID) {
        this.mUserID = userID;
        this.mFirebaseAnalytics = firebaseAnalytics;
    }

    public void logEvent(String eventTypeString, Bundle params) {
        params.putString(USER_ID_KEY, this.mUserID);
        this.mFirebaseAnalytics.logEvent(eventTypeString, params);
    }
}
