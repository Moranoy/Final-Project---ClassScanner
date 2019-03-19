package Logic.Database;

public enum eFirebaseDBEntityTypes {
    Courses("Courses"),
    Users("Users"),
    Albums("Albums"),
    Pictures("m_Pictures"),
    SharedAlbums("SharedAlbums"),
    PrivateAlbums("PrivateAlbums"),
    UserNotifications("UserNotifications"),
    SuggestedCourses("SuggestedCourses"),
    CourseActions("CourseActions"),
    UserActions("UserActions");

    private String mReferenceName;

    String getReferenceName() {
        return mReferenceName;
    }

    eFirebaseDBEntityTypes(String referenceName) {
        mReferenceName = referenceName;
    }
}
