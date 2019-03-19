package Logic.Managers.AnalyticsManager.EventParams;

import java.util.List;

import Logic.Models.Course;

public class CourseEventParams {

    private Course mCourse;
    private int mNumberOfCoursesDisplayed;
    private int mNumberOfAddedAlbums;

    public int getmNumberOfCoursesDisplayed() {
        return mNumberOfCoursesDisplayed;
    }

    public void setmNumberOfCoursesDisplayed(int mNumberOfCoursesDisplayed) {
        this.mNumberOfCoursesDisplayed = mNumberOfCoursesDisplayed;
    }

    public Course getmCourse() {
        return mCourse;
    }

    public void setmCourse(Course mCourse) {
        this.mCourse = mCourse;
    }

    public int getmNumberOfAddedAlbums() {
        return mNumberOfAddedAlbums;
    }

    public void setmNumberOfAddedAlbums(int mNumberOfAddedAlbums) {
        this.mNumberOfAddedAlbums = mNumberOfAddedAlbums;
    }
}
