package Logic.Database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Logic.Database.DBModels.CourseActions.CourseActionData;
import Logic.Database.DBModels.CourseActions.eCourseActionType;
import Logic.Database.DBModels.UserActionData;
import Logic.Enums.eDataType;
import Logic.Models.Album;
import Logic.Models.Course;
import Logic.Interfaces.MyConsumer;
import Logic.Interfaces.MyFunction;
import Logic.Models.PictureAudioData;
import Logic.Models.User;
import Logic.Models.UserNotification;

import static Logic.Database.KeysForDBModels.*;

/**
 * Created by galbenabu1 on 08/05/2018.
 */

public class DBManager {
    private static String TAG = "DATABASE";

    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    //private classScannetGlideModule mGlideModule;

    public DBManager() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public void uploadImageToPrivateAlbum(Bitmap imageBitmap, String userId, String albumID, MyConsumer<PictureAudioData> uploadImageSuccess, Runnable uploadImageFailure) {
        Log.e(TAG, "Writing picture data to DB...");
        PictureAudioData pictureData = writePictureAudioDataToPrivateAlbumsAndGetKey(userId, albumID);

        pictureData.setM_DataType(eDataType.Picture);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        this.uploadImage(data, pictureData, uploadImageSuccess, uploadImageFailure);
    }

    private void uploadImage(byte[] imageData, PictureAudioData imageMetaData, MyConsumer<PictureAudioData> uploadImageSuccess, Runnable uploadImageFailure) {
        // Upload image
        Log.e(TAG, "Uploading image to DB...");
        StorageReference imageRef = mStorageRef.child(StorageConstants.ImagesRefString).child(imageMetaData.getM_Id());

        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnFailureListener(exception -> {
            Log.e(TAG, "Failed reading from storage.");
            uploadImageFailure.run();
        }).addOnSuccessListener(taskSnapshot -> {
            Uri downloadUrl = taskSnapshot.getDownloadUrl();
            Log.e(TAG, "Successfully read " + downloadUrl.toString() + " from storage!");
            imageMetaData.setM_Path(downloadUrl.getPath());
            uploadImageSuccess.accept(imageMetaData);
        });
    }

    private PictureAudioData writePictureAudioDataToPrivateAlbumsAndGetKey(String userId, String albumID) {
        PictureAudioData pictureData = new PictureAudioData();
        DatabaseReference privateAlbumRef = FirebaseDBReferenceGenerator.getPrivateAlbumReference(albumID, userId);
        String pictureKey = privateAlbumRef.push().getKey();

        pictureData.setM_Id(pictureKey);
        pictureData.setM_CreationDate(Calendar.getInstance().getTime());
        privateAlbumRef.child(pictureKey).setValue(pictureData);

        return pictureData;
    }

    public String getNewAlbumID(String userID) {
        DatabaseReference userPrivateAlbumsRef = FirebaseDBReferenceGenerator.getAllUserPrivateAlbumsReference(userID);

        return userPrivateAlbumsRef.push().getKey();
    }

    public void removeAlbumFromDB(String albumID, String userID, boolean isPrivateAlbum, Collection<PictureAudioData> pictureDataCollection) {
        Log.e(TAG, "Removing album with ID: " + albumID + " from DB");

        DatabaseReference albumRef;

        if (isPrivateAlbum) {
            albumRef = FirebaseDBReferenceGenerator.getPrivateAlbumReference(albumID, userID);
        } else {
            albumRef = FirebaseDBReferenceGenerator.getSharedAlbumReference(albumID, userID);
        }

        this.removePicturesFromDB(albumID, userID, isPrivateAlbum, pictureDataCollection);

        albumRef.removeValue(
                (error, dbRef) -> {
                    String errorMsg = error == null ? "" : " received code: " + error.getCode() +
                            ". and message: " + error.getMessage();
                    Log.e(TAG, "Removing album with ID: " + albumID + errorMsg);
                }
        );
    }

    //todo: delete on the end

//    public void removePictureFromDB(Album album, String userID, String pictureId, String pictureDbId, boolean isPrivateAlbum, Runnable onDeletedPictureSuccess) {
//        String albumID = album.getM_Id();
//        List<PictureAudioData> pictureCollections = new ArrayList<>();
//        PictureAudioData pictureAudioData=new PictureAudioData(pictureDbId,null,null,"Images/" + pictureId);
//        pictureCollections.add(pictureAudioData);
//
//        removePicturesFromStorage(pictureCollections);
//        int photoDbIndex=Integer.parseInt(pictureDbId);
//        album.deletePictureFromAlbum(photoDbIndex);
//
//        DatabaseReference albumPicturesRef;
//
//        if (isPrivateAlbum) {
//            albumPicturesRef = FirebaseDBReferenceGenerator.getPrivateAlbumPictureReference(albumID, userID);
//        } else {
//            albumPicturesRef = FirebaseDBReferenceGenerator.getSharedAlbumPictureReference(albumID, userID);
//        }
//
//        albumPicturesRef.setValue(album.getM_Pictures()).addOnSuccessListener(
//                (aVoid) -> onDeletedPictureSuccess.run()
//        );
//    }

