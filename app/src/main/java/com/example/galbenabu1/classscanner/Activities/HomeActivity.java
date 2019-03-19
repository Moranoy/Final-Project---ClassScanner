package com.example.galbenabu1.classscanner.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.galbenabu1.classscanner.Activities.Enums.eShowCoursesOptions;
import com.example.galbenabu1.classscanner.R;
import com.google.firebase.auth.FirebaseAuth;

import Logic.Database.DBManager;
import Logic.Managers.AnalyticsManager.AnalyticsManager;

public class HomeActivity extends Activity {

    private static final String TAG = "HomeActivity";

    private static final String SHOULD_SHOW_PRIVATE_ALBUMS_DATA = "should_show_private_albums";
    private static final String IS_SELECTING_ALBUMS = "is_selecting_albums"; // In an album selecting mode. Returns selected albums to previous activity.
    // Decides which courses will be displayed in the ShowCoursesActivity
    private static final String SHOW_COURSES_OPTIONS = "show_courses_options";

    private TextView mtvGreeting;
    Button mbtnNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "onCreate >>");

        mtvGreeting = findViewById(R.id.tvGreeting);
        setContentView(R.layout.activity_home);

        AnalyticsManager.getInstance().init(getApplicationContext()); // Init analytics

        this.mbtnNotifications = findViewById(R.id.btnNotifications);

        Log.e(TAG, "onCreate <<");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume >>");

        DBManager dbManager = new DBManager();
        dbManager.fetchNumberOfNotifications(this::onFinishedFetchingNumberOfNotifications);
    }

    private void onFinishedFetchingNumberOfNotifications(int numberOfNotifications) {
        Log.e(TAG, "onFinishedFetchingNumberOfNotifications >> number of notifications: " + numberOfNotifications);

        if(numberOfNotifications > 0) {
            mbtnNotifications.setText("Notifications (" + numberOfNotifications + ")");
        }
    }

    public void onSignoutClick(View v) {
        Log.e(TAG, "onSignoutClick >>");
        FirebaseAuth.getInstance().signOut();
        Intent mainViewIntent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(mainViewIntent);
        Log.e(TAG, "onSignoutClick <<");
    }

    public void onTmpTakePicClick(View v) {
        Log.e(TAG, "onTmpTakePicClick >>");
        Intent intent = new Intent(HomeActivity.this, TakePicActivity.class);
        startActivity(intent);
        Log.e(TAG, "onTmpTakePicClick <<");
    }

    public void onCreateNewCourseClick(View v){
        Log.e(TAG, "onCreateNewCourseClick >>");

        Intent intent = new Intent(HomeActivity.this, CreateCourseActivity.class);
        startActivity(intent);

        Log.e(TAG, "onCreateNewCourseClick <<");
    }

    public void onViewMyCoursesClick(View v) {
        Log.e(TAG, "onViewMyCoursesClick >>");
        Intent intent = new Intent(HomeActivity.this, ShowCoursesActivity.class);
        intent.putExtra(SHOW_COURSES_OPTIONS, eShowCoursesOptions.ShowCoursesTheCurrentUserIsIn);
        startActivity(intent);
        Log.e(TAG, "onViewMyCoursesClick <<");
    }

    public void onShowPrivateAlbumsClick(View v) {
        Log.e(TAG, "onShowPrivateAlbumsClick >>");

        Intent intent = new Intent(HomeActivity.this, ShowAlbumsActivity.class);
        intent.putExtra(IS_SELECTING_ALBUMS, false);
        intent.putExtra(SHOULD_SHOW_PRIVATE_ALBUMS_DATA, true);
        startActivity(intent);

        Log.e(TAG, "onShowPrivateAlbumsClick <<");
    }

    public void onSearchCoursesClick(View v) {
        Log.e(TAG, "onSearchCoursesClick >>");

        Intent intent = new Intent(HomeActivity.this, ShowCoursesActivity.class);
        intent.putExtra(SHOW_COURSES_OPTIONS, eShowCoursesOptions.ShowSearchedCourses);
        startActivity(intent);

        Log.e(TAG, "onSearchCoursesClick <<");
    }

    public void onSuggestedCoursesClick(View v) {
        Log.e(TAG, "onSuggestedCoursesClick >>");

        Intent intent = new Intent(HomeActivity.this, ShowCoursesActivity.class);
        intent.putExtra(SHOW_COURSES_OPTIONS, eShowCoursesOptions.ShowSuggestedCourses);
        startActivity(intent);

        Log.e(TAG, "onSuggestedCoursesClick <<");
    }

    public void onNotificationsClick(View v) {
        Log.e(TAG, "onNotificationsClick>>");

        Intent intent = new Intent(HomeActivity.this, UserNotificationsActivity.class);
        startActivity(intent);

        Log.e(TAG, "onNotificationsClick <<");
    }
}
