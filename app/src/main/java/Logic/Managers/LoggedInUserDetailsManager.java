package Logic.Managers;

import android.util.Log;

import java.util.ArrayList;

import Logic.Database.DBManager;
import Logic.Models.User;

public class LoggedInUserDetailsManager {
    private static final String TAG = "LoggedInUserDetailsMng";
    private static final String UNKNOWN_USER_NAME = "Unknown";
    private static final String UNKNOWN_USER_MAIL = "";

    private static final DBManager sfDBManager = new DBManager();
    private static String sPushNotificationToken;


    private LoggedInUserDetailsManager() {

    }

    private static User sLoggedInUser;

    public static User getsLoggedInUser() {
        return sLoggedInUser;
    }

    public static void setsLoggedInUser(User loggedInUser) {
        sLoggedInUser = loggedInUser;
    }

    public static String getUserID() {
        return sLoggedInUser.getM_Id();
    }

    public static void initUserDetailsOnLogin(String uid) {
        sfDBManager.fetchUserDetails(uid, LoggedInUserDetailsManager::onFetchedUserSuccess, LoggedInUserDetailsManager::onFetchedUserFailure);
    }

    private static void onFetchedUserSuccess(User userInfo) {
        Log.e(TAG, "Fetched user info for user: " + userInfo.getM_Id());
        setsLoggedInUser(userInfo);
        sfDBManager.onUserLogin(userInfo.getM_Id(), userInfo.getM_CourseIds(), sPushNotificationToken);
    }

    private static void onFetchedUserFailure() {
        Log.e(TAG, "Failed fetching user info.");

        // We get here only if firebase succeeded in fetching user, but we did not manage to fetch user info from database.
        // Init unknown user.
        User unknownUser = new User(UNKNOWN_USER_NAME, UNKNOWN_USER_MAIL);
        unknownUser.setM_CourseIds(new ArrayList<>());

        setsLoggedInUser(unknownUser);
    }

    public static boolean doesUserContainCourseID(String courseID) {
        return sLoggedInUser.getM_CourseIds().contains(courseID);
    }

    public static void addCourseIDToUser(String courseID) {
        sLoggedInUser.getM_CourseIds().add(courseID);
    }

    public static void setPushNotificationToken(String pushNotificationToken) {
        LoggedInUserDetailsManager.sPushNotificationToken = pushNotificationToken;
    }
}
