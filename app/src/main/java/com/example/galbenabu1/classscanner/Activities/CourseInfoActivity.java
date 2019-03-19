package com.example.galbenabu1.classscanner.Activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.TooltipCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.galbenabu1.classscanner.R;
import com.google.firebase.auth.FirebaseAuth;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import Logic.Managers.AnalyticsManager.AnalyticsHelpers.CourseEventsHelper;
import Logic.Managers.AnalyticsManager.AnalyticsManager;
import Logic.Managers.AnalyticsManager.EventParams.CourseEventParams;
import Logic.Managers.LoggedInUserDetailsManager;
import Logic.Models.Album;
import Logic.Models.Course;
import Logic.Database.DBManager;

public class CourseInfoActivity extends AppCompatActivity {

    private static final String SHOULD_SHOW_PRIVATE_ALBUMS_DATA = "should_show_private_albums"; // Send this flag to show albums activity so it will fetch shared albums.
    private static final String COURSE_ID_DATA = "course_id_data"; // Send the course ID to show albums activity to show the courses albums.
    private static final String COURSE_DATA = "course_data";
    private static final String TAG = "CourseInfoActivity";
    private static final String IS_SELECTING_ALBUMS = "is_selecting_albums";
    private static final String SELECTED_ALBUM_DATA = "selected_albums_data";
    private final static int SELECT_ALBUMS_CODE = 100; // Code to identify that the user has selected album IDs in the returning intent
    private static final String DESCRIPTION_STR = "Description:";

    private DBManager mDBManager = new DBManager();
    private boolean mIsInEditState;

