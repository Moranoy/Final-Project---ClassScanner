package Logic.Models;

import Logic.Enums.eNotificationType;

public class UserNotification {
    private eNotificationType mNotificationType;
    private String mCourseName = "";
    private String mAlbumName = "";

    public UserNotification() {}

    public UserNotification(eNotificationType mNotificationType, String mCourseName, String mAlbumName) {
        this.mNotificationType = mNotificationType;
        this.mCourseName = mCourseName;
        this.mAlbumName = mAlbumName;
    }

    public eNotificationType getmNotificationType() {
        return mNotificationType;
    }

    public void setmNotificationType(eNotificationType mNotificationType) {
        this.mNotificationType = mNotificationType;
    }

    public String getmCourseName() {
        return mCourseName;
    }

    public void setmCourseName(String mCourseName) {
        this.mCourseName = mCourseName;
    }

    public String getmAlbumName() {
        return mAlbumName;
    }

    public void setmAlbumName(String mAlbumName) {
        this.mAlbumName = mAlbumName;
    }

    @Override
    public String toString() {
        return "UserNotification{" +
                "mNotificationType=" + mNotificationType +
                ", mCourseName='" + mCourseName + '\'' +
                ", mAlbumName='" + mAlbumName + '\'' +
                '}';
    }

    public String getDescription() {
        String notificationDescription = null;

        switch(this.mNotificationType) {
            case AlbumAddedToCourse:
                notificationDescription = "The album named " + this.mAlbumName + " has been added to the course " + this.mCourseName;
                break;
        }

        return notificationDescription;
    }

    public String getTitle() {
        String notificationTitle = null;

        switch(this.mNotificationType) {
            case AlbumAddedToCourse:
                notificationTitle = "Album Added";
                break;
        }

        return notificationTitle;
    }
}
