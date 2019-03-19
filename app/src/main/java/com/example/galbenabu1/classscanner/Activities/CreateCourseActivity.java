package com.example.galbenabu1.classscanner.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.galbenabu1.classscanner.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Logic.Managers.AnalyticsManager.AnalyticsHelpers.CourseEventsHelper;
import Logic.Managers.AnalyticsManager.AnalyticsManager;
import Logic.Managers.AnalyticsManager.EventParams.CourseEventParams;
import Logic.Managers.LoggedInUserDetailsManager;
import Logic.Models.Album;
import Logic.Models.Course;
import Logic.Database.DBManager;

public class CreateCourseActivity extends AppCompatActivity {

    private final static String TAG = "CreateCourseActivity";

    private static final String SHOULD_SHOW_PRIVATE_ALBUMS_DATA = "should_show_private_albums"; // Showing private albums if true, shared albums if false.
    private static final String IS_SELECTING_ALBUMS_FOR_COURSE = "is_selecting_albums"; // In an album selecting mode. Returns selected albums to previous activity.
    private static final String SELECTED_ALBUM_DATA = "selected_albums_data"; // The selected albums that return from show albums activity.
    private final static int SELECT_ALBUMS_CODE = 100; // Code to identify that the user has selected album IDs in the returning intent

    private List<Album> mAlbumIDCollection;

    // UI
    private EditText metCourseName;
    private EditText metCreatorName;
    private EditText metCreateDate;
    private EditText metCourseDescription;
    private DBManager mDBManager = new DBManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        this.getAlbumAndCourseID();
        mAlbumIDCollection=new ArrayList<>();
        this.bindUI();
        this.setUI();
    }

    private void setUI() {
        this.metCreatorName.setText(LoggedInUserDetailsManager.getsLoggedInUser().getM_UserName());
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        this.metCreateDate.setText(dateFormat.format(date));
    }

    private void getAlbumAndCourseID() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
    }

    private void bindUI(){
        this.metCourseName = findViewById(R.id.etCreateCourseName);
        this.metCourseDescription = findViewById(R.id.etCreateCourseDescription);
        this.metCreateDate = findViewById(R.id.etDate);
        this.metCreatorName = findViewById(R.id.etCreatorName);
    }

    public void onFinishCreatingCourseClick(View v) {
        Log.e(TAG, "onFinishCreatingCourse >>");

        Course newCourse = this.createNewCourse();

        this.mDBManager.addNewCourseDetailsToDBAndSetCourseID(newCourse);
        LoggedInUserDetailsManager.addCourseIDToUser(newCourse.getID());
        this.mDBManager.userJoinsCourse(LoggedInUserDetailsManager.getsLoggedInUser(), newCourse.getID());

        this.logCourseCreatedEvent(newCourse);
        int numberOfAddedAlbums = this.mAlbumIDCollection == null ? 0 : this.mAlbumIDCollection.size();
        this.logAddedAlbumsToCourseEvent(newCourse, numberOfAddedAlbums);

        // Return to home screen
        Intent homeIntent = new Intent(CreateCourseActivity.this, HomeActivity.class);
        startActivity(homeIntent);

        Toast.makeText(this, "Created Course successfully!", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onFinishCreatingCourse <<");

    }

    private void logCourseCreatedEvent(Course newCourse) {
        CourseEventParams courseEventParams = new CourseEventParams();
        courseEventParams.setmCourse(newCourse);
        AnalyticsManager.getInstance().trackCourseEvent(CourseEventsHelper.eCourseEventType.CourseCreated, courseEventParams);
    }
    private void logAddedAlbumsToCourseEvent(Course newCourse, int numberOfAddedAlbums) {
        CourseEventParams courseEventParams = new CourseEventParams();
        courseEventParams.setmCourse(newCourse);
        courseEventParams.setmNumberOfAddedAlbums(numberOfAddedAlbums);
        AnalyticsManager.getInstance().trackCourseEvent(CourseEventsHelper.eCourseEventType.AddAlbumsToCourse, courseEventParams);
    }

    public void onChooseAlbumsClick(View v) {
        Log.e(TAG, "onActionButtonClick >>");

        if(this.mAlbumIDCollection != null) {
            this.mAlbumIDCollection.clear(); // Reset previous albums collection before selecting new albums.
        }

        Intent chooseAlbumsIntent = new Intent(this.getApplicationContext(), ShowAlbumsActivity.class);
        chooseAlbumsIntent.putExtra(IS_SELECTING_ALBUMS_FOR_COURSE, true);
        chooseAlbumsIntent.putExtra(SHOULD_SHOW_PRIVATE_ALBUMS_DATA, true);
        startActivityForResult(chooseAlbumsIntent, SELECT_ALBUMS_CODE);

        Log.e(TAG, "onActionButtonClick <<");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult >>");

        if(requestCode == SELECT_ALBUMS_CODE && resultCode == RESULT_OK) {
            this.mAlbumIDCollection = data.getExtras().getParcelableArrayList(SELECTED_ALBUM_DATA);
            Log.e(TAG, "onActivityResult >> received album IDs: " + mAlbumIDCollection);
        }

        Log.e(TAG, "onActivityResult <<");
    }


    private Course createNewCourse() {
        Course newCourse = new Course();

        String courseName = this.metCourseName.getText().toString();
        String courserDescription = this.metCourseDescription.getText().toString();
        String creatorID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String courseCreatorName = this.metCreatorName.getText().toString();

        newCourse.setCreationDate(new Date());
        newCourse.setDescription(courserDescription);
        newCourse.setCourseName(courseName);
        newCourse.setCreatorName(courseCreatorName);
        newCourse.setCreatorID(creatorID);

        List<String> albumIDsList = new ArrayList<>();

        for(Album album: this.mAlbumIDCollection) {
            albumIDsList.add(album.getM_Id());
        }

        newCourse.setM_AlbumIds(albumIDsList);

        return newCourse;
    }
}
