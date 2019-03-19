package Logic.Database.DBModels.CourseActions;

import java.util.Collection;
import java.util.List;

public class CourseActionData {
    private eCourseActionType mCourseActionType;
    private String mCourseID;
    private List<String> mUserCoursesIDs;


    public eCourseActionType getmCourseActionType() {
        return mCourseActionType;
    }

    public void setmCourseActionType(eCourseActionType mCourseActionType) {
        this.mCourseActionType = mCourseActionType;
    }

    public String getmCourseID() {
        return mCourseID;
    }

    public void setmCourseID(String mCourseID) {
        this.mCourseID = mCourseID;
    }

    public Collection<String> getmUserCoursesIDs() {
        return mUserCoursesIDs;
    }

    public void setmUserCoursesIDs(List<String> mUserCoursesIDs) {
        this.mUserCoursesIDs = mUserCoursesIDs;
    }
}
