package com.example.galbenabu1.classscanner.Activities;

import android.app.Activity;
import android.content.Intent;
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

import Logic.Enums.eDataType;
import Logic.Managers.AnalyticsManager.AnalyticsHelpers.AlbumEventsHelper;
import Logic.Managers.AnalyticsManager.AnalyticsManager;
import Logic.Managers.AnalyticsManager.EventParams.AlbumEventParams;
import Logic.Managers.LoggedInUserDetailsManager;
import Logic.Models.Album;
import Logic.Database.DBManager;
import Logic.Models.PictureAudioData;

public class CreateAlbumActivity extends Activity {

    private final static String TAG = "CreateAlbumActivity";
    private final static String NEW_ALBUM_PICTURE_AUDIO_DATA = "new_album_picture_audio_data";
    private final static String NEW_ALBUM_ID = "new_album_id";

    // New album data
    private List<PictureAudioData> mPictureAudioDataCollection;
    private String mNewAlbumID;
    private String mAlbumCreatorId;

    // UI.
    private EditText mAlbumCreator;
    private EditText mAlbumCreatoionDate;
    private EditText metAlbumName;
    private EditText metAlbumDescription;

    private DBManager mDBManager = new DBManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);
        this.mAlbumCreatorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.getNewPictureAudioDataAndAlbumID();
        this.bindUI();
        this.setUI();
    }

    private void getNewPictureAudioDataAndAlbumID() {
        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        mPictureAudioDataCollection = (List<PictureAudioData>) extras.get(NEW_ALBUM_PICTURE_AUDIO_DATA);
        mNewAlbumID = (String)extras.get(NEW_ALBUM_ID);
    }

    private void bindUI() {
        this.metAlbumName = findViewById(R.id.etCreateAlbumName);
        this.metAlbumDescription = findViewById(R.id.etCreateAlbumDescription);
        this.mAlbumCreatoionDate = findViewById(R.id.etAlbumDate);
        this.mAlbumCreator = findViewById(R.id.etAlbumCreatorName);
    }

    private void setUI() {
        this.mAlbumCreator.setText(LoggedInUserDetailsManager.getsLoggedInUser().getM_UserName());
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        this.mAlbumCreatoionDate.setText(dateFormat.format(date));
    }

    public void onFinishCreatingAlbumClick(View v) {
        Log.e(TAG, "onFinishCreatingAlbum >>");

        String albumName = metAlbumName.getText().toString();
        String albumDescription = metAlbumDescription.getText().toString();
        String albumCreatorName = this.mAlbumCreator.getText().toString();

        Album newAlbum = new Album(mNewAlbumID, albumName, new Date(), albumCreatorName, this.mAlbumCreatorId);

        newAlbum.setM_Description(albumDescription);
        newAlbum.setM_NumOfPictures(mPictureAudioDataCollection.size());
        this.setPicturesAndAudioForNewAlbum(newAlbum);

        mDBManager.addAlbumDetailsToDB(newAlbum, FirebaseAuth.getInstance().getCurrentUser().getUid());

        this.logCreateAlbumEvent(newAlbum);
        // Return to home screen
        Intent homeIntent = new Intent(CreateAlbumActivity.this, HomeActivity.class);
        startActivity(homeIntent);

        Toast.makeText(this, "Album Created Successfully!", Toast.LENGTH_SHORT).show();

        Log.e(TAG, "onFinishCreatingAlbum <<");
    }

    private void logCreateAlbumEvent(Album newAlbum) {
        AlbumEventParams albumEventParams = new AlbumEventParams();

        albumEventParams.setmAlbum(newAlbum);

        AnalyticsManager.getInstance().trackAlbumEvent(AlbumEventsHelper.eAlbumEventType.AlbumCreated, albumEventParams);
    }

    private void setPicturesAndAudioForNewAlbum(Album newAlbum) {
        PictureAudioData audioData = null;
        List<PictureAudioData> pictureDataList = new ArrayList<>();

        for(PictureAudioData data: mPictureAudioDataCollection) {
            if(data.getM_DataType().equals(eDataType.Picture)) {
                pictureDataList.add(data);
            } else {
                audioData = data;
            }
        }

        newAlbum.setM_Audio(audioData);
        newAlbum.setM_Pictures(pictureDataList);
    }

    public void onAbortAlbumCreationClick(View v) {
        Log.e(TAG, "onAbortAlbumCreationClick >>");
        this.mDBManager.removeAlbumFromDB(this.mNewAlbumID, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                true, this.mPictureAudioDataCollection);
        finish();
    }
}
