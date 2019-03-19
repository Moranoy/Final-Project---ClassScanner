package Logic.Managers.AnalyticsManager.AnalyticsHelpers;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import Logic.Managers.AnalyticsManager.Logger;

public class UserEventsHelper {
    private Logger mLogger;

    public void init(Logger logger) {
        this.mLogger = logger;
    }

    private void logEvent(String eventTypeString, Bundle params) {
        this.mLogger.logEvent(eventTypeString, params);
    }

    public void trackViewNotifications(int numberOfNotifications) {
        Bundle params = new Bundle();

        params.putInt(ParamNames.NUMBER_OF_NOTIFICATIONS, numberOfNotifications);

        logEvent(eUserEventType.ViewNotifications.name(), params);
    }

    public void trackClearNotifications(int numberOfNotifications) {
        Bundle params = new Bundle();

        params.putInt(ParamNames.NUMBER_OF_NOTIFICATIONS, numberOfNotifications);

        logEvent(eUserEventType.ClearNotifications.name(), params);
    }

    private static class ParamNames {
        private static final String NUMBER_OF_NOTIFICATIONS = "numberOfNotifications";
    }

    public enum eUserEventType {
        ViewNotifications,
        ClearNotifications,
    }
}
