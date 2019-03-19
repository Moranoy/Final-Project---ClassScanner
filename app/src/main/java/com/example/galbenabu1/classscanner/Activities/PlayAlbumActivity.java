package com.example.galbenabu1.classscanner.Activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.galbenabu1.classscanner.Activities.Helpers.PlayAlbumManager;
import com.example.galbenabu1.classscanner.R;

import Logic.Managers.AnalyticsManager.AnalyticsHelpers.AlbumEventsHelper;
import Logic.Managers.AnalyticsManager.AnalyticsManager;
import Logic.Managers.AnalyticsManager.EventParams.AlbumEventParams;
import Logic.Models.Album;

public class PlayAlbumActivity extends Activity {

    private static final String ALBUM_DATA = "album_data";
    private static final String TAG = "PlayAlbumActivity";

    private Album mAlbum;
    private int mNumberOfShownImages = 0;
    private int mTotalNumberOfImagesToPresent;
    private boolean mIsPresentationInProgress;

    private ImageView mivDisplayedImage;
    private SeekBar mpbPlayAlbumProgress;
    private Button mbtnPlayButton;

    private PlayAlbumManager mPlayAlbumManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_album);

        Log.e(TAG, "onCreate >>");

        this.bindUI();
        this.init();
    }

    private void bindUI() {
        this.mivDisplayedImage = findViewById(R.id.ivDisplayCurrentImage);
        this.mpbPlayAlbumProgress = findViewById(R.id.pbAlbumPresentation);
        this.mbtnPlayButton = findViewById(R.id.btnStartPlayingAlbum);
    }

    private void init() {
        this.mAlbum = getIntent().getExtras().getParcelable(ALBUM_DATA);
        this.mTotalNumberOfImagesToPresent = this.mAlbum.getM_Pictures().size();

        this.initSeekBar();
        this.mIsPresentationInProgress = false;
        this.mbtnPlayButton.setEnabled(false); // Set play button to false until the play album manager is ready.
        this.mPlayAlbumManager = new PlayAlbumManager(this.mAlbum, this::onUpdateNextImage);
    }

    private void initSeekBar() {
        this.mpbPlayAlbumProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean isChangeFromUser) {
                Log.e(TAG, "onStart >> is presentation currently in progress. Value: " + progressValue + " Is change from user: " + isChangeFromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "onStartTrackingTouch >> current progress: " + seekBar.getProgress());
                mPlayAlbumManager.stop();
                mIsPresentationInProgress = false;
                mbtnPlayButton.setText("Start");
                mbtnPlayButton.setEnabled(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "onStopTrackingTouch >> current progress: " + seekBar.getProgress());
                handleProgressChange(seekBar.getProgress());
            }
        });
    }

    private void handleProgressChange(int newProgressValue) {
        int indexOfShownImage = (int)(((float)newProgressValue / 100.0) * this.mAlbum.getM_Pictures().size()); // Calculate which image to show.

        if(indexOfShownImage >= this.mAlbum.getM_Pictures().size()) {
            indexOfShownImage = this.mAlbum.getM_Pictures().size() - 1; // get last picture
        }

        Log.e(TAG, "handleProgressChange: progress - " +  newProgressValue + " index of shown picture - " + indexOfShownImage);
        this.mNumberOfShownImages = indexOfShownImage;
        this.mPlayAlbumManager.jumpTo(this.mNumberOfShownImages, newProgressValue);
    }

    public void onStart(View v) {
        Log.e(TAG, "onStart >> is presentation currently in progress: " + this.mIsPresentationInProgress);

        if(this.mIsPresentationInProgress) {
            // Presentation in progress, stop it.
            this.mPlayAlbumManager.reset();
            this.mbtnPlayButton.setText("Start");
            this.mpbPlayAlbumProgress.setProgress(0);
            this.mNumberOfShownImages = 0;
            this.mbtnPlayButton.setEnabled(false);
        } else {
            this.logPlayAlbumEvent();
            // Presentation not in progress, start it.
            this.mPlayAlbumManager.start();
            this.mbtnPlayButton.setText("Stop");
        }

        this.mIsPresentationInProgress = !this.mIsPresentationInProgress; // Toggle boolean.
    }

    private void logPlayAlbumEvent() {
        AlbumEventParams albumEventParams = new AlbumEventParams();

        albumEventParams.setmAlbum(this.mAlbum);

        AnalyticsManager.getInstance().trackAlbumEvent(AlbumEventsHelper.eAlbumEventType.ViewAlbumPresentation, albumEventParams);
    }

    private void onUpdateNextImage(Bitmap imageBitmap) {
        this.mbtnPlayButton.setEnabled(true);

        if(imageBitmap != null) {
            Log.e(TAG, "Showing next image");
            this.mivDisplayedImage.setImageBitmap(imageBitmap);

            if(this.mIsPresentationInProgress) {
                this.mNumberOfShownImages++;
            }
        } else {
            this.mTotalNumberOfImagesToPresent--; // One less image to show.
            Log.e(TAG, "Next image to present is null.");
        }

        int progress = this.calculateProgressPercentage();

        Log.e(TAG, "Updating progress to: " + progress);
        this.mpbPlayAlbumProgress.setProgress(progress);
    }

    private int calculateProgressPercentage() {
        float ratio = ((float)this.mNumberOfShownImages / (float)this.mTotalNumberOfImagesToPresent);
        return (int) (ratio * 100);
    }
}