    // Course info
    private Course mCourse;
    private EditText metCourseName;
    private EditText metCourseCreationDate;
    private EditText metCourseCreatorName;
    private EditText metCourseDescription;
    private Button mbtnAddCourseAlbum;
    private ImageView mIVEditCourseDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);

        Log.e(TAG, "onCreate >>");
        this.mCourse = getIntent().getExtras().getParcelable(COURSE_DATA);
        bindUI();
        setUI();
        setIsEditableState(false);
        Log.e(TAG, "onCreate <<");
    }

    // UI

    private void bindUI() {
        this.metCourseName = findViewById(R.id.et_course_name);
        this.metCourseCreationDate = findViewById(R.id.et_course_creation_date);
        this.metCourseCreatorName = findViewById(R.id.et_course_publisher_name);
        this.metCourseDescription = findViewById(R.id.et_course_description);
        this.mbtnAddCourseAlbum = findViewById(R.id.btnAddCourseAlbum);
        this.mIVEditCourseDetails = findViewById(R.id.ivEditCourseDetails);
    }

    private void setUI() {
        // Course info
        this.metCourseName.setText(mCourse.getCourseName());
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateStr = dateFormat.format(mCourse.getCreationDate());
        this.metCourseCreationDate.setText("Created at:" + dateStr);
        this.metCourseCreatorName.setText("Created by: " + this.mCourse.getCreatorName());
        this.metCourseDescription.setText("Description: " + this.mCourse.getDescription());
        setActionButtonUI();
        initET();
    }

    private void setActionButtonUI() {
        // Add albums to course
        if (isUserJoinedCourse()){ //check if this user joined course
            this.mbtnAddCourseAlbum.setText("Add Albums");
        }else{
            this.mbtnAddCourseAlbum.setText("Join Course");
        }

        // Can edit course details
        if (isUserTheManager()){
            this.mIVEditCourseDetails.setVisibility(View.VISIBLE);
        }else{
            this.mIVEditCourseDetails.setVisibility(View.INVISIBLE);
        }
    }


    public void onDisplayCourseAlbumsClick(View v){
        Log.e(TAG, "onShowCoursesClick >>");

        Intent showAlbumsIntent = new Intent(getApplicationContext(), ShowAlbumsActivity.class);
        showAlbumsIntent.putExtra(IS_SELECTING_ALBUMS, false);
        showAlbumsIntent.putExtra(SHOULD_SHOW_PRIVATE_ALBUMS_DATA, false); // Should fetch shared albums and not private ones.
        showAlbumsIntent.putExtra(COURSE_ID_DATA, this.mCourse.getID());
        startActivity(showAlbumsIntent);

        Log.e(TAG, "onShowCoursesClick <<");
    }

    public void onCreateInfoBackButtonClick(View v){
        finish();
    }

    public void onActionButtonClick(View v) {
        Log.e(TAG, "onActionButtonClick >>");

        if (isUserJoinedCourse()){
            addAlbumsToExistCourse();
        }else{
            joinCourse();
        }

        Log.e(TAG, "onActionButtonClick <<");
    }
    private void joinCourse(){
        LoggedInUserDetailsManager.addCourseIDToUser(this.mCourse.getID());
        this.mDBManager.userJoinsCourse(LoggedInUserDetailsManager.getsLoggedInUser(), this.mCourse.getID());
        this.setActionButtonUI();
        Toast.makeText(this, "Successfully joined to " + this.mCourse.getCourseName() + " course!", Toast.LENGTH_SHORT).show();
    }

    private void addAlbumsToExistCourse(){
        List<String> albumsIdList = this.mCourse.getM_AlbumIds();
        if(albumsIdList != null) {
            albumsIdList.clear();
        }

        Intent chooseAlbumsIntent = new Intent(this.getApplicationContext(), ShowAlbumsActivity.class);
        chooseAlbumsIntent.putExtra(SHOULD_SHOW_PRIVATE_ALBUMS_DATA, true);
        chooseAlbumsIntent.putExtra(IS_SELECTING_ALBUMS, true);
        startActivityForResult(chooseAlbumsIntent, SELECT_ALBUMS_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult >>");

        if (requestCode == SELECT_ALBUMS_CODE && resultCode == RESULT_OK) {
            ArrayList<Album> albumList = data.getExtras().getParcelableArrayList(SELECTED_ALBUM_DATA);
            Log.e(TAG, "onActivityResult >> received album IDs: " + albumList);
            Log.e(TAG, "onActivityResult >> Adding album IDs to existing course IDs: " + this.mCourse.getM_AlbumIds());
            this.addAlbumsToCourse(albumList);
            this.mDBManager.moveAlbumsFromPrivateToShared(this.mCourse.getID(), albumList);

            this.logAddedAlbumsToCourseEvent(albumList.size());
        }

        Log.e(TAG, "onActivityResult <<");
    }

    private void addAlbumsToCourse(ArrayList<Album> albumList) {
        List<String> albumIDs = new ArrayList<>();

        for(Album album: albumList) {
            albumIDs.add(album.getM_Id());
        }

        this.mDBManager.addAlbumsToExistsCourse(this.mCourse.getID(), albumIDs);
        this.mCourse.getM_AlbumIds().addAll(albumIDs);
    }

    private void logAddedAlbumsToCourseEvent(int numberOfAddedAlbums) {
        CourseEventParams courseEventParams = new CourseEventParams();
        courseEventParams.setmCourse(this.mCourse);
        courseEventParams.setmNumberOfAddedAlbums(numberOfAddedAlbums);
        AnalyticsManager.getInstance().trackCourseEvent(CourseEventsHelper.eCourseEventType.AddAlbumsToCourse, courseEventParams);
    }

    private boolean isUserJoinedCourse() {
        return isUserTheManager() ||
                LoggedInUserDetailsManager.getsLoggedInUser().getM_CourseIds().contains(this.mCourse.getID());
    }

    private boolean isUserTheManager() {
        return this.mCourse.getCreatorID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public void onEditCourseDetailsClick(View v){
        Log.e(TAG, "onEditableState >>");

        if (this.mIsInEditState){ //finish to edit - save changes
            setIsEditableState(false);
            this.mIVEditCourseDetails.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.edit));
            this.mCourse.setCourseName(this.metCourseName.getText().toString());
            this.mCourse.setDescription(getSubDescription());
            this.mDBManager.updateCourseDetailsToDB(this.mCourse);
            TooltipCompat.setTooltipText(this.mIVEditCourseDetails,"Edit details");
        } else{ //start to edit
            setIsEditableState(true);
            this.mIVEditCourseDetails.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.oksign));
            TooltipCompat.setTooltipText(this.mIVEditCourseDetails,"Save Changes");
        }

        Log.e(TAG, "onEditableState <<");
    }

    public void setIsEditableState(boolean isEditable){
        this.mIsInEditState = isEditable;
        disableEditText(this.metCourseDescription, isEditable);
        disableEditText(this.metCourseName, isEditable);
    }

    private void disableEditText(EditText editText, boolean isDisable) {
        editText.setEnabled(isDisable);
    }

    private void initET() {
        disableEditText(this.metCourseName, false);
        disableEditText(this.metCourseCreationDate, false);
        disableEditText(this.metCourseDescription, false);
        disableEditText(this.metCourseCreatorName, false);
    }

    private String getSubDescription(){
        if (this.metCourseDescription.getText().toString().contains(DESCRIPTION_STR)){
            String[] subString = this.metCourseDescription.getText().toString().split(DESCRIPTION_STR);
            System.out.println(subString[1]);
            return subString[1];
        }else{
            return this.metCourseDescription.getText().toString();
        }
    }

}