    public void removePictureFromDB(Album album, String userID, String pictureId, String pictureDbId, boolean isPrivateAlbum) {
        String albumID = album.getM_Id();
        List<PictureAudioData> pictureCollections = new ArrayList<>();
        PictureAudioData pictureAudioData=new PictureAudioData(pictureDbId,null,null,"Images/" + pictureId);
        pictureCollections.add(pictureAudioData);

        removePicturesFromDB(albumID, userID, isPrivateAlbum, pictureCollections);
        album.deletePictureFromAlbum(Integer.parseInt(pictureDbId));

    }

    public void removePicturesFromDB(String albumID, String userID, boolean isPrivateAlbum, Collection<PictureAudioData> pictureCollections) {
        Log.e(TAG, "Removing pictures from DB");

        DatabaseReference picturesRef;

        if (isPrivateAlbum) {
            picturesRef = FirebaseDBReferenceGenerator.getPrivateAlbumPictureReference(albumID, userID);
        } else {
            picturesRef = FirebaseDBReferenceGenerator.getSharedAlbumPictureReference(albumID, userID);
        }

        this.removePicturesFromStorage(pictureCollections);

        Map<String, Object> deletionMap = new HashMap<>();

        for (PictureAudioData pictureData : pictureCollections) {
            deletionMap.put(pictureData.getM_Id(), null);
        }

        picturesRef.updateChildren(deletionMap).addOnSuccessListener(
                (aVoid -> Log.e(TAG, "Pictures deleted successfully"))
        ).addOnFailureListener(
                (exception) -> Log.e(TAG, "Failed deleting pictures from DB with message: " + exception.getMessage())
        );
    }

    private void removePicturesFromStorage(Collection<PictureAudioData> pictureCollection) {
        StorageReference imagesRef = mStorageRef;

        for (PictureAudioData pictureData : pictureCollection) {
            imagesRef.child(pictureData.getM_Path()).delete().addOnSuccessListener(
                    (aVoid) -> Log.e(TAG, "Successfully deleted picture with ID: " + pictureData.getM_Id() + " from storage.")
            ).addOnFailureListener(
                    (exception) -> Log.e(TAG, "Failed to delete picture with ID: " + pictureData.getM_Id() + " from storage." + System.lineSeparator() +
                            "Error message: " + exception.getMessage())
            );
        }
    }

    public void addAlbumDetailsToDB(Album newAlbum, String userID) {
        Log.e(TAG, "Adding new album details with ID: " + newAlbum.getM_Id() + " to DB");

        DatabaseReference privateAlbumRef = FirebaseDBReferenceGenerator.getPrivateAlbumReference(newAlbum.getM_Id(), userID);

        privateAlbumRef.setValue(newAlbum).addOnSuccessListener(
                (aVoid) -> Log.e(TAG, "Successfully added album details for album with ID: " + newAlbum.getM_Id())
        ).addOnFailureListener(
                (exception) -> Log.e(TAG, "failed to add album details for album with ID: " + newAlbum.getM_Id() + System.lineSeparator() +
                        "Error message: " + exception.getMessage())
        );

    }

    public void fetchUserPrivateAlbumsFromDB(String userID, MyConsumer<List<Album>> onFinishConsumer) {
        Log.e(TAG, "Fetching private albums for user with ID: " + userID);
        DatabaseReference userPrivateAlbumsRef = FirebaseDBReferenceGenerator.getAllUserPrivateAlbumsReference(userID);

        this.fetchAlbums(userPrivateAlbumsRef, userID, true, onFinishConsumer);
    }

    public void fetchCourseSharedAlbumsFromDB(String courseID, MyConsumer<List<Album>> onFinishFetchingAlbums) {
        Log.e(TAG, "Fetching private albums for user with ID: " + courseID);
        DatabaseReference courseSharedAlbumsRef = FirebaseDBReferenceGenerator.getAllCourseSharedAlbumsReference(courseID);

        fetchAlbums(courseSharedAlbumsRef, courseID, false, onFinishFetchingAlbums);
    }

