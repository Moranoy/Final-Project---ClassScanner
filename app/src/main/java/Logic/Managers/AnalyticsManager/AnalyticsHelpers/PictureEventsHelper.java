package Logic.Managers.AnalyticsManager.AnalyticsHelpers;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import Logic.Managers.AnalyticsManager.Logger;

public class PictureEventsHelper {

    private Logger mLogger;

    public void init(Logger logger) {
        this.mLogger = logger;
    }

    private void logEvent(String eventTypeString, Bundle params) {
        this.mLogger.logEvent(eventTypeString, params);
    }

    public void trackStartCroppingImage(String picturePath) {
        Bundle params = new Bundle();

        params.putString(ParamNames.PICTURE_PATH, picturePath);

        logEvent(ePictureEventType.StartCropingImage.name(), params);
    }

    public void trackStartTakingPictures() {
        Bundle params = new Bundle();
        logEvent(ePictureEventType.StartTakingPictures.name(), params);
    }

    private static class ParamNames {
        private static final String PICTURE_PATH = "picturePath";
    }

    public enum ePictureEventType {
        StartCropingImage,
        StartTakingPictures,
    }
}
