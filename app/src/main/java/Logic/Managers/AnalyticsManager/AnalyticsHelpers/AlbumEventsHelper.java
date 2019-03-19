package Logic.Managers.AnalyticsManager.AnalyticsHelpers;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import Logic.Managers.AnalyticsManager.Logger;
import Logic.Models.Album;

public class AlbumEventsHelper {

    private Logger mLogger;

    public void init(Logger logger) {
        this.mLogger = logger;
    }

    private void logEvent(String eventTypeString, Bundle params) {
        this.mLogger.logEvent(eventTypeString, params);
    }

    public void trackViewCourseAlbums(String courseID, int numberOfAlbums) {
        Bundle params = new Bundle();

        params.putString(ParamNames.COURSE_ID, courseID);
        params.putInt(ParamNames.NUMBER_OF_ALBUMS, numberOfAlbums);

        logEvent(eAlbumEventType.ViewCourseAlbums.name(), params);
    }

    public void trackViewPrivateAlbums(int numberOfAlbums) {
        Bundle params = new Bundle();

        params.putInt(ParamNames.NUMBER_OF_ALBUMS, numberOfAlbums);

        logEvent(eAlbumEventType.ViewPrivateAlbums.name(), params);
    }

    public void trackAlbumPresentationStarted(Album presentedAlbum) {
        Bundle params = new Bundle();

        params.putInt(ParamNames.NUMBER_OF_PICTURES, presentedAlbum.getM_Pictures().size());
        params.putString(ParamNames.ALBUM_NAME, presentedAlbum.getM_AlbumName());

        logEvent(eAlbumEventType.ViewAlbumPresentation.name(), params);
    }

    public void trackAlbumCreated(Album createdAlbum) {
        Bundle params = new Bundle();

        params.putInt(ParamNames.NUMBER_OF_PICTURES, createdAlbum.getM_Pictures().size());
        params.putString(ParamNames.ALBUM_NAME, createdAlbum.getM_AlbumName());

        logEvent(eAlbumEventType.AlbumCreated.name(), params);
    }

    public void trackViewAlbumImages(Album album) {
        Bundle params = new Bundle();

        params.putInt(ParamNames.NUMBER_OF_PICTURES, album.getM_Pictures().size());
        params.putString(ParamNames.ALBUM_NAME, album.getM_AlbumName());

        logEvent(eAlbumEventType.AlbumCreated.name(), params);
    }

    private static class ParamNames {
        public static final String COURSE_ID = "courseID";
        public static final String NUMBER_OF_ALBUMS = "numberOfDisplayedAlbums";
        public static final String ALBUM_NAME = "albumName";
        public static final String NUMBER_OF_PICTURES = "numberOfPictures";
    }

    public enum eAlbumEventType {
        ViewCourseAlbums,
        ViewPrivateAlbums,
        ViewAlbumPresentation,
        AlbumCreated,
        ViewAlbumImages,
    }
}
