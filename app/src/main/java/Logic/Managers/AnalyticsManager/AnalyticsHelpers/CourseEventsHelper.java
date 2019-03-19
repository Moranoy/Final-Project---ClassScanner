package Logic.Managers.AnalyticsManager.AnalyticsHelpers;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import Logic.Managers.AnalyticsManager.AnalyticsManager;
import Logic.Managers.AnalyticsManager.Logger;
import Logic.Models.Course;

public class CourseEventsHelper {

    private Logger mLogger;

    public void init(Logger logger) {
        this.mLogger = logger;
    }

    private void logEvent(String eventTypeString, Bundle params) {
        this.mLogger.logEvent(eventTypeString, params);
    }

    public void trackViewedSuggestedCourses(int numberOfCoursesDisplayed) {
        Bundle params = new Bundle();

        params.putInt(ParamNames.NUMBER_OF_DISPLAYED_COURSES, numberOfCoursesDisplayed);

        this.logEvent(eCourseEventType.ViewSuggestedCourses.name(), params);
    }

    public void trackViewMyCourses(int numberOfCoursesDisplayed) {
        Bundle params = new Bundle();

        params.putInt(ParamNames.NUMBER_OF_DISPLAYED_COURSES, numberOfCoursesDisplayed);

        this.logEvent(eCourseEventType.ViewMyCourses.name(), params);
    }

    public void trackViewAllCourses(int numberOfCoursesDisplayed) {
        Bundle params = new Bundle();

        params.putInt(ParamNames.NUMBER_OF_DISPLAYED_COURSES, numberOfCoursesDisplayed);

        this.logEvent(eCourseEventType.ViewAllCourses.name(), params);
    }

    public void trackCourseCreated(Course createdCourse) {
        Bundle params = new Bundle();

        params.putString(ParamNames.COURSE_NAME, createdCourse.getCourseName());

        this.logEvent(eCourseEventType.CourseCreated.name(), params);
    }

    public void trackAddAlbumsToCourse(Course course, int numberOfAddedAlbums) {
        Bundle params = new Bundle();

        params.putString(ParamNames.COURSE_NAME, course.getCourseName());
        params.putInt(ParamNames.NUMBER_OF_ADDED_ALBUMS, numberOfAddedAlbums);

        this.logEvent(eCourseEventType.CourseCreated.name(), params);
    }

    private static class ParamNames {
        private static final String NUMBER_OF_DISPLAYED_COURSES = "numberOfDisplayedCourses";
        private static final String COURSE_NAME = "courseName";
        private static final String NUMBER_OF_ADDED_ALBUMS = "numberOfAddedAlbums";
    }

    public enum eCourseEventType {
        ViewSuggestedCourses,
        ViewMyCourses,
        ViewAllCourses,
        CourseCreated,
        AddAlbumsToCourse,
    }
}
