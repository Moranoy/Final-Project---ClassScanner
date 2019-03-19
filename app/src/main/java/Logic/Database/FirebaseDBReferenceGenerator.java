package Logic.Database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Helper class for DB manager - Generates database references
class FirebaseDBReferenceGenerator {
    private static final DatabaseReference sfDatabaseRoot = FirebaseDatabase.getInstance().getReference();

    public static DatabaseReference getAllCoursesReference() {
        return sfDatabaseRoot.child(eFirebaseDBEntityTypes.Courses.getReferenceName());
    }

    public static DatabaseReference getCourseReference(String courseID) {
        return getAllCoursesReference().child(courseID);
    }

    public static DatabaseReference getAllUsersReference() {
        return sfDatabaseRoot.child(eFirebaseDBEntityTypes.Users.getReferenceName());
    }

    public static DatabaseReference getUserReference(String userID) {
        return getAllUsersReference().child(userID);
    }

    public static DatabaseReference getSuggestedCoursesReference() {
        return sfDatabaseRoot.child(eFirebaseDBEntityTypes.SuggestedCourses.getReferenceName());
    }

    public static DatabaseReference getAllSharedAlbumsReference() {
        return getAlbumsReference().child(eFirebaseDBEntityTypes.SharedAlbums.getReferenceName());
    }

    public static DatabaseReference getAllCourseSharedAlbumsReference(String courseID) {
        return getAllSharedAlbumsReference().child(courseID);
    }

    public static DatabaseReference getSharedAlbumReference(String albumID, String courseID) {
        return getAllCourseSharedAlbumsReference(courseID).child(albumID);
    }

    public static DatabaseReference getAllPrivateAlbumsReference() {
        return getAlbumsReference().child(eFirebaseDBEntityTypes.PrivateAlbums.getReferenceName());
    }

    public static DatabaseReference getAllUserPrivateAlbumsReference(String userID) {
        return getAllPrivateAlbumsReference().child(userID);
    }

    public static DatabaseReference getPrivateAlbumReference(String albumID, String userID) {
        return getAllUserPrivateAlbumsReference(userID).child(albumID);
    }

    private static DatabaseReference getAlbumsReference() {
        return sfDatabaseRoot.child(eFirebaseDBEntityTypes.Albums.getReferenceName());
    }

    public static DatabaseReference getPrivateAlbumPictureReference(String albumID, String userID) {
        return getPrivateAlbumReference(albumID, userID).child(eFirebaseDBEntityTypes.Pictures.getReferenceName());
    }

    public static DatabaseReference getSharedAlbumPictureReference(String albumID, String courseID) {
        return getSharedAlbumReference(albumID, courseID).child(eFirebaseDBEntityTypes.Pictures.getReferenceName());
    }

    public static DatabaseReference getUserActionsReference() {
        return sfDatabaseRoot.child(eFirebaseDBEntityTypes.UserActions.getReferenceName());
    }

    public static DatabaseReference getCourseActionReference() {
        return sfDatabaseRoot.child(eFirebaseDBEntityTypes.CourseActions.getReferenceName());
    }
}