    private void fetchAlbums(DatabaseReference albumsRef, String albumHolderID, boolean isPrivateAlbums, MyConsumer<List<Album>> onFinishFetchingAlbums) {
        String albumTypeString = isPrivateAlbums ? eFirebaseDBEntityTypes.PrivateAlbums.getReferenceName() :
                eFirebaseDBEntityTypes.SharedAlbums.getReferenceName();

        albumsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "Received " + albumTypeString + " for album holder with ID: " + albumHolderID);
                List<Album> albumList = new ArrayList<>();
                Album album;

                for (DataSnapshot albumSnapshot : dataSnapshot.getChildren()) {
                    album = albumSnapshot.getValue(Album.class);
                    albumList.add(album);
                }

                onFinishFetchingAlbums.accept(albumList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed fetching " + albumTypeString + " for user with ID: " + albumHolderID);
            }
        });
    }

    public void fetchFilteredCourses(MyFunction<Course, Boolean> filterFunction, MyConsumer<List<Course>> onFinishFetchingCourses) {
        Log.e(TAG, "Fetching filtered courses");
        DatabaseReference courseRef = FirebaseDBReferenceGenerator.getAllCoursesReference();

        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "Fetched courses from DB");
                List<Course> courseList = new ArrayList<>();
                Course course;

                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    course = courseSnapshot.getValue(Course.class);
                    if (filterFunction.apply(course)) {
                        courseList.add(course);
                    }
                }

                onFinishFetchingCourses.accept(courseList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed fetching courses");
            }
        });
    }

    public void fetchNumberOfNotifications(MyConsumer<Integer> onFinishedFetchingNumberOfNotifications) {
        this.fetchUserNotifications(
                (notificationsList) -> onFinishedFetchingNumberOfNotifications.accept(notificationsList.size())
        );
    }

    public void fetchUserNotifications(MyConsumer<List<UserNotification>> onFinishedFetchingUserNotifications) {
        final String USER_NOTIFICATIONS_KEY = "mUserNotifications";
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userNotificationsRef = FirebaseDBReferenceGenerator.getUserReference(userID).child(USER_NOTIFICATIONS_KEY);

        userNotificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "Fetched user notifications.");
                List<UserNotification> userNotificationsList = new ArrayList<>();
                UserNotification userNotification;

                for (DataSnapshot userNotificationsSnapshot: dataSnapshot.getChildren()) {
                    userNotification = userNotificationsSnapshot.getValue(UserNotification.class);
                    userNotificationsList.add(userNotification);
                }

                onFinishedFetchingUserNotifications.accept(userNotificationsList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed fetching user notifications. Error: " + databaseError.getMessage());
            }
        });
    }

    public void removeUserNotificationsFromDB() {
        final String USER_NOTIFICATIONS_KEY = "mUserNotifications";
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userNotificationsRef = FirebaseDBReferenceGenerator.getUserReference(userID).child(USER_NOTIFICATIONS_KEY);

        Log.e(TAG, "Removing user notifications from DB.");

        userNotificationsRef.setValue(null);
    }

    public void fetchSuggestedCourses(MyConsumer<List<Course>> onFinishFetchingCourses) {
        Log.e(TAG, "Fetching suggested courses");
        DatabaseReference suggestedCoursesReference = FirebaseDBReferenceGenerator.getSuggestedCoursesReference();

        suggestedCoursesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "Fetched courses from DB");
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                List<String> courseIDsList = (ArrayList<String>)dataSnapshot.child(userID).getValue();
                fetchCoursesForIDs(courseIDsList, onFinishFetchingCourses);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed fetching suggested courses");
            }
        });
    }

    private void fetchCoursesForIDs(List<String> courseIDsList, MyConsumer<List<Course>> onFinishFetchingCourses) {
        DatabaseReference courseRef = FirebaseDBReferenceGenerator.getAllCoursesReference();
        List<Course> updatedSuggestedCoursesList = new ArrayList<>();

        if(courseIDsList == null || courseIDsList.isEmpty()) {
            return; // No need to continue.
        }

        for(String courseID: courseIDsList) {
            courseRef.child(courseID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Course course = dataSnapshot.getValue(Course.class);
                    updatedSuggestedCoursesList.add(course);
                    onFinishFetchingCourses.accept(updatedSuggestedCoursesList); // Send the courses we've gotten so far.
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void addAlbumsToExistsCourse(String courseID, List<String> albumIDsList) {
        Log.e(TAG, "Adding Albums to course: " + courseID + " to DB");

        DatabaseReference courseRef = FirebaseDBReferenceGenerator.getCourseReference(courseID);
        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Course course = dataSnapshot.getValue(Course.class);
                course.getM_AlbumIds().addAll(albumIDsList);
                dataSnapshot.getRef().setValue(course);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addNewCourseDetailsToDBAndSetCourseID(Course newCourse) {
        Log.e(TAG, "Adding new course details with ID: " + newCourse.getID() + " to DB");

        // Set new course ID.
        DatabaseReference newCourseRef = FirebaseDBReferenceGenerator.getAllCoursesReference().push();
        String courseID = newCourseRef.getKey();

        newCourse.setId(courseID);

        updateCourseDetailsToDB(newCourse);

    }

    public void updateCourseDetailsToDB(Course course){
        DatabaseReference courseRef = FirebaseDBReferenceGenerator.getCourseReference(course.getID());
        courseRef.setValue(course).addOnSuccessListener(
                (aVoid) -> {
                    Log.e(TAG, "Successfully update course details for course with ID: " + course.getID());
                    // Remove album IDs from private albums to shared albums.
                    // Send course's creator ID as album creator ID (since those are private albums, its the same user ID).
                    this.moveAlbumIDsFromPrivateToShared(course.getID(), course.getCreatorID(), course.getM_AlbumIds());
                    CourseActionData courseActionData = new CourseActionData();
                    courseActionData.setmCourseActionType(eCourseActionType.CourseCreated);
                    courseActionData.setmCourseID(course.getID());
                    this.writeCourseAction(courseActionData);
                }
        ).addOnFailureListener(
                (exception) -> Log.e(TAG, "failed to add course details for course with ID: " + course.getID() + System.lineSeparator()));
    }


    public void moveAlbumIDsFromPrivateToShared(String courseID, String albumCreatorUserID, List<String> albumIDs) {
        Log.e(TAG, "Removing album IDs " + albumIDs + " From private albums of user with iD: " + albumCreatorUserID + " To shared albums of course with ID: " + courseID);
        DatabaseReference userPrivateAlbumsRef = FirebaseDBReferenceGenerator.getAllUserPrivateAlbumsReference(albumCreatorUserID);
        DatabaseReference courseSharedAlbumsRef = FirebaseDBReferenceGenerator.getAllCourseSharedAlbumsReference(courseID);

        for (String albumID : albumIDs) {
            userPrivateAlbumsRef.child(albumID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Album currentAlbum = dataSnapshot.getValue(Album.class);
                    if (currentAlbum == null) {
                        // Album does not exist in DB. Might be because this function is called again after removing the album the first time.
                        return;
                    }

                    Log.e(TAG, "Finished fetching album with ID: " + currentAlbum.getM_Id());

                    // Remove album from private albums
                    userPrivateAlbumsRef.child(currentAlbum.getM_Id()).removeValue(
                            (error, dbRef) -> {
                                String errorMsg = error == null ? "" : " Error message: " + error.getMessage();
                                Log.e(TAG, "Finished removing album with ID: " + albumID + errorMsg);
                            }
                    );

                    // Add album to shared albums.
                    courseSharedAlbumsRef.child(currentAlbum.getM_Id()).setValue(currentAlbum);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void moveAlbumsFromPrivateToShared(String courseID, ArrayList<Album> albumList) {
        String albumCreatorID = albumList.get(0).getM_AlbumCreatorId(); // Get the creator ID from the first album (all of the albums have the same creator).
        List<String> albumIDsList = new ArrayList<>();

        for(Album album: albumList) {
            albumIDsList.add(album.getM_Id());
        }

        this.moveAlbumIDsFromPrivateToShared(courseID, albumCreatorID, albumIDsList);
    }

    public void addUserInfoToDataBase(User loggedInUser) {
        DatabaseReference userReference = FirebaseDBReferenceGenerator.getAllUsersReference();

        userReference.child(loggedInUser.getM_Id()).setValue(loggedInUser);
    }

    public void fetchUserDetails(String userID, MyConsumer<User> onFetchedUserSuccess, Runnable onFetchedUserFailure) {
        DatabaseReference userRef = FirebaseDBReferenceGenerator.getUserReference(userID);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userInfo = dataSnapshot.getValue(User.class);

                Log.e(TAG, "Finished fetching user info with ID: " + userInfo.getM_Id());

                onFetchedUserSuccess.accept(userInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onFetchedUserFailure.run();
            }
        });
    }

    // User actions

    public void userJoinsCourse(User user, String joinedCourseID) {
        DatabaseReference userRef = FirebaseDBReferenceGenerator.getUserReference(user.getM_Id());
        final String courseIDsDatabaseKey = "m_CourseIds";
        userRef.child(courseIDsDatabaseKey).setValue(user.getM_CourseIds());

        CourseActionData courseActionData = new CourseActionData();
        courseActionData.setmCourseActionType(eCourseActionType.JoinedCourse);
        courseActionData.setmCourseID(joinedCourseID);
        courseActionData.setmUserCoursesIDs(user.getM_CourseIds());

        this.writeCourseAction(courseActionData);
    }

    public void onUserLogin(String userID, List<String> userCourseIDs, String pushNotificationToken) {
        Log.e(TAG, "Writing user log in data for use with id: " + userID);

        DatabaseReference userActionsReference = FirebaseDBReferenceGenerator.getUserActionsReference();

        UserActionData userActionData = new UserActionData();
        userActionData.setmUserID(userID);
        userActionData.setmUserCourseIDs(userCourseIDs);

        userActionsReference.child(userID).setValue(userActionData);

        if(pushNotificationToken != null) {
            this.regiterDevieToken(userID, pushNotificationToken);
        }
    }

    // Course actions

    private void writeCourseAction(CourseActionData courseActionData) {
        DatabaseReference courseActionRef = FirebaseDBReferenceGenerator.getCourseActionReference();

        String courseActionKey = courseActionRef.push().getKey();

        courseActionRef.child(courseActionKey).setValue(courseActionData);
    }

    public void uploadAudioFile(String fileName, PictureAudioData audioData) {
        this.uploadAudioToStorage(fileName, audioData);
    }

    private void uploadAudioToStorage(String fileName, PictureAudioData audioData) {
        StorageReference audioRef = this.mStorageRef.child(StorageConstants.AudioRefString).child(audioData.getM_Id() + StorageConstants.AudioFileType);
        Uri fileUri = Uri.fromFile(new File(fileName));

        audioRef.putFile(fileUri).addOnFailureListener(exception -> {
            Log.e(TAG, "Failed writting audio file to storage.");
        }).addOnSuccessListener(taskSnapshot -> {
            Uri downloadUrl = taskSnapshot.getDownloadUrl();
            Log.e(TAG, "Successfully wrote " + downloadUrl.toString() + " from storage!");
            audioData.setM_Path(downloadUrl.getPath());
        });
    }

    public PictureAudioData addRecordingToPrivateAlbum(String userID, String albumID, Date creationDate) {
        PictureAudioData audioData = this.writePictureAudioDataToPrivateAlbumsAndGetKey(userID, albumID);

        audioData.setM_DataType(eDataType.Audio);
        audioData.setM_CreationDate(creationDate);

        return audioData;
    }

    public void fetchImage(PictureAudioData pictureData, MyConsumer<Bitmap> onFinishedFetchingImages) {
        this.fetchImageFromStoragePath(pictureData.getM_Id(), onFinishedFetchingImages);
    }

    public void fetchImageFromStoragePath(String path, MyConsumer<Bitmap> onFinishedFetchingImage) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(StorageConstants.ImagesRefString).child(path);

        try {
            final File localFile = File.createTempFile(StorageConstants.TempFileName, StorageConstants.TempFileType);
            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap imageBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    Log.e(TAG, "onSuccess: image fetched from path: " + path);
                    onFinishedFetchingImage.accept(imageBitmap);
                }
            }).addOnFailureListener((OnFailureListener) e ->  {
                Log.e(TAG, "Failed fetching image from path " + path);
                onFinishedFetchingImage.accept(null);
            });
        } catch(IOException e) {
            Log.e(TAG, "Cannot open file to fetch image data to.");
        }
    }

    public void fetchRecordingDataSource(String audioFileID, MyConsumer<Uri> onFetchedRecordingDataSource) {
        FirebaseStorage.getInstance()
                .getReference(StorageConstants.AudioRefString)
                .child(audioFileID + StorageConstants.AudioFileType)
                .getDownloadUrl()
                .addOnSuccessListener(onFetchedRecordingDataSource::accept);
    }

    public void regiterDevieToken(String userID, String deviceToken) {
        final String PUSH_TOKEN = "pushToken";
        DatabaseReference pushTokenRef = FirebaseDBReferenceGenerator.getUserReference(userID).child(PUSH_TOKEN);

        Log.e(TAG, "regiterDevieToken >> writting push token " + deviceToken + " to DB");
        pushTokenRef.setValue(deviceToken);
    }

    private static class StorageConstants {
        private final static String ImagesRefString = "Images";
        private final static String AudioRefString = "Audio";
        private final static String AudioFileType = ".3gp";
        private final static String TempFileName = "Temp";
        private final static String TempFileType = "jpg";
    }
}
