package Logic.Managers.AnalyticsManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

import Logic.Managers.AnalyticsManager.AnalyticsHelpers.AlbumEventsHelper;
import Logic.Managers.AnalyticsManager.AnalyticsHelpers.CourseEventsHelper;
import Logic.Managers.AnalyticsManager.AnalyticsHelpers.PictureEventsHelper;
import Logic.Managers.AnalyticsManager.AnalyticsHelpers.UserEventsHelper;
import Logic.Managers.AnalyticsManager.EventParams.AlbumEventParams;
import Logic.Managers.AnalyticsManager.EventParams.CourseEventParams;
import Logic.Managers.AnalyticsManager.EventParams.PictureEventParams;
import Logic.Managers.AnalyticsManager.EventParams.UserEventParams;

public class AnalyticsManager {
    private static String TAG = "AnalyticsManager";

    private static final AnalyticsManager ourInstance = new AnalyticsManager();

    private static FirebaseAnalytics mFirebaseAnalytics;
    private static CourseEventsHelper mCourseEventsHelper;
    private static AlbumEventsHelper mAlbumEventsHelper;
    private static UserEventsHelper mUserEventsHelper;
    private static PictureEventsHelper mPictureEventsHelper;
    private static Logger mLogger;
    public static AnalyticsManager getInstance() {
        return ourInstance;
    }

    private AnalyticsManager() {
        mLogger = new Logger();
        mCourseEventsHelper = new CourseEventsHelper();
        mAlbumEventsHelper = new AlbumEventsHelper();
        mUserEventsHelper = new UserEventsHelper();
        mPictureEventsHelper = new PictureEventsHelper();
    }

    public void init(Context context) {
        Log.e(TAG, "Initializing");
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

        mLogger.init(mFirebaseAnalytics, userID);
        mCourseEventsHelper.init(mLogger);
        mAlbumEventsHelper.init(mLogger);
        mUserEventsHelper.init(mLogger);
        mPictureEventsHelper.init(mLogger);
    }

    public void trackSearchEvent(String searchedString, int numberOfMatches) {
        Log.e(TAG, "Tracking search event for search: " + searchedString);

        final String NUMBER_OF_MATCHES_PARAM = "numberOfMatches";

        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SEARCH_TERM, searchedString);
        params.putInt(NUMBER_OF_MATCHES_PARAM, numberOfMatches);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH,params);
    }

    public void trackCourseEvent(CourseEventsHelper.eCourseEventType eventType, CourseEventParams courseEventParams) {
        Log.e(TAG, "Tracking event " + eventType.name());

        switch (eventType) {
            case ViewSuggestedCourses:
                mCourseEventsHelper.trackViewedSuggestedCourses(courseEventParams.getmNumberOfCoursesDisplayed());
                break;
            case ViewMyCourses:
                mCourseEventsHelper.trackViewMyCourses(courseEventParams.getmNumberOfCoursesDisplayed());
                break;
            case ViewAllCourses:
                mCourseEventsHelper.trackViewAllCourses(courseEventParams.getmNumberOfCoursesDisplayed());
                break;
            case CourseCreated:
                mCourseEventsHelper.trackCourseCreated(courseEventParams.getmCourse());
                break;
            case AddAlbumsToCourse:
                mCourseEventsHelper.trackAddAlbumsToCourse(courseEventParams.getmCourse(), courseEventParams.getmNumberOfAddedAlbums());
                break;
        }
    }

    public void trackAlbumEvent(AlbumEventsHelper.eAlbumEventType eventType, AlbumEventParams albumEventParams) {
        Log.e(TAG, "Tracking event " + eventType.name());

        switch (eventType) {
            case ViewCourseAlbums:
                mAlbumEventsHelper.trackViewCourseAlbums(albumEventParams.getmCourseID(), albumEventParams.getmNumberOfAlbums());
                break;
            case ViewPrivateAlbums:
                mAlbumEventsHelper.trackViewPrivateAlbums(albumEventParams.getmNumberOfAlbums());
                break;
            case ViewAlbumPresentation:
                mAlbumEventsHelper.trackAlbumPresentationStarted(albumEventParams.getmAlbum());
                break;
            case AlbumCreated:
                mAlbumEventsHelper.trackAlbumCreated(albumEventParams.getmAlbum());
                break;
            case ViewAlbumImages:
                mAlbumEventsHelper.trackViewAlbumImages(albumEventParams.getmAlbum());
                break;
        }
    }

    public void trackUserEvent(UserEventsHelper.eUserEventType eventType, UserEventParams userEventParams) {
        Log.e(TAG, "Tracking event " + eventType.name());

        switch (eventType) {
            case ViewNotifications:
                mUserEventsHelper.trackViewNotifications(userEventParams.getmNumberOfNotifications());
                break;
            case ClearNotifications:
                mUserEventsHelper.trackClearNotifications(userEventParams.getmNumberOfNotifications());
                break;
        }
    }

    public void trackPictureEvent(PictureEventsHelper.ePictureEventType eventType, PictureEventParams pictureEventParams) {
        Log.e(TAG, "Tracking event " + eventType.name());

        switch (eventType) {
            case StartCropingImage:
                mPictureEventsHelper.trackStartCroppingImage(pictureEventParams.getmPicturePath());
                break;
            case StartTakingPictures:
                mPictureEventsHelper.trackStartTakingPictures();
                break;
        }
    }
}
