package Logic.Database.DBModels;

import java.util.Collection;
import java.util.List;

public class UserActionData {
    private String mPushNotificationToken;
    private String mUserID;
    private List<String> mUserCourseIDs;

    public String getmUserID() {
        return mUserID;
    }

    public void setmUserID(String mUserID) {
        this.mUserID = mUserID;
    }

    public Collection<String> getmUserCourseIDs() {
        return mUserCourseIDs;
    }

    public void setmUserCourseIDs(List<String> mUserCourseIDs) {
        this.mUserCourseIDs = mUserCourseIDs;
    }

    public String getmPushNotificationToken() {
        return mPushNotificationToken;
    }

    public void setmPushNotificationToken(String mPushNotificationToken) {
        this.mPushNotificationToken = mPushNotificationToken;
    }
}
