package com.example.galbenabu1.classscanner.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.galbenabu1.classscanner.Adapters.AlbumsAdapter;
import com.example.galbenabu1.classscanner.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Logic.Managers.AnalyticsManager.AnalyticsHelpers.AlbumEventsHelper;
import Logic.Managers.AnalyticsManager.AnalyticsManager;
import Logic.Managers.AnalyticsManager.EventParams.AlbumEventParams;
import Logic.Models.Album;
import Logic.Database.DBManager;
import Logic.Interfaces.MyConsumer;

public class ShowAlbumsActivity extends Activity {

    // If showing shared albums, need course ID to fetch them from DB.
    private static final String COURSE_ID_DATA = "course_id_data"; // Course ID for fetching course albums (only relevant if shouldShowPrivateAlbums is false.
    private static final String SHOULD_SHOW_PRIVATE_ALBUMS_DATA = "should_show_private_albums"; // Showing private albums if true, shared albums if false.
    private static final String IS_SELECTING_ALBUMS = "is_selecting_albums"; // In an album selecting mode. Returns selected albums to previous activity.
    private static final String SELECTED_ALBUMS_DATA = "selected_albums_data";

    private static final String TAG = "ShowAlbumsActivity";

    private Set<Album> mSelectedAlbumIDsSet = new HashSet<>();
    private List<Album> mAlbumsList = new ArrayList<>();
    private RecyclerView mAlbumsRecycleView;
    private AlbumsAdapter mAlbumsAdapter;
    private boolean mShouldShowPrivateAlbums;
    private boolean mIsUserSelectingPrivateAlbums;
    private String mCourseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_albums);
        Log.e(TAG, "onCreate >>");

        this.mIsUserSelectingPrivateAlbums = getIntent().getExtras().getBoolean(IS_SELECTING_ALBUMS);
        Button btnFinishedSelectingAlbums = findViewById(R.id.btnFinishSelectingAlbums);

        if(this.mIsUserSelectingPrivateAlbums) {
            // Set on click for button.
            btnFinishedSelectingAlbums.setOnClickListener(this::onFinishSelectingAlbumsClick);
        } else {
            // Make button invisible.
            btnFinishedSelectingAlbums.setVisibility(View.INVISIBLE);
        }

        mShouldShowPrivateAlbums = getIntent().getExtras().getBoolean(SHOULD_SHOW_PRIVATE_ALBUMS_DATA);
        bindUI();
        getAlbumsFromDB();

        Log.e(TAG, "onCreate <<");
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBack >>");

        super.onBackPressed();
        Intent resultIntent = new Intent();

        setResult(RESULT_CANCELED, resultIntent);
    }

    // UI

    private void bindUI() {
        mAlbumsRecycleView = findViewById(R.id.course_info_recyclerView);
        mAlbumsRecycleView.setHasFixedSize(true);
        mAlbumsRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAlbumsRecycleView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setUI() {
        // Album Recycler view
        this.mAlbumsRecycleView.getAdapter().notifyDataSetChanged();
    }

    // Data

    private void getAlbumsFromDB() {
        mAlbumsList.clear();
        mAlbumsAdapter = new AlbumsAdapter(mAlbumsList, this::onItemLongClick, mShouldShowPrivateAlbums, this.mIsUserSelectingPrivateAlbums);
        mAlbumsRecycleView.setAdapter(mAlbumsAdapter);
        fetchAlbumsFromDB();
    }

    private void fetchAlbumsFromDB() {
        Log.e(TAG, "fetchAlbumsFromDB >> Showing private albums: " + mShouldShowPrivateAlbums);

        DBManager dbManager = new DBManager();
        MyConsumer<List<Album>> onFinishFetchingAlbums = (fetchedAlbumList) -> {
            this.mAlbumsList.addAll(fetchedAlbumList);
            this.setUI();
            this.logShowAlbumsEvent();
        };

        if(mShouldShowPrivateAlbums) {
            dbManager.fetchUserPrivateAlbumsFromDB(FirebaseAuth.getInstance().getCurrentUser().getUid(), onFinishFetchingAlbums);
        } else {
            this.mCourseID = getIntent().getExtras().getString(COURSE_ID_DATA);
            dbManager.fetchCourseSharedAlbumsFromDB(this.mCourseID, onFinishFetchingAlbums);
        }
    }

    private void logShowAlbumsEvent() {
        AlbumEventsHelper.eAlbumEventType eventType;

        if(this.mShouldShowPrivateAlbums) {
            eventType = AlbumEventsHelper.eAlbumEventType.ViewPrivateAlbums;
        } else {
            eventType = AlbumEventsHelper.eAlbumEventType.ViewCourseAlbums;
        }

        AlbumEventParams albumEventParams = new AlbumEventParams();

        albumEventParams.setmNumberOfAlbums(this.mAlbumsList.size());
        albumEventParams.setmCourseID(this.mCourseID);

        AnalyticsManager.getInstance().trackAlbumEvent(eventType, albumEventParams);
    }

    public void onFinishSelectingAlbumsClick(View v) {
        Log.e(TAG, "onFinishSelectingAlbumsClick >> ");
        Intent resultIntent = new Intent();
        ArrayList<Album> albumIDsList = new ArrayList<>(this.mSelectedAlbumIDsSet); // Convert set to list so it can be sent back to the previous activity.

        resultIntent.putParcelableArrayListExtra(SELECTED_ALBUMS_DATA, albumIDsList);
        setResult(RESULT_OK, resultIntent);
        Toast.makeText(getApplicationContext(), "Successfully added albums to course", Toast.LENGTH_SHORT).show();
        finish();
    }


    public void onItemLongClick(Album selectedAlbum) {
        Log.e(TAG, "onItemLongClick >> For album with ID: " + selectedAlbum.getM_Id() + " And name: " + selectedAlbum.getM_AlbumName());
        if (this.mSelectedAlbumIDsSet.contains(selectedAlbum.getM_Id())){
            this.mSelectedAlbumIDsSet.remove(selectedAlbum.getM_Id());
        }else{
            this.mSelectedAlbumIDsSet.add(selectedAlbum);
        }
    }

    public void onShowAlbumsBackButtonClick(View v){
        finish();
    }
}
